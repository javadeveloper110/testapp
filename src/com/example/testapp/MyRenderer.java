package com.example.testapp;

import android.opengl.GLSurfaceView.Renderer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.util.Log;

public class MyRenderer implements Renderer
{
    final
        String TAG = "MyRenderer",
        
        vertexShaderSource = "void main() {}",
        
        fragmentShaderSource = "void main() {}";
    
    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
    
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }
    
    private int loadShader(int type, String shaderSource) throws Exception
    {
        //Log.i(TAG, "MyRenderer.loadShader begin");
        
        String typeStr = type == GLES20.GL_VERTEX_SHADER ? "vertex" : "fragment";
        
        int shader = GLES20.glCreateShader(type);
        
        if(shader == 0)
            throw new Exception("can't create a "+ typeStr +" shader object");
        
        GLES20.glShaderSource(shader, shaderSource);
        
        GLES20.glCompileShader(shader);
        
        int[] params = new int[1];
        
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, params, 0);
        
        if(params[0] == 0)
            throw new Exception(GLES20.glGetShaderInfoLog(shader));
        
        //Log.i(TAG, "MyRenderer.loadShader end");
        
        return shader;
    }
    
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        Log.v(TAG, "\n\n\n=============================================================================");
        //Log.i(TAG, "MyRenderer.onSurfaceCreated begin");
        
        try
        {
            int
                program = GLES20.glCreateProgram(),
                params[] = new int[1];
            
            if(program == 0)
                throw new Exception("can't create a gl program object");
            
            GLES20.glAttachShader(program, loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource));
            GLES20.glAttachShader(program, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource));
            GLES20.glGetProgramiv(program, GLES20.GL_ATTACHED_SHADERS, params, 0);
            
            if(params[0] != 2)
                throw new Exception("can not attach shaders");
            
            GLES20.glLinkProgram(program);
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, params, 0);
            
            if(params[0] == 0)
                throw new Exception(GLES20.glGetProgramInfoLog(program));
            
            GLES20.glValidateProgram(program);
            GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, params, 0);
            
            if(params[0] == 0)
                throw new Exception(GLES20.glGetProgramInfoLog(program));
            
            GLES20.glUseProgram(program);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        
        GLES20.glClearColor((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
        
        //Log.i(TAG, "MyRenderer.onSurfaceCreated end");
    }
}