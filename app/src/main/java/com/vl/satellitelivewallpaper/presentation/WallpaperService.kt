package com.vl.satellitelivewallpaper.presentation

import net.rbgrn.android.glwallpaperservice.GLWallpaperService

class WallpaperService: GLWallpaperService() {

    override fun onCreateEngine(): GLEngine = Engine()

    private inner class Engine: GLEngine() {
        private val renderer = WallpaperRenderer(applicationContext)

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