package com.vl.satellitelivewallpaper.domain.boundary

import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Vertex

interface Painter {
    fun paint(color: Color, vertices: Array<Vertex>, normalVector: Vertex)
    fun moved(vector: Vertex, block: () -> Unit)
    fun rotated(degree: Float, vector: Vertex, block: () -> Unit)
    fun scaled(size: Float, block: () -> Unit)
}