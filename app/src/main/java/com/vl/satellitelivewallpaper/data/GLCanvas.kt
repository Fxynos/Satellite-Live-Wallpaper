package com.vl.satellitelivewallpaper.data

import com.vl.satellitelivewallpaper.domain.boundary.Canvas
import com.vl.satellitelivewallpaper.domain.entity.Color
import javax.microedition.khronos.opengles.GL10

class GLCanvas(private val gl: GL10): Canvas {

    override fun clear(color: Color) {
        gl.apply {
            glClearColor(color.red, color.green, color.blue, color.alpha)
            glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        }
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) =
        gl.glViewport(0, 0, width, height)
}