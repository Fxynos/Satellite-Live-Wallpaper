package com.vl.satellitelivewallpaper

import net.rbgrn.android.glwallpaperservice.GLWallpaperService

class SatelliteWallpaperService: GLWallpaperService() {

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