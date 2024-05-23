package com.vl.satellitelivewallpaper.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.util.Log
import com.vl.satellitelivewallpaper.R
import com.vl.satellitelivewallpaper.data.GLScene
import com.vl.satellitelivewallpaper.data.GLPainter
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Model
import com.vl.satellitelivewallpaper.domain.entity.Vertex
import com.vl.satellitelivewallpaper.domain.manager.GraphicsManager
import com.vl.satellitelivewallpaper.data.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.Volatile
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

class WallpaperRenderer(context: Context): GLSurfaceView.Renderer, SensorEventListener {
    companion object {
        private const val TAG = "WallpaperRenderer"

        private fun sqr(value: Float) = value.pow(2)
    }

    private lateinit var graphicsManager: GraphicsManager
    private val materials = Parser.parseMtlLib(context.resources.openRawResource(R.raw.rocket_mtl))
    private val model: Model = Parser.parseObjModel(context.resources.openRawResource(R.raw.rocket_obj), materials)

    private val fps = AtomicInteger(0)
    private val fpsCounter = flow {
        while (true) {
            kotlinx.coroutines.delay(3000)
            emit(fps.get() / 3)
            fps.set(0)
        }
    }
    private val scope = CoroutineScope(Dispatchers.IO)

    private val sensorManager = context.getSystemService(SensorManager::class.java).apply {
        registerListener(
            this@WallpaperRenderer,
            getDefaultSensor(Sensor.TYPE_GRAVITY), // TODO use magnet sensor to get Y-rotation
            SensorManager.SENSOR_DELAY_UI
        )
    }
    @Volatile private var rotation = 0f to Vertex(0f, 0f, 0f) // radians to vector

    override fun onSensorChanged(event: SensorEvent) {
        // rotates gravity vector from (0; -1; 0) to following vector
        val (x, y, z) = event.values
        // scalar product of vectors is cos of angle between them
        // vector product is a normal of plane of these vectors n(-z; 0; x)
        rotation = -acos(y / sqrt(sqr(x) + sqr(y) + sqr(z))) to Vertex(-z, 0f, x)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.d(TAG, "Surface created")
        scope.launch { fpsCounter.collect { Log.d(TAG, "FPS $it") } }
        graphicsManager = GraphicsManager(GLPainter(gl), GLScene(gl), true)
        graphicsManager.scene.isLightEnabled = true
        graphicsManager.scene.setLight(
            Vertex(0f, 0f, 3f),
            Color("#FFFFFF"),
            Color("#FFFFFF"),
            Color("#FFFFFF")
        )
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d(TAG, "Changed: $width, $height")
        graphicsManager.scene.setBounds(0, 0, width, height)

        val projectionHeight = height.toFloat() / width
        graphicsManager.scene.setCamera(
            bottom = -projectionHeight,
            top = projectionHeight,
            near = 3f,
            far = 10f
        )
    }

    override fun onDrawFrame(gl: GL10) {
        graphicsManager.scene.clear(Color(0, 0, 0))
        graphicsManager.painter.apply { // move and then scale
            moved(Vertex(0f, 0f, -8f)) {
                scaled(0.2f) {
                    rotated(rotation.first * 180 / Math.PI.toFloat(), rotation.second) {
                        graphicsManager.draw(model)
                    }
                }
            }
        }
        fps.incrementAndGet()
    }

    fun release() {
        Log.d(TAG, "Destroyed")
        scope.cancel()
        sensorManager.unregisterListener(this)
    }
}