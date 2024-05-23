package com.vl.satellitelivewallpaper.data

import com.vl.satellitelivewallpaper.domain.boundary.Painter
import com.vl.satellitelivewallpaper.domain.entity.Material
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

        private fun Array<Vertex>.flatMapCoordinates() = FloatArray(size * 3) {
            val (x, y, z) = this[it / 3]
            arrayOf(x, y, z)[it % 3]
        }
    }

    init { gl.glEnable(GL10.GL_DEPTH_TEST) }

    override fun paint(material: Material, vertices: Array<Vertex>, normals: Array<Vertex>) {
        gl.apply {
            glColor4f(
                material.diffuseColor.red,
                material.diffuseColor.green,
                material.diffuseColor.blue,
                material.diffuseColor.alpha
            )
            // TODO find out why GL_FRONT and GL_BACK aren't working even if normals are reflected
            glMaterialfv(
                GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
                floatArrayOf(
                    material.diffuseColor.red,
                    material.diffuseColor.green,
                    material.diffuseColor.blue,
                    1f
                ), 0
            )
            glMaterialfv(
                GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
                floatArrayOf(
                    material.ambientColor.red,
                    material.ambientColor.green,
                    material.ambientColor.blue,
                    1f
                ), 0
            )
            glMaterialfv(
                GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
                floatArrayOf(
                    material.specularColor.red,
                    material.specularColor.green,
                    material.specularColor.blue,
                    1f
                ), 0
            )
            glVertexPointer(3, GL10.GL_FLOAT, 0, allocateFloatBuffer(vertices.flatMapCoordinates()))
            glNormalPointer(3, GL10.GL_FLOAT, allocateFloatBuffer(normals.flatMapCoordinates()))
            glEnableClientState(GL10.GL_VERTEX_ARRAY)
            glDrawArrays(GL10.GL_TRIANGLES, 0, vertices.size)
            glDisableClientState(GL10.GL_VERTEX_ARRAY)
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