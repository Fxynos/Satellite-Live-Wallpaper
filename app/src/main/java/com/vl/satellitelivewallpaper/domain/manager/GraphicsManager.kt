package com.vl.satellitelivewallpaper.domain.manager

import com.vl.satellitelivewallpaper.domain.boundary.Scene
import com.vl.satellitelivewallpaper.domain.boundary.Painter
import com.vl.satellitelivewallpaper.domain.boundary.Painting
import com.vl.satellitelivewallpaper.domain.entity.Model
import java.util.LinkedList

class GraphicsManager(
    val painter: Painter,
    val scene: Scene
) {
    private val cache = HashMap<Model, Painting>()

    fun draw(model: Model) {
        if (!cache.containsKey(model))
            cache[model] = painter.prepare(
                (0 until model.facetsCount)
                    .asSequence()
                    .map(model::getFacet)
                    .toCollection(LinkedList())
            )

        cache[model]!!.paint()
    }
}