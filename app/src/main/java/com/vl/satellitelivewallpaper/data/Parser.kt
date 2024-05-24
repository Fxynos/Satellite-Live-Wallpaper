package com.vl.satellitelivewallpaper.data

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import android.util.Log
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Material
import com.vl.satellitelivewallpaper.domain.entity.Model
import java.io.InputStreamReader
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.util.LinkedList
import javax.microedition.khronos.opengles.GL10

/**
 * Parser for Wavefront OBJ and MTL files
 */
class Parser(private val context: Context, private val gl: GL10) {
    companion object {
        private const val TAG = "ModelParser"

        private fun Sequence<String>.parseCommands() =
            map { it.split('#', limit = 2).first() }
                .filter(String::isNotBlank)
                .map { line ->
                    val (operator, args) = line
                        .split(' ', limit = 2)
                        .map(String::trim)
                    operator to args
                }

        private fun <T> Iterator<T>.nextOr(default: T) =
            if (hasNext()) next() else default
    }

    fun parseObjModel(objAsset: String) = InputStreamReader(context.assets.open(objAsset)).useLines { lines ->
        val materials = LinkedList<Material>()
        val rawVertices = LinkedList<Float>()
        val rawTextureMap = LinkedList<Float>()
        val rawNormals = LinkedList<Float>()
        val rawFacets = LinkedList<IntArray>()
        val facetMaterials = LinkedList<Material?>()
        var currentMaterial: Material? = null

        lines.parseCommands().forEach { (operator, args) ->
            when (operator) {
                /* Vertex Data */

                "v" -> args
                    .splitToSequence(' ')
                    .filter(String::isNotBlank)
                    .map(::parseFloat)
                    .iterator()
                    .apply {
                        repeat(4) {
                            rawVertices.add(if (it < 3) next() else nextOr(1f))
                        }
                    }

                "vt" -> args
                    .splitToSequence(' ')
                    .filter(String::isNotBlank)
                    .map(::parseFloat)
                    .iterator()
                    .apply {
                        repeat(3) { i ->
                            rawTextureMap.add(if (i == 0) next() else nextOr(0f))
                        }
                    }

                "vn" -> args
                    .splitToSequence(' ')
                    .filter(String::isNotBlank)
                    .map(::parseFloat)
                    .iterator()
                    .apply {
                        repeat(3) { rawNormals.add(next()) }
                    }

                /* Elements */

                "f" -> args
                    .splitToSequence(' ')
                    .map(String::trim)
                    .filter(String::isNotEmpty)
                    .map { it.split('/', limit = 3) }
                    .flatMap {
                        sequenceOf(
                            parseInt(it[0]),
                            it[1].run { if (isEmpty()) 0 else parseInt(this) },
                            parseInt(it[2])
                        ).map(Int::dec) // OBJ indices start with 1
                    }.toCollection(ArrayList())
                    .toIntArray()
                    .apply {
                        rawFacets.add(this)
                        facetMaterials.add(currentMaterial)
                    }

                /* Display Attributes */

                "mtllib" -> materials.addAll(parseMaterialLibrary(args))

                "usemtl" -> {
                    val name = args.trim()
                    currentMaterial = materials.find { it.name == name }
                    if (currentMaterial == null)
                        Log.w(TAG, "Couldn't find material \"$name\"")
                }

                else -> Log.w(TAG, "Skip OBJ operator \"$operator\" with args \"$args\"")
            }
        }

        materials.forEach {
            println("${it.name} ${it.diffuseColor.red} ${it.diffuseColor.green} ${it.diffuseColor.blue}")
        }

        Model(
            rawVertices.toFloatArray(),
            rawTextureMap.toFloatArray(),
            rawNormals.toFloatArray(),
            rawFacets.toTypedArray(),
            facetMaterials.toTypedArray()
        )
    }

    private fun parseMaterialLibrary(mtlAsset: String) = InputStreamReader(context.assets.open(mtlAsset)).useLines { lines ->
        val defaultMaterial = Material("default", Color(0,0,0), Color(0,0,0), Color(0,0,0))
        lines.parseCommands().scan(LinkedList<Material>()) { materials, (operator, args) ->
            when (operator) {
                "newmtl" -> materials.add(defaultMaterial.copy(name = args))

                "Ka", "Kd", "Ks" -> {
                    val color = args
                        .splitToSequence(' ')
                        .filter(String::isNotEmpty)
                        .map(::parseFloat)
                        .iterator()
                        .run { Color(next(), next(), next()) }

                    materials[materials.lastIndex] = when (operator) {
                        "Ka" -> materials.last.copy(ambientColor = color)
                        "Kd" -> materials.last.copy(diffuseColor = color)
                        "Ks" -> materials.last.copy(specularColor = color)
                        else -> throw RuntimeException() // unreachable
                    }
                }

                "map_Kd" -> materials[materials.lastIndex] =
                    materials.last.copy(texture = bindTexture(args))

                else -> Log.w(TAG, "Skip MTL operator \"$operator\" with args \"$args\"")
            }
            materials
        }.last().toTypedArray()
    }

    /**
     * Immediately bind texture to OpenGL
     */
    private fun bindTexture(textureAsset: String): Int {
        val bitmap = BitmapFactory.decodeStream(context.assets.open(textureAsset))
        val textureId = IntArray(1)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glGenTextures(1, textureId, 0)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId.first())
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        return textureId.first()
    }
}