package com.vl.satellitelivewallpaper.domain.boundary

import com.vl.satellitelivewallpaper.domain.entity.Material
import com.vl.satellitelivewallpaper.domain.entity.Vertex

interface Painter {

    fun prepare(
        material: Material,
        vertices: Array<Vertex>,
        textureMap: Array<Vertex>?,
        normals: Array<Vertex>
    ): Painting

    fun moved(vector: Vertex, block: () -> Unit)
    fun rotated(degree: Float, vector: Vertex, block: () -> Unit)
    fun scaled(size: Float, block: () -> Unit)
}