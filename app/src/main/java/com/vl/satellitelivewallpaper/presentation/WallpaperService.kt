package com.vl.satellitelivewallpaper.presentation

import com.vl.satellitelivewallpaper.R
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Material
import com.vl.satellitelivewallpaper.domain.manager.ModelParser
import net.rbgrn.android.glwallpaperservice.GLWallpaperService

class WallpaperService: GLWallpaperService() {

    override fun onCreateEngine(): GLEngine = Engine()

    private inner class Engine: GLEngine() {
        private val renderer = WallpaperRenderer(ModelParser.parse(
            resources.openRawResource(R.raw.test),
            arrayOf(Material(
                "Material",
                Color("#00FF00"),
                Color("#00FF00"),
                Color("#00FF00")
            ))
        ))

        init {
            setRenderer(renderer)
            renderMode = RENDERMODE_CONTINUOUSLY
        }

        override fun onDestroy() {
            super.onDestroy()
            renderer.release()
        }
    }
}