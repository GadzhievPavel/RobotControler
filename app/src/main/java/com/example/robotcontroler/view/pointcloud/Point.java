package com.example.robotcontroler.view.pointcloud;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glLinkProgram;

import android.content.Context;
import android.opengl.GLES20;

import com.example.robotcontroler.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Point {
    private FloatBuffer vertexBuffer;
    static final int COORDS_PER_VERTEX = 3;
    private int positionHandle;
    private int colorHandle;
    private final int mProgram;
    private int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private int vPMatrixHandle;
    private Context context;
    private float[] linesCoord = new float[]{0.0f,0.0f,0.0f,1.0f,0.0f,0.0f,
            0.0f,0.0f,0.0f,0.0f,1.0f,0.0f,
            0.0f,0.0f,0.0f,0.0f,0.0f,1.0f};
    float pointsCoords[] = {
            0.0f,  0.622008459f, 1.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    float[] allPoints;

    float color[]={ 0.63671875f, 0.76953125f, 0.22265625f, 0.50f };

    public Point(Context context, float[] pointsCoords){
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        mProgram = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glLinkProgram(mProgram);
        allPoints = new float[pointsCoords.length+ linesCoord.length];
        this.pointsCoords = pointsCoords;
        vertexCount = pointsCoords.length/COORDS_PER_VERTEX;
        int i=0;
        for(;i<linesCoord.length;i++){
            allPoints[i]=linesCoord[i];
        }
        for(int j = 0; j<pointsCoords.length; i++,j++){
            allPoints[i]=pointsCoords[j];
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(
                allPoints.length*4);
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(allPoints);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "v_Position");
        colorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        GLES20.glUniformMatrix4fv(vPMatrixHandle,1,false,mvpMatrix,0);
        // Set color for drawing the triangle


        ;
        // Draw the triangle

        drawDecartSystem(10);
        color[0] = 0.63671875f;
        color[1]= 0.76953125f;
        color[2] = 0.22265625f;
        color[3] = 0.50f;
        drawPoints(5, color);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private  void drawDecartSystem(int width){
        color[0] = 0.0f;
        color[1] = 0.0f;
        color[2] = 1.0f;
        color[3] = 1.0f;
        drawLines(color,width,0,2);
        color[0] = 0.0f;
        color[1] = 1.0f;
        color[2] = 0.0f;
        color[3] = 1.0f;
        drawLines(color,width,2,2);
        color[0] = 1.0f;
        color[1] = 0.0f;
        color[2] = 0.0f;
        color[3] = 1.0f;
        drawLines(color,width,4,2);
    }

    private void drawLines(float[] color, int width, int startCoords, int finishCoords){
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        glLineWidth(width);//size points
        GLES20.glDrawArrays(GLES20.GL_LINES, startCoords, finishCoords);
    }

    private void drawPoints(int size, float[] color){
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        glLineWidth(size);//size points
        GLES20.glDrawArrays(GL_POINTS,6,vertexCount);
    }
    public void setPointsCords(float[] data){
        this.pointsCoords = data;
        allPoints = new float[pointsCoords.length+ linesCoord.length];
        vertexCount = pointsCoords.length/COORDS_PER_VERTEX;
        int i=0;
        for(;i<linesCoord.length;i++){
            allPoints[i]=linesCoord[i];
        }
        for(int j = 0; j<pointsCoords.length; i++,j++){
            allPoints[i]=pointsCoords[j];
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(
                allPoints.length*4);
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(allPoints);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }
}
