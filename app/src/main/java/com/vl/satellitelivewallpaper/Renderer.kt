package com.vl.satellitelivewallpaper

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer: GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) = Unit

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) = Unit

    override fun onDrawFrame(gl: GL10) {
        gl.glClearColor(0f, 0f, 1f, 1f)
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
    }

    fun release() = Unit
}