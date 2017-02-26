package com.example.testapp;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.view.MotionEvent;
import android.util.Log;

class MyGLSurfaceView extends GLSurfaceView
{
    final
        String TAG = "MyRenderer";
    
    private
        float beginX, beginY;
        int pointerId;
        MyRenderer renderer;
    
    MyGLSurfaceView(Context context)
    {
        super(context);
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
                renderer.changeViewPosition(
                    event.getAxisValue(MotionEvent.AXIS_X, pointerId) - beginX,
                    event.getAxisValue(MotionEvent.AXIS_Y, pointerId) - beginY
                );
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
        
        renderer = (MyRenderer)r;
    }
}