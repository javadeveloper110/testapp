package com.example.testapp;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.view.MotionEvent;
import android.util.Log;
import android.view.SurfaceHolder;

class MyGLSurfaceView extends GLSurfaceView
{
    final String TAG = "MyRenderer";
    final View view = new View();
    
    private
        float beginX, beginY;
        int pointerId;
        MyRenderer renderer;
        Cube objects[];
    
    MyGLSurfaceView(Context context)
    {
        super(context);
        
        Log.i(TAG, "MyGLSurfaceView.constructor");
    }
    
    public void surfaceCreated(SurfaceHolder holder)
    {
        super.surfaceCreated(holder);
        
        //renderer.setViewMatrix(view.getMatrix(), view.getEyeVec4());
        
        Log.i(TAG, "\n\n\n\n\n\n\n\n\nMyGLSurfaceView.surfaceCreated");
        
        init();
    }
    
    private void init()
    {
        objects = new Cube[1];
        objects[0] = new Cube();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                pointerId = event.getPointerId(0);
            break;
            
            case MotionEvent.ACTION_MOVE:
                view.move(event.getAxisValue(MotionEvent.AXIS_X, pointerId) - beginX, event.getAxisValue(MotionEvent.AXIS_Y, pointerId) - beginY);
                
                renderer.setViewMatrix(view.getMatrix(), view.getEyeVec4());
            break;
            
            case MotionEvent.ACTION_UP:
                beginX = beginY = 0f;
                pointerId = 0;
            break;
            
            default:
                return false;
        }
        
        beginX = event.getAxisValue(MotionEvent.AXIS_X, pointerId);
        beginY = event.getAxisValue(MotionEvent.AXIS_Y, pointerId);
        
        return true;
    }
    
    @Override
    public void setRenderer(GLSurfaceView.Renderer r)
    {
        super.setRenderer(r);
        Log.i(TAG, "MyGLSurfaceView.setRenderer");
        //renderer.setViewMatrix(view.getMatrix(), view.getEyeVec4());
        renderer = (MyRenderer)r;
    }
}