package com.example.testapp;

import android.opengl.Matrix;
import android.util.Log;

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
            
            T[] = {
                1f,0,0,0,
                0,1f,0,0,
                0,0,1f,0,
                0,0,0,1f,
            };
    
    //final String TAG = "MyRenderer";
    
    View()
    {
        
    }
    
    public void move(final float angle_x_grad, final float angle_y_grad)
    {
        float[] mx = new float[16];
        
        Matrix.multiplyMM(mx, 0, T, 0, getRotateByXMatrix(getAngle(angle_x_grad)), 0);
        Matrix.multiplyMM(T, 0, mx, 0, getRotateByYMatrix(getAngle(angle_y_grad)), 0);
        
        Matrix.multiplyMV(_eye, 0, T, 0, eye, 0);
        Matrix.multiplyMV(_up, 0, T, 0, up, 0);
    }
    
    public float[] getRotateByXMatrix(final float angle_rad)
    {
        final float
            cos = (float)Math.cos(angle_rad),
            sin = (float)Math.sin(angle_rad),
            matrix[] = {
                1f,  0f,  0f,  0f,
                0f,  cos, sin, 0f,
                0f, -sin, cos, 0f,
                0f,  0f,  0f,  1f,
            };
        
        return matrix;
    }
    
    public float[] getRotateByYMatrix(final float angle_rad)
    {
        final float
            cos = (float)Math.cos(angle_rad),
            sin = (float)Math.sin(angle_rad),
            matrix[] = {
                cos, 0f, sin, 0f,
                0f,  1f, 0f,  0f,
               -sin, 0f, cos, 0f,
                0f,  0f, 0f,  1f,
            };
        
        return matrix;
    }
    
    public float[] getMatrix()
    {
        float[] matrix = new float[16];
        
        Matrix.setLookAtM(
            matrix,      // rm
            0,           // rmOffset
            _eye[0],     // eyeX
            _eye[1],     // eyeY
            _eye[2],     // eyeZ
            center[0],   // centerX
            center[1],   // centerY
            center[2],   // centerZ
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
    
    private float getAngle(final float v)
    {
        return (float)(v * Math.PI / 180);
    }
}