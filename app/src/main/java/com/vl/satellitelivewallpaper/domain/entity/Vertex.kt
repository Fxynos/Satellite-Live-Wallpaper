package com.vl.satellitelivewallpaper.domain.entity

data class Vertex(
    val x: Float,
    val y: Float,
    val z: Float
) {
    companion object {
        val ORIGIN = Vertex(0f, 0f, 0f)
    }

    fun reflect(pivot: Vertex = ORIGIN) =
        Vertex(2 * pivot.x - x, 2 * pivot.y - y, 2 * pivot.z - z)
}