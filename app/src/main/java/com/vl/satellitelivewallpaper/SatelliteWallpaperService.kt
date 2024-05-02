package com.vl.satellitelivewallpaper

import android.util.Log
import net.rbgrn.android.glwallpaperservice.GLWallpaperService

class SatelliteWallpaperService: GLWallpaperService() {
    companion object {
        private const val TAG = "Satellite Wallpaper"
    }

    override fun onCreateEngine(): GLEngine = Engine()

    private inner class Engine: GLEngine() {
        private val renderer = Renderer()

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