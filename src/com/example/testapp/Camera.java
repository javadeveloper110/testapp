package com.example.testapp;

import android.opengl.Matrix;
import android.util.Log;

class Camera
{
    private
        float
            fovy = 30f,//float: field of view in y direction, in degrees
            zNear = 1.0f,
            zFar = 50f,
            
            _eye[] = {0f, 0f, -25f, 1f},
            _center[] = {0f, 0f, 0f, 1f},
            _up[] = {0f, 1f, 0f, 1f},
            
            _viewMatrix[] = new float[16],
            _projectionMatrix[] = new float[16],
            
            eye[] = new float[4],
            center[] = new float[4],
            up[] = new float[4],
            
            viewMatrix[] = new float[16],
            
            projectionMatrix[] = {
                1f,0,0,0,
                0,1f,0,0,
                0,0,1f,0,
                0,0,0,1f,
            },
            
            viewProjectionMatrix[] = new float[16],
            
            T[] = {
                1f,0,0,0,
                0,1f,0,0,
                0,0,1f,0,
                0,0,0,1f,
            };
    
    private int width, height;
    
    Camera()
    {
        init();
        
        update();
    }
    
    protected void init()
    {
        Matrix.setLookAtM(
            _viewMatrix, // rm
            0,          // rmOffset
            _eye[0],     // eyeX
            _eye[1],     // eyeY
            _eye[2],     // eyeZ
            _center[0],  // centerX
            _center[1],  // centerY
            _center[2],  // centerZ
            _up[0],      // upX
            _up[1],      // upY
            _up[2]       // upZ
        );
    }
    
    protected void update()
    {
        Matrix.multiplyMV(eye, 0, T, 0, _eye, 0);
        //Matrix.multiplyMV(center, 0, T, 0, _center, 0);
        Matrix.multiplyMV(up, 0, T, 0, _up, 0);
        
        Matrix.setLookAtM(
            viewMatrix, // rm
            0,          // rmOffset
            eye[0],     // eyeX
            eye[1],     // eyeY
            eye[2],     // eyeZ
            _center[0],  // centerX
            _center[1],  // centerY
            _center[2],  // centerZ
            up[0],      // upX
            up[1],      // upY
            up[2]       // upZ
        );
        
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }
    
    /**
     * set screen size
    */
    public void setSize(final int w, final int h)
    {
        width = w;
        height = h;
        
        float aspect = (float)width / height;//float: width to height aspect ratio of the viewport
        
        Matrix.perspectiveM(projectionMatrix, 0, fovy, aspect, zNear, zFar);
        
        update();
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    /**
     * rotate around center
    */
    public void rotateAroundCenter(final float angle_around_x_axis_deg, final float angle_around_y_axis_deg)
    {
        float[] tmp = new float[16];
        
        Matrix.multiplyMM(tmp, 0, T, 0, getRotationMatrixAroundXAxis(_getAngle(angle_around_x_axis_deg)), 0);
        Matrix.multiplyMM(T, 0, tmp, 0, getRotationMatrixAroundYAxis(_getAngle(angle_around_y_axis_deg)), 0);
        
        update();
    }
    
    public float[] getRotationMatrixAroundXAxis(final float angle_rad)
    {
        final float
            cos = (float)Math.cos(angle_rad),
            sin = (float)Math.sin(angle_rad),
            matrix[] = {
                1f, 0f,   0f,  0f,
                0f, cos, sin, 0f,
                0f, -sin,  cos, 0f,
                0f, 0f,   0f,  1f,
            };
        
        return matrix;
    }
    
    public float[] getRotationMatrixAroundYAxis(final float angle_rad)
    {
        final float
            cos = (float)Math.cos(angle_rad),
            sin = (float)Math.sin(angle_rad),
            matrix[] = {
                cos, 0f, sin, 0f,
                0f,  1f,  0f,  0f,
                -sin, 0f,  cos, 0f,
                0f,  0f,  0f,  1f,
            };
        
        return matrix;
    }
    
    public float[] getVPMatrix()
    {
        return viewProjectionMatrix;
    }
    
    public float[] getEyeVec4()
    {
        return eye;
    }
    
    private float _getAngle(final float v)
    {
        return (float)(v * Math.PI / 180);
    }
}