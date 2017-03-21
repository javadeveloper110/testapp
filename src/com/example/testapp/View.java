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
        float[]
            m = new float[16],
            e = new float[4],
            u = new float[4];
        
        Matrix.multiplyMV(e, 0, T, 0, eye, 0);
        Matrix.multiplyMV(u, 0, T, 0, up, 0);
        
        Matrix.setLookAtM(
            m,      // rm
            0,           // rmOffset
            e[0],     // eyeX
            e[1],     // eyeY
            e[2],     // eyeZ
            center[0],   // centerX
            center[1],   // centerY
            center[2],   // centerZ
            u[0],      // upX
            u[1],      // upY
            u[2]       // upZ
        );
        
        return m;
    }
    
    public float[] getEyeVec4()
    {
        float[] e = new float[4];
        
        Matrix.multiplyMV(e, 0, T, 0, eye, 0);
        
        return e;
    }
    
    private float getAngle(final float v)
    {
        return (float)(v * Math.PI / 180);
    }
}