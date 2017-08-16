package com.example.workingonopengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by 2114 on 11-07-2017.
 */

public class MyTextureOne
{
    private FloatBuffer vertex_float_buffer, texture_float_buffer,
            vertex_float_buffer2, texture_float_buffer2;
    private ByteBuffer vertex_byte_buffer, texture_byte_buffer,
            vertex_byte_buffer2, texture_byte_buffer2;
    private int vertex_capacity, texture_capacity;
    private static float vertex_array[],texture_array[],
            vertex_array2[],texture_array2[];
    private static final int COORDINATES_PER_VERTEX = 3;
    private static final int COORDINATES_PER_TEXTURE = 2;
    private int vertexCount;
    private int vertexStride,textureStride;


    private int vertex_shader,fragment_shader,program;
    private final String vertex_shader_code =
            "attribute vec4 aPosition;" +
                    "attribute vec2 aTexPosition;" +
                    "varying vec2 vTexPosition;" +
                    "void main() {" +
                    "  gl_Position = aPosition;" +
                    "  vTexPosition = aTexPosition;" +
                    "}";
    private final String fragment_shader_code =
            "precision mediump float;" +
                    "uniform sampler2D uTexture;" +
                    "varying vec2 vTexPosition;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(uTexture, vTexPosition);" +
                    "}";

    public MyTextureOne()
    {
        initBuffers();
        initProgram();
    }

    public void initBuffers()
    {
        vertex_array = new float[]
                {
                        -1.0f, -1.0f,0.0f,
                        -1.0f, 0.5f,0.0f,
                        0.5f, -1.0f,0.0f,
                        0.5f, 0.5f,0.0f
                };

        texture_array = new float[]
                {
                        0.0f,1.0f,
                        0.0f,0.0f,
                        1.0f,1.0f,
                        1.0f,0.0f
                };

        vertex_array2 = new float[]
                {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        1.0f, 1.0f
                };

        texture_array2 = new float[]
                {
                        0.0f,1.0f,
                        0.0f,0.0f,
                        1.0f,1.0f,
                        1.0f,0.0f
                };

        vertex_capacity = vertex_array.length * 4;
        texture_capacity = texture_array.length * 4;

        vertex_byte_buffer = ByteBuffer.allocateDirect(vertex_capacity);
        vertex_byte_buffer.order(ByteOrder.nativeOrder());
        vertex_float_buffer = vertex_byte_buffer.asFloatBuffer();
        vertex_float_buffer.put(vertex_array);
        vertex_float_buffer.position(0);

        texture_byte_buffer = ByteBuffer.allocateDirect(texture_capacity);
        texture_byte_buffer.order(ByteOrder.nativeOrder());
        texture_float_buffer = texture_byte_buffer.asFloatBuffer();
        texture_float_buffer.put(texture_array);
        texture_float_buffer.position(0);

        vertex_byte_buffer2 = ByteBuffer.allocateDirect(vertex_capacity);
        vertex_byte_buffer2.order(ByteOrder.nativeOrder());
        vertex_float_buffer2 = vertex_byte_buffer2.asFloatBuffer();
        vertex_float_buffer2.put(vertex_array2);
        vertex_float_buffer2.position(0);

        texture_byte_buffer2 = ByteBuffer.allocateDirect(texture_capacity);
        texture_byte_buffer2.order(ByteOrder.nativeOrder());
        texture_float_buffer2 = texture_byte_buffer2.asFloatBuffer();
        texture_float_buffer2.put(texture_array2);
        texture_float_buffer2.position(0);

        vertexCount = vertex_array.length / COORDINATES_PER_VERTEX;
        vertexStride = COORDINATES_PER_VERTEX*4;
        textureStride = COORDINATES_PER_TEXTURE*4;
    }

    public void initProgram()
    {
        vertex_shader = loadShader(GLES20.GL_VERTEX_SHADER,vertex_shader_code);
        fragment_shader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragment_shader_code);
        program = GLES20.glCreateProgram();

        GLES20.glAttachShader(program,vertex_shader);
        GLES20.glAttachShader(program,fragment_shader);
        GLES20.glLinkProgram(program);
    }

    public void draw(int texture)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(program);
        GLES20.glDisable(GLES20.GL_BLEND);

        /*Getting Positions From C - Program*/
        int position_handle = GLES20.glGetAttribLocation(program, "aPosition");
        int texture_handle = GLES20.glGetUniformLocation(program, "uTexture");
        int texture_position_handle = GLES20.glGetAttribLocation(program, "aTexPosition");

        /*Textures*/
        GLES20.glVertexAttribPointer(
                texture_position_handle,
                COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                textureStride,
                texture_float_buffer);

        GLES20.glEnableVertexAttribArray(texture_position_handle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(texture_handle, 0);



        /*Vertices*/
        GLES20.glVertexAttribPointer(
                position_handle,
                COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertex_float_buffer);

        GLES20.glEnableVertexAttribArray(position_handle);

        /*Drawing*/
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        /*GLES20.glVertexAttribPointer(
                texture_position_handle,
                COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                texture_float_buffer2);

        GLES20.glVertexAttribPointer(
                position_handle,
                COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertex_float_buffer2);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);*/

    }

    public int loadShader(int type, String shaderCode)
    {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
