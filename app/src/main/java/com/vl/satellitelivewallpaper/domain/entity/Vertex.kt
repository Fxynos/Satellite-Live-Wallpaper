package com.vl.satellitelivewallpaper.domain.entity

class Vertex(
    val x: Float,
    val y: Float,
    val z: Float
) {
    operator fun component1() = x
    operator fun component2() = y
    operator fun component3() = z
}