package com.vl.satellitelivewallpaper.domain.boundary

import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Vertex

interface Scene {

    var isLightEnabled: Boolean

    fun clear(color: Color)
    fun setBounds(x: Int, y: Int, width: Int, height: Int)

    /**
     * Define x, y, z areas of visible space
     */
    fun setCamera(
        left: Float = -1f,
        right: Float = 1f,
        bottom: Float = -1f,
        top: Float = 1f,
        near: Float = -1f,
        far: Float = 1f
    )

    fun setLight(position: Vertex)
}