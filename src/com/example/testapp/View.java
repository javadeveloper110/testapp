package com.example.testapp;

import android.opengl.Matrix;

class View
{
    private
        float[]
            eye = {0f, 0f, -4f, 1f},
            center = {0f, 0f, 0f, 1f},
            up = {0f, 1f, 0f, 1f};
    
    View()
    {
        
    }public void rotateX(final float a)
    {
        final float angle = getAngle(a);
        final float kx = eye[2] / (float)Math.sqrt((double)(eye[0]*eye[0] + eye[2]*eye[2]));
        final float kz = eye[0] / (float)Math.sqrt((double)(eye[0]*eye[0] + eye[2]*eye[2]));
        final float ky = eye[1] / (float)Math.sqrt((double)(eye[0]*eye[0] + eye[2]*eye[2]));
        
        double angleX = (double)(angle * kx);
        double angleY = (double)(angle * ky);
        double angleZ = (double)(angle * kz);
        
        float cosX = (float)Math.cos(angleX);
        float sinX = (float)Math.sin(angleX);
        float cosY = (float)Math.cos(angleY);
        float sinY = (float)Math.sin(angleY);
        float cosZ = (float)Math.cos(angleZ);
        float sinZ = (float)Math.sin(angleZ);
        
        float[]
            matrixX = {
                1f,  0f,   0f,   0f,
                0f,  cosX, sinX, 0f,
                0f, -sinX, cosX, 0f,
                0f,  0f,   0f,   1f,
            },
            matrixY = {
                cosY, 0f, sinY, 0f,
                0f, 1f, 0f, 0f,
                -sinY, 0f, cosY, 0f,
                0f, 0f, 0f, 1f,
            },
            matrixZ = {
                cosZ, -sinZ, 0f, 0f,
                sinZ,  cosZ, 0f, 0f,
                0f,    0f,   1f, 0f,
                0f,    0f,   0f, 1f,
            },
            resultMatrix = new float[16];
        
        Matrix.multiplyMM(resultMatrix, 0, matrixX, 0, matrixY, 0);
        Matrix.multiplyMM(resultMatrix, 0, resultMatrix, 0, matrixZ, 0);
        
        Matrix.multiplyMV(eye, 0, resultMatrix, 0, eye, 0);
        Matrix.multiplyMV(up, 0, resultMatrix, 0, up, 0);
    }
    
    public void rotateY(final float a)
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
        final float moveMatrix[] = getMoveMatrix(x, y);
        
        Matrix.multiplyMV(eye, 0, moveMatrix, 0, eye, 0);
        Matrix.multiplyMV(up, 0, moveMatrix, 0, up, 0);
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
            moveMatrix = new float[16];
        
        Matrix.multiplyMM(moveMatrix, 0, matrixY, 0, matrixX, 0);
        
        return moveMatrix;
    }
    
    private float getAngle(final float v)
    {
        return (float)(v * Math.PI / 180);
    }
}