package com.vl.satellitelivewallpaper

import android.animation.ValueAnimator
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
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

    private var distance = 0f
    private val distanceAnimator = ValueAnimator()
    private var rotation = 0f
    private val rotationAnimator = ValueAnimator()

    init {
        distanceAnimator.repeatMode = ValueAnimator.REVERSE
        distanceAnimator.repeatCount = ValueAnimator.INFINITE
        distanceAnimator.duration = 1000
        distanceAnimator.interpolator = AccelerateDecelerateInterpolator()
        distanceAnimator.setFloatValues(4f, 8f)
        distanceAnimator.addUpdateListener { distance = it.animatedValue as Float }
        distanceAnimator.start()

        rotationAnimator.repeatMode = ValueAnimator.RESTART
        rotationAnimator.repeatCount = ValueAnimator.INFINITE
        rotationAnimator.duration = 3000
        rotationAnimator.interpolator = LinearInterpolator()
        rotationAnimator.setFloatValues(0f, 360f)
        rotationAnimator.addUpdateListener { rotation = it.animatedValue as Float }
        rotationAnimator.start()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.d(TAG, "Surface created")
        gl.glEnable(GL10.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d(TAG, "Changed: $width, $height")
        gl.glViewport(0, 0, width, height)
        gl.apply {
            /* Projection */
            glMatrixMode(GL10.GL_PROJECTION)
            glLoadIdentity()
            val projectionHeight = height.toFloat() / width
            glFrustumf(-1f, 1f, -projectionHeight, projectionHeight, 3f, 10f)
        }
    }

    override fun onDrawFrame(gl: GL10) {
        gl.apply {
            glClearColor(1f, 1f, 1f, 1f)
            glClear(
                GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT
            )
            /* Transformation */
            glMatrixMode(GL10.GL_MODELVIEW)
            glPushMatrix()
            glTranslatef(0f, 0f, -distance)
            glRotatef(-rotation, 0f, 0f, 1f)
            /* Drawing */
            glColor4f(1f, 0f, 0f, 1f)
            glVertexPointer(3, GL10.GL_FLOAT, 0, vertices)
            glEnableClientState(GL10.GL_VERTEX_ARRAY)
            glDrawArrays(GL10.GL_TRIANGLES, 0, 3)
            glDisableClientState(GL10.GL_VERTEX_ARRAY)
            glPopMatrix()
        }
    }

    fun release() {
        Log.d(TAG, "Destroyed")
        distanceAnimator.cancel()
        rotationAnimator.cancel()
    }
}