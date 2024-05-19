package com.vl.satellitelivewallpaper.presentation

import android.opengl.GLSurfaceView
import android.util.Log
import com.vl.satellitelivewallpaper.data.GLCanvas
import com.vl.satellitelivewallpaper.data.GLPainter
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Model
import com.vl.satellitelivewallpaper.domain.entity.Vertex
import com.vl.satellitelivewallpaper.domain.manager.GraphicsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WallpaperRenderer(private val model: Model): GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "WallpaperRenderer"
    }

    private lateinit var graphicsManager: GraphicsManager

    private val fps = AtomicInteger(0)
    private val fpsCounter = flow {
        while (true) {
            kotlinx.coroutines.delay(3000)
            emit(fps.get() / 3)
            fps.set(0)
        }
    }
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.d(TAG, "Surface created")
        scope.launch { fpsCounter.collect { Log.d(TAG, "FPS $it") } }
        graphicsManager = GraphicsManager(GLPainter(gl), GLCanvas(gl))
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d(TAG, "Changed: $width, $height")
        graphicsManager.canvas.setBounds(0, 0, width, height)

        gl.apply {
            /* Projection */
            glMatrixMode(GL10.GL_PROJECTION)
            glLoadIdentity()
            val projectionHeight = height.toFloat() / width
            glFrustumf(-1f, 1f, -projectionHeight, projectionHeight, 3f, 10f)
        }
    }

    override fun onDrawFrame(gl: GL10) {
        graphicsManager.canvas.clear(Color(1f, 1f, 1f))
        graphicsManager.painter.apply { // move and then scale
            moved(Vertex(0f, 0f, -5f)) {
                scaled(0.1f) {
                    graphicsManager.draw(model)
                }
            }
        }
        fps.incrementAndGet()
    }

    fun release() {
        Log.d(TAG, "Destroyed")
        scope.cancel()
    }
}