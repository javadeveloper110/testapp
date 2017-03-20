package com.example.testapp;

import android.opengl.Matrix;

class View
{
    private
        float
            eye[] = {0f, 0f, -12f, 1f},
            center[] = {0f, 0f, 0f, 1f},
            up[] = {0f, 1f, 0f, 1f},
            _eye[] = new float[4],
            _center[] = new float[4],
            _up[] = new float[4],
            
            angleX = 0f,
            angleY = 0f;
    
    View()
    {
        
    }
    
    public void move(final float x, final float y)
    {
        final float mm[] = getMoveMatrix(x, y);
        
        angleX = (angleX + x) % 360;
        angleY = (angleY + y) % 360;
        
        Matrix.multiplyMV(_eye, 0, mm, 0, eye, 0);
        Matrix.multiplyMV(_up, 0, mm, 0, up, 0);
    }
    
    public float[] getMatrix()
    {
        float[] matrix = new float[16];
        
        Matrix.setLookAtM(
            matrix,     // rm
            0,          // rmOffset
            _eye[0],     // eyeX
            _eye[1],     // eyeY
            _eye[2],     // eyeZ
            center[0],  // centerX
            center[1],  // centerY
            center[2],  // centerZ
            _up[0],      // upX
            _up[1],      // upY
            _up[2]       // upZ
        );
        
        return matrix;
    }
    
    public float[] getEyeVec4()
    {
        return _eye;
    }
    
    private float[] _getRotateMatrix(final float x, final float y)
    {
        final float
            ax = getAngle(x),
            ay = getAngle(y),
            cosX = (float)Math.cos(ax),
            sinX = (float)Math.sin(ax),
            cosY = (float)Math.cos(ay),
            sinY = (float)Math.sin(ay);
        
        float[]
            matrixX = {
                1f,  0f,    0f,   0f,
                0f,  cosX,  sinX, 0f,
                0f, -sinX,  cosX, 0f,
                0f,  0f,    0f,   1f,
            },
            matrixY = {
                cosY,  0f,  sinY,  0f,
                0f,    1f,  0f,    0f,
               -sinY,  0f,  cosY,  0f,
                0f,    0f,  0f,    1f,
            },
            matrixXY = new float[16];
        
        Matrix.multiplyMM(matrixXY, 0, matrixY, 0, matrixX, 0);
        
        return matrixXY;
    }
    
    private float[] getMoveMatrix(final float x, final float y)
    {
        float
            mm[] = new float[16],
            mt[] = new float[16],
            mr[] = new float[16];
        
        mt = _getRotateMatrix(angleX, angleY);
        mm = _getRotateMatrix(x, y);
        
        Matrix.multiplyMM(mr, 0, mm, 0, mt, 0);
        
        return mr;
    }
    
    private float getAngle(final float v)
    {
        return (float)(v * Math.PI / 180);
    }
}