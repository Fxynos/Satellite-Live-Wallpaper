package com.vl.satellitelivewallpaper.domain.boundary

import com.vl.satellitelivewallpaper.domain.entity.Color

interface Canvas {
    fun clear(color: Color)
    fun setBounds(x: Int, y: Int, width: Int, height: Int)
}