package com.vl.satellitelivewallpaper.domain.entity

data class Material( // TODO support textures
    val name: String,
    val ambientColor: Color,
    val diffuseColor: Color,
    val specularColor: Color,
    val textureMap: String? = null
)