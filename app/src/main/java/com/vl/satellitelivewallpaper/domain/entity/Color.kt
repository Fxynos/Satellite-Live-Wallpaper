package com.vl.satellitelivewallpaper.domain.entity

import android.graphics.Color
import androidx.annotation.ColorInt

class Color(@ColorInt private val value: Int) {
    val red: Float
        get() = redByte / 255f
    val green: Float
        get() = greenByte / 255f
    val blue: Float
        get() = blueByte / 255f
    val alpha: Float
        get() = alphaByte / 255f

    val redByte: Int
        get() = Color.red(value)
    val greenByte: Int
        get() = Color.green(value)
    val blueByte: Int
        get() = Color.blue(value)
    val alphaByte: Int
        get() = Color.alpha(value)

    constructor(hex: String): this(Color.parseColor(hex))
    constructor(red: Float, green: Float, blue: Float, alpha: Float = 1f): this(Color.argb(alpha, red, green, blue))
    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255): this(Color.argb(alpha, red, green, blue))
}