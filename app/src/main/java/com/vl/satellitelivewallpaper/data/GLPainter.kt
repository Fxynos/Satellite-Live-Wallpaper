package com.vl.satellitelivewallpaper.data

import com.vl.satellitelivewallpaper.domain.boundary.Painter
import com.vl.satellitelivewallpaper.domain.entity.Color
import com.vl.satellitelivewallpaper.domain.entity.Vertex
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10

class GLPainter(private val gl: GL10): Painter {
    companion object {
        private fun allocateFloatBuffer(array: FloatArray) = ByteBuffer
            .allocateDirect(array.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(array)
            .position(0)
    }

    init { gl.glEnable(GL10.GL_DEPTH_TEST) }

    override fun paint(color: Color, vertices: Array<Vertex>, normalVector: Vertex) {
        gl.apply {
            glColor4f(color.red, color.green, color.blue, color.alpha)
            glVertexPointer(3, GL10.GL_FLOAT, 0, allocateFloatBuffer(FloatArray(vertices.size) {
                val (x, y, z) = vertices[it / 3]
                arrayOf(x, y, z)[it % 3]
            }))
            glEnableClientState(GL10.GL_VERTEX_ARRAY)
            glDrawArrays(GL10.GL_TRIANGLES, 0, 3) // TODO map facet to triangles
            glDisableClientState(GL10.GL_VERTEX_ARRAY)
            glPopMatrix()
        }
    }

    override fun moved(vector: Vertex, block: () -> Unit) = transformed {
        glTranslatef(vector.x, vector.y, vector.z)
        block()
    }

    override fun rotated(degree: Float, vector: Vertex, block: () -> Unit) = transformed {
        glRotatef(degree, vector.x, vector.y, vector.z)
        block()
    }

    override fun scaled(size: Float, block: () -> Unit) = transformed {
        glScalef(size, size, size)
        block()
    }

    private inline fun transformed(block: GL10.() -> Unit) {
        gl.apply {
            glMatrixMode(GL10.GL_MODELVIEW)
            glPushMatrix()
            block()
            glPopMatrix()
        }
    }
}