package com.example.testapp;

import android.opengl.GLSurfaceView.Renderer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.util.Log;
import java.util.Map;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Date;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MyRenderer implements Renderer
{
    final
        String TAG = "MyRenderer",
        
        vertexShaderSource = "attribute vec4 a_position;"
                            +""
                            +""
                            +"void main()"
                            +"{"
                            +"gl_Position = a_position;"
                            +""
                            +""
                            +""
                            +""
                            +"}",
        
        fragmentShaderSource = ""
                            +"precision mediump float;"
                            +""
                            +"void main()"
                            +"{"
                            +"gl_FragColor = vec4(0.3, 0.3, 0.7, 1.0);"
                            +""
                            +""
                            +""
                            +""
                            +"}";
    
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        Date d = new Date();
        Log.v(TAG, "\n\n"+ 	d.toGMTString() +"\n=============================================================================");
        //Log.i(TAG, "MyRenderer.onSurfaceCreated begin");
        
        GLES20.glClearColor(0.3f, 0.9f, 0.9f, 1.0f);
        
        try
        {
            int program = createProgram();
            
            int a_position_loc = GLES20.glGetAttribLocation(program, "a_position");
            
            float mVerticesData[] = {
                0.6f, 0.5f, 0.0f,
                0.3f, 0.5f, 0.0f,
                -0.9f, -0.5f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f
            };
            
            FloatBuffer mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
            
            mVertices.put(mVerticesData).position(0);
            
            GLES20.glVertexAttribPointer (a_position_loc, 
                3,//int size, 
                GLES20.GL_FLOAT,//int type, 
                false,//boolean normalized, 
                0,//int stride,
                mVertices//int offset
            );
            
            GLES20.glEnableVertexAttribArray(a_position_loc);
            
            Log.i(TAG, "a_position_loc: "+ a_position_loc);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_LINES, 2, 4);
        
        Log.i(TAG, "GLES20.GL_INVALID_VALUE: "+ GLES20.GL_INVALID_VALUE);
    }
    
    public void onDrawFrame(GL10 unused)
    {
        
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
        __logShaderInfo(shader);
        
        return shader;
    }
    
    private int createProgram() throws Exception
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
        
        __logProgramInfo(program);
        
        return program;
    }
    
    private void __logProgramInfo(int program)
    {
        //Log.i(TAG, "Float.SIZE: "+ Float.SIZE);
        Log.i(TAG, "MyRenderer.__logProgramInfo (program: "+ program +"):");
        
        int params[] = new int[1];
        Map<Integer, String> map = new Hashtable<Integer, String>();
        
        map.put(GLES20.GL_ACTIVE_ATTRIBUTES, "GL_ACTIVE_ATTRIBUTES");
        map.put(GLES20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, "GL_ACTIVE_ATTRIBUTE_MAX_LENGTH");
        map.put(GLES20.GL_ACTIVE_UNIFORMS, "GL_ACTIVE_UNIFORMS");
        map.put(GLES20.GL_ACTIVE_UNIFORM_MAX_LENGTH, "GL_ACTIVE_UNIFORM_MAX_LENGTH");
        map.put(GLES20.GL_ATTACHED_SHADERS, "GL_ATTACHED_SHADERS");
        map.put(GLES20.GL_DELETE_STATUS, "GL_DELETE_STATUS");
        map.put(GLES20.GL_INFO_LOG_LENGTH, "GL_INFO_LOG_LENGTH");
        map.put(GLES20.GL_LINK_STATUS, "GL_LINK_STATUS");
        map.put(GLES20.GL_VALIDATE_STATUS, "GL_VALIDATE_STATUS");
        
        for (Map.Entry<Integer, String> entry : map.entrySet())
        {
            GLES20.glGetProgramiv(program, entry.getKey(), params, 0);
            Log.i(TAG, "program["+ entry.getValue() +"]: "+ params[0]);
        }
        
        Log.i(TAG, "program info log: "+ GLES20.glGetProgramInfoLog(program));
        
        Log.i(TAG, "\n\n\n");
    }
    
    private void __logShaderInfo(int shader)
    {
        Log.i(TAG, "MyRenderer.__logShaderInfo (shader: "+ shader +"):");
        
        int params[] = new int[1];
        Map<Integer, String> map = new Hashtable<Integer, String>();
        
        map.put(GLES20.GL_COMPILE_STATUS, "GL_COMPILE_STATUS");
        map.put(GLES20.GL_DELETE_STATUS, "GL_DELETE_STATUS");
        map.put(GLES20.GL_INFO_LOG_LENGTH, "GL_INFO_LOG_LENGTH");
        map.put(GLES20.GL_SHADER_SOURCE_LENGTH, "GL_SHADER_SOURCE_LENGTH");
        map.put(GLES20.GL_SHADER_TYPE, "GL_SHADER_TYPE");
        
        for (Map.Entry<Integer, String> entry : map.entrySet())
        {
            GLES20.glGetShaderiv(shader, entry.getKey(), params, 0);
            Log.i(TAG, "shader["+ entry.getValue() +"]: "+ params[0]);
        }
        
        Log.i(TAG, "shader info log: "+ GLES20.glGetShaderInfoLog(shader));
        
        Log.i(TAG, "\n\n\n");
    }
}