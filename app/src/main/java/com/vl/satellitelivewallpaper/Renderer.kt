package com.vl.satellitelivewallpaper

import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer: GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "GL Renderer"

        private fun allocateFloatBuffer(array: FloatArray): FloatBuffer {
            val byteBuf = ByteBuffer
                .allocateDirect(array.size * 4)
                .order(ByteOrder.nativeOrder())
            val floatBuf = byteBuf.asFloatBuffer()
                .put(array)
            byteBuf.position(0)
            floatBuf.position(0)
            return floatBuf
        }
    }

    private val vertices = allocateFloatBuffer(floatArrayOf(
        0f, 1f, 0f,
        -1f, -1f, 0f,
        1f, -1f, 0f
    ))
    private val colors = allocateFloatBuffer(floatArrayOf(1f, 0f, 0f))

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.d(TAG, "Surface created")
        gl.apply {
            glEnable(GL10.GL_DEPTH_TEST)
            /* Projection */
            /*glMatrixMode(GL10.GL_PROJECTION)
            glLoadIdentity()
            glFrustumx(-1, 1, -1, 1, 3, 10)*/
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d(TAG, "Changed: $width, $height")
        gl.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        gl.apply {
            glClearColor(1f, 1f, 1f, 1f)
            glClear(
                GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT
            )
            /* Transformation */
            glMatrixMode(GL10.GL_MODELVIEW)
            /* Drawing */
            glVertexPointer(3, GL10.GL_FLOAT, 0, vertices)
            glColor4f(1f, 0f, 0f, 1f) //glColorPointer(3, GL10.GL_FLOAT, 0, colors)

            glEnableClientState(GL10.GL_VERTEX_ARRAY)
            //glEnableClientState(GL10.GL_COLOR_ARRAY)

            glDrawArrays(GL10.GL_TRIANGLES, 0, 3)

            glDisableClientState(GL10.GL_VERTEX_ARRAY)
            //glDisableClientState(GL10.GL_COLOR_ARRAY)
        }
    }

    fun release() {
        Log.d(TAG, "Destroyed")
    }
}