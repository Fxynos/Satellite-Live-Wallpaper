package com.vl.satellitelivewallpaper.data

import com.vl.satellitelivewallpaper.domain.boundary.Scene
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Vertex
import javax.microedition.khronos.opengles.GL10

class GLScene(private val gl: GL10): Scene {

    override var isLightEnabled: Boolean = false
        set(value) {
            field = value
            if (isLightEnabled)
                gl.glEnable(GL10.GL_LIGHTING)
            else
                gl.glDisable(GL10.GL_LIGHTING)
        }

    override fun clear(color: Color) {
        gl.apply {
            glClearColor(color.red, color.green, color.blue, color.alpha)
            glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        }
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) =
        gl.glViewport(0, 0, width, height)

    /**
     * Define perspective projection cube of visible space
     */
    override fun setCamera(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) {
        gl.apply {
            glMatrixMode(GL10.GL_PROJECTION)
            glLoadIdentity()
            glFrustumf(left, right, bottom, top, near, far)
        }
    }

    override fun setLight(position: Vertex) {
        gl.apply {
            glEnable(GL10.GL_LIGHT0)
            glLightfv(
                GL10.GL_LIGHT0, GL10.GL_POSITION,
                floatArrayOf(position.x, position.y, position.z, 1f), 0
            )
        }
    }
}