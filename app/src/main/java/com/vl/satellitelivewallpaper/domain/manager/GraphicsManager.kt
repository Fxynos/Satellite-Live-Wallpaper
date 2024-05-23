package com.vl.satellitelivewallpaper.domain.manager

import com.vl.satellitelivewallpaper.domain.boundary.Scene
import com.vl.satellitelivewallpaper.domain.boundary.Painter
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Material
import com.vl.satellitelivewallpaper.domain.entity.Model
import com.vl.satellitelivewallpaper.domain.entity.Vertex
import java.util.LinkedList

class GraphicsManager(
    val painter: Painter,
    val scene: Scene,
    private val triangulateFacets: Boolean = false,
    private val negateNormals: Boolean = false
) {
    companion object {
        private val MATERIAL_DEFAULT = Material("default",
            Color("#AAAAAA"),
            Color("#EEEEEE"),
            Color("#FFFFFF")
        )
    }

    fun draw(model: Model) {
        repeat(model.facetsCount) {
            model.getFacet(it).apply {
                painter.paint(
                    material ?: MATERIAL_DEFAULT,
                    if (triangulateFacets) triangulate(vertices) else vertices,
                    textureMap,
                    if (negateNormals) normals.map(Vertex::reflect).toTypedArray() else normals
                )
            }
        }
    }

    /**
     * Split convex facet into triangles
     * @param vertices facet vertices in their connections order
     * @return triangles vertices `v11 v12 v13 v21 v22 v23 ...`
     */
    private fun triangulate(vertices: Array<Vertex>): Array<Vertex> { // TODO [tva] non-convex facets support
        val triangles = LinkedList<Int>()
        var step = 1
        var pointer = 0
        do {
            var wasTriangle = false
            while (pointer + 2 * step <= vertices.size) {
                wasTriangle = true
                triangles.addAll(listOf(
                    (pointer + vertices.size) % vertices.size, // the first or last vertex of the facet
                    pointer + step,
                    (pointer + 2 * step) % vertices.size // the first or last vertex of the facet
                ))
                pointer += 2 * step
            }
            step++
            pointer -= vertices.size // sets to 0 or -1
        } while (wasTriangle)
        return triangles.map(vertices::get).toTypedArray()
    }
}