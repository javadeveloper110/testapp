package com.example.testapp;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.view.MotionEvent;
import android.util.Log;
import android.view.SurfaceHolder;

class MyGLSurfaceView extends GLSurfaceView
{
    final String TAG   = "MyRenderer";
    
    private
        float beginX, beginY;
        int pointerId;
        MyRenderer renderer;
    
    MyGLSurfaceView(Context context)
    {
        super(context);
        
        Log.i(TAG, "MyGLSurfaceView.constructor");
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                pointerId = event.getPointerId(0);
                
                beginX = event.getAxisValue(MotionEvent.AXIS_X, pointerId);
                beginY = event.getAxisValue(MotionEvent.AXIS_Y, pointerId);
                
                //renderer.getPixRgbColor((int)beginX, (int)beginY);
            break;
            
            case MotionEvent.ACTION_MOVE:
                float
                    currentX = event.getAxisValue(MotionEvent.AXIS_X, pointerId),
                    currentY = event.getAxisValue(MotionEvent.AXIS_Y, pointerId),
                    rotateX = currentY - beginY,
                    rotateY = currentX - beginX,
                    k = 0.3f;
                
                renderer.getCamera().rotateAroundCenter(rotateX*k, rotateY*k);
                
                beginX = currentX;
                beginY = currentY;
            break;
            
            case MotionEvent.ACTION_UP:
                beginX = beginY = 0f;
                pointerId = 0;
            break;
            
            default:
                return false;
        }
        
        return true;
    }
    
    @Override
    public void setRenderer(GLSurfaceView.Renderer r)
    {
        super.setRenderer(r);
        Log.i(TAG, "MyGLSurfaceView.setRenderer");
        renderer = (MyRenderer)r;
    }
}