package com.vl.satellitelivewallpaper.domain.manager

import com.vl.satellitelivewallpaper.domain.boundary.Canvas
import com.vl.satellitelivewallpaper.domain.boundary.Painter
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Model

class GraphicsManager(val painter: Painter, val canvas: Canvas) {

    fun draw(model: Model) {
        repeat(model.facetsCount) {
            model.getFacet(it).apply {
                painter.paint(
                    material?.diffuseColor ?: Color("#000000"),
                    vertices,
                    normals.first()
                )
            }
        }
    }
}