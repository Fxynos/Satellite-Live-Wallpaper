package com.vl.satellitelivewallpaper.data

import com.vl.satellitelivewallpaper.domain.boundary.Painter
import com.vl.satellitelivewallpaper.domain.boundary.Painting
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Facet
import com.vl.satellitelivewallpaper.domain.entity.Material
import com.vl.satellitelivewallpaper.domain.entity.Vertex
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.LinkedList
import javax.microedition.khronos.opengles.GL10

class GLPainter(private val gl: GL10): Painter {
    companion object {
        private val MATERIAL_DEFAULT = Material("default",
            Color("#AAAAAA"),
            Color("#EEEEEE"),
            Color("#FFFFFF")
        )

        private fun allocateFloatBuffer(array: FloatArray) = ByteBuffer
            .allocateDirect(array.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(array)
            .position(0)

        private fun Array<Vertex>.flatMapCoordinates() = FloatArray(size * 3) {
            val (x, y, z) = this[it / 3]
            arrayOf(x, y, z)[it % 3]
        }

        /**
         * Split convex facet into triangles
         * @return triangles vertices `v11 v12 v13 v21 v22 v23 ...`
         */
        private fun triangulate(verticesCount: Int): List<Int> { // TODO [tva] non-convex facets support
            val triangles = LinkedList<Int>()
            var step = 1
            do {
                var wasTriangle = false
                for (vertex in 0 until (verticesCount - 2 * step) step (2 * step)) {
                    wasTriangle = true
                    triangles.addAll(listOf(
                        vertex,
                        vertex + step,
                        (vertex + 2 * step) % verticesCount // the first or last vertex of the facet
                    ))
                }
                step *= 2
            } while (wasTriangle)
            return triangles
        }
    }

    init {
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glEnable(GL10.GL_NORMALIZE) // otherwise colors would be changed with scaling
    }

    override fun prepare(facets: List<Facet>): Painting {
        val triangles = facets.map { triangulate(it.vertices.size) }

        val verticesBuffer = triangles.flatMapIndexed { facet, vertices ->
            vertices.map { vertex -> facets[facet].vertices[vertex] }
        }.toTypedArray()
            .flatMapCoordinates()
            .let(GLPainter::allocateFloatBuffer)

        val normalsBuffer = triangles.flatMapIndexed { facet, vertices ->
            vertices.map { vertex -> facets[facet].normals[vertex] }
        }.toTypedArray()
            .flatMapCoordinates()
            .let(GLPainter::allocateFloatBuffer)

        return Painting {
            gl.apply {
                glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer)
                glNormalPointer(3, GL10.GL_FLOAT, normalsBuffer)
                glEnableClientState(GL10.GL_VERTEX_ARRAY)

                var pointer = 0
                facets.forEach { facet ->
                    val verticesCount = facet.vertices.size
                    applyMaterial(facet.material ?: MATERIAL_DEFAULT)
                    glDrawArrays(GL10.GL_TRIANGLES, pointer, verticesCount)
                    pointer += verticesCount
                }

                glDisableClientState(GL10.GL_VERTEX_ARRAY)
            }
        }
    }

    override fun moved(vector: Vertex, block: () -> Unit) = transformed {
        glTranslatef(vector.x, vector.y, vector.z)
        block()
    }

    override fun rotated(degree: Float, vector: Vertex, block: () -> Unit) = transformed {
        glRotatef(degree, vector.x, vector.y, vector.z)
        block()
    }

    override fun scaled(size: Float, block: () -> Unit) = transformed {
        glScalef(size, size, size)
        block()
    }

    private fun applyMaterial(material: Material, textureMap: Array<Vertex>? = null) { // TODO textures support
        gl.apply {
            glColor4f( // if lighting disabled
                material.diffuseColor.red,
                material.diffuseColor.green,
                material.diffuseColor.blue,
                material.diffuseColor.alpha
            )

            // TODO find out why GL_FRONT and GL_BACK aren't working even if normals are reflected
            applyMaterialColor(GL10.GL_DIFFUSE, material.diffuseColor)
            applyMaterialColor(GL10.GL_AMBIENT, material.ambientColor)
            applyMaterialColor(GL10.GL_SPECULAR, material.specularColor)

            if (material.texture != null) {
                glActiveTexture(GL10.GL_TEXTURE0)
                glBindTexture(GL10.GL_TEXTURE_2D, material.texture)
                glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
                glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
                glTexCoordPointer(3, GL10.GL_FLOAT, 0, allocateFloatBuffer(textureMap!!.flatMapCoordinates()))
            }
        }
    }

    private fun applyMaterialColor(glColor: Int, color: Color) {
        gl.glMaterialfv(
            GL10.GL_FRONT_AND_BACK, glColor,
            floatArrayOf(color.red, color.green, color.blue, color.alpha), 0
        )
    }

    private inline fun transformed(block: GL10.() -> Unit) {
        gl.apply {
            glMatrixMode(GL10.GL_MODELVIEW)
            glPushMatrix()
            block()
            glPopMatrix()
        }
    }
}