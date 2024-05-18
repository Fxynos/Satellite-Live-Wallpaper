package com.vl.satellitelivewallpaper.presentation

import net.rbgrn.android.glwallpaperservice.GLWallpaperService

class SatelliteWallpaperService: GLWallpaperService() {
    companion object {
        private const val TAG = "Satellite Wallpaper"
    }

    override fun onCreateEngine(): GLEngine = Engine()

    private inner class Engine: GLEngine() {
        private val renderer = WallpaperRenderer()

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