package com.vl.satellitelivewallpaper.domain.boundary

import com.vl.satellitelivewallpaper.domain.entity.Facet
import com.vl.satellitelivewallpaper.domain.entity.Material
import com.vl.satellitelivewallpaper.domain.entity.Vertex

interface Painter {
    fun prepare(facets: List<Facet>): Painting
    fun moved(vector: Vertex, block: () -> Unit)
    fun rotated(degree: Float, vector: Vertex, block: () -> Unit)
    fun scaled(size: Float, block: () -> Unit)
}