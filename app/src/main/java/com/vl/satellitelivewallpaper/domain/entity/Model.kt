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
        const val VERTEX_TEXTURE_NONE = 0

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

    /**
     * @param position starts with 1
     */
    fun getFacet(position: Int): Facet {
        val frames = rawFacets[position - 1].split(3)

        val vertices = frames[0].mapToObj(this::getVertex)
            .collect(Collectors.toList()).toTypedArray()
        val textureMap = frames[1].mapToObj(this::getTextureVertex)
            .collect(Collectors.toList()).toTypedArray()
        val normals = frames[2].mapToObj(this::getNormal)
            .collect(Collectors.toList()).toTypedArray()

        return Facet(materials[position - 1], vertices, textureMap, normals)
    }

    private fun getVertex(position: Int): Vertex {
        val startIndex = (position - 1) * 4
        return Vertex(
            rawVertices[startIndex],
            rawVertices[startIndex + 1],
            rawVertices[startIndex + 2]
        ) // `w` is omitted
    }

    private fun getTextureVertex(position: Int): Vertex {
        val startIndex = (position - 1) * 3
        return Vertex(
            rawTextureMap[startIndex],
            rawTextureMap[startIndex + 1],
            rawTextureMap[startIndex + 2]
        )
    }

    private fun getNormal(position: Int): Vertex {
        val startIndex = (position - 1) * 3
        return Vertex(
            rawNormals[startIndex],
            rawNormals[startIndex + 1],
            rawNormals[startIndex + 2]
        )
    }
}