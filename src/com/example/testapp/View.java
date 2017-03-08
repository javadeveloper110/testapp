package com.example.testapp;

import android.opengl.Matrix;

class View
{
    private
        float[]
            eye = {0f, 0f, -12f, 1f},
            center = {0f, 0f, 0f, 1f},
            up = {0f, 1f, 0f, 1f},
            moveMatrix = {
                1f, 0, 0, 0,
                0, 1f, 0, 0,
                0, 0, 1f, 0,
                0, 0, 0, 1f,
            };
    
    View()
    {
        
    }
    
    public float[] getMatrix()
    {
        float[] matrix = new float[16];
        
        Matrix.setLookAtM(
            matrix,     // rm
            0,          // rmOffset
            eye[0],     // eyeX
            eye[1],     // eyeY
            eye[2],     // eyeZ
            center[0],  // centerX
            center[1],  // centerY
            center[2],  // centerZ
            up[0],      // upX
            up[1],      // upY
            up[2]       // upZ
        );
        
        return matrix;
    }
    
    public void move(final float x, final float y)
    {
        final float mm[] = getMoveMatrix(y, x);
        
        Matrix.multiplyMV(eye, 0, mm, 0, eye, 0);
        Matrix.multiplyMV(up, 0, mm, 0, up, 0);
    }
    
    public float[] getEyeVec4()
    {
        return eye;
    }
    
    private float[] getMoveMatrix(final float x, final float y)
    {
        final float
            angleX = getAngle(x),
            angleY = getAngle(y),
            cosX = (float)Math.cos(angleX),
            sinX = (float)Math.sin(angleX),
            cosY = (float)Math.cos(angleY),
            sinY = (float)Math.sin(angleY);
        
        float[]
            matrixX = {
                1f, 0f, 0f, 0f,
                0f, cosX, sinX, 0f,
                0f, -sinX, cosX, 0f,
                0f, 0f, 0f, 1f,
            },
            matrixY = {
                cosY, 0f, sinY, 0f,
                0f, 1f, 0f, 0f,
                -sinY, 0f, cosY, 0f,
                0f, 0f, 0f, 1f,
            },
            matrixXY = new float[16];
        
        Matrix.multiplyMM(matrixXY, 0, matrixX, 0, matrixY, 0);
        Matrix.multiplyMM(moveMatrix, 0, matrixXY, 0, moveMatrix, 0);
        return matrixXY;
    }
    
    private float getAngle(final float v)
    {
        return (float)(v * Math.PI / 180);
    }
}