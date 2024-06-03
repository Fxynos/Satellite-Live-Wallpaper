package com.vl.satellitelivewallpaper.domain.manager

import android.util.Log
import com.vl.satellitelivewallpaper.domain.boundary.Scene
import com.vl.satellitelivewallpaper.domain.boundary.Painter
import com.vl.satellitelivewallpaper.domain.boundary.Painting
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Material
import com.vl.satellitelivewallpaper.domain.entity.Model
import com.vl.satellitelivewallpaper.domain.entity.Vertex
import java.util.LinkedList

class GraphicsManager(
    val painter: Painter,
    val scene: Scene
) {
    companion object {
        private const val TAG = "GraphicsManager"
        private val MATERIAL_DEFAULT = Material("default",
            Color("#AAAAAA"),
            Color("#EEEEEE"),
            Color("#FFFFFF")
        )
    }

    private val cache = HashMap<Model, List<Painting>>()

    fun draw(model: Model) {
        if (!cache.containsKey(model))
            cache[model] = (0 until model.facetsCount).asSequence().map {
                model.getFacet(it).run {
                    painter.prepare(
                        material ?: MATERIAL_DEFAULT,
                        triangulate(vertices),
                        textureMap,
                        normals
                    )
                }
            }.toCollection(LinkedList()).also {
                Log.d(TAG, "Cached model with ${it.size} facets")
            }

        cache[model]!!.forEach(Painting::paint)
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