package com.vl.satellitelivewallpaper.domain.entity

import java.util.stream.Collectors
import java.util.stream.IntStream

class Model(
    private val rawVertices: FloatArray, // x y z w=1
    private val rawTextureMap: FloatArray, // u v=0 w=0
    private val rawNormals: FloatArray, // i j k
    private val rawFacets: Array<IntArray>, // v vt=0 vn
    private val materials: Array<Material?>
) {
    companion object {
        const val VERTEX_TEXTURE_NONE = -1

        private fun IntArray.split(frameSize: Int) = Array(frameSize) { i ->
            IntStream.range(0, size)
                .filter { it % frameSize == i }
                .map(this::get)
        }

        private fun FloatArray.split(frameSize: Int) = Array(frameSize) { i ->
            IntStream.range(0, size)
                .filter { it % frameSize == i }
                .mapToDouble { get(it).toDouble() } // there's no freaking FloatStream
        }
    }

    init {
        require(rawVertices.size % 4 == 0)
        require(rawTextureMap.size % 3 == 0)
        require(rawNormals.size % 3 == 0)
        require(rawFacets.size == materials.size)
    }

    val facetsCount: Int
        get() = rawFacets.size

    fun getFacet(index: Int): Facet {
        val frames = rawFacets[index].split(3)

        val vertices = frames[0].mapToObj(this::getVertex)
            .collect(Collectors.toList()).toTypedArray()

        // if the first texel is omitted, there are no texels at all
        val textureMap = if (rawFacets[index][1] == VERTEX_TEXTURE_NONE) null else
            frames[1].mapToObj(this::getTextureVertex)
                .collect(Collectors.toList()).toTypedArray()

        val normals = frames[2].mapToObj(this::getNormal)
            .collect(Collectors.toList()).toTypedArray()

        return Facet(materials[index], vertices, textureMap, normals)
    }

    private fun getVertex(index: Int): Vertex {
        val startIndex = index * 4
        return Vertex(
            rawVertices[startIndex],
            rawVertices[startIndex + 1],
            rawVertices[startIndex + 2]
        ) // `w` is omitted
    }

    private fun getTextureVertex(index: Int): Vertex {
        val startIndex = index * 3
        return Vertex(
            rawTextureMap[startIndex],
            rawTextureMap[startIndex + 1],
            rawTextureMap[startIndex + 2]
        )
    }

    private fun getNormal(index: Int): Vertex {
        val startIndex = index * 3
        return Vertex(
            rawNormals[startIndex],
            rawNormals[startIndex + 1],
            rawNormals[startIndex + 2]
        )
    }
}