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
import android.opengl.Matrix;

public class MyRenderer implements Renderer
{
    protected
        float[] viewMatrix = new float[16];
        float[] projMatrix = new float[16];
        float[] mMVPMatrix = new float[16];
        float[] cameraPosition = {0f, 0f, -2f, 1f};
        int muMVPMatrixHandle;
        int u_lightPosHandle;
    
    final
        String TAG = "MyRenderer",
        
        vertexShaderSource = "precision mediump float;"
                            +"attribute vec4 a_position;\n"
                            +"attribute vec4 a_normal;\n"
                            +"uniform mat4 u_MVPMatrix;\n"
                            +"uniform vec4 u_lightPos;\n"
                            +""
                            +"void main()"
                            +"{"
                            +"gl_Position = u_MVPMatrix*a_position;"
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
                            +"gl_FragColor = vec4(0.5, 0.9, 0.1, 1.0);"
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
         
        Matrix.setLookAtM(
            viewMatrix, // rm
            0,          // rmOffset
            0,          // eyeX
            0.0f,       // eyeY
            -2f,        // eyeZ
            0f,         // centerX
            0f,         // centerY
            0f,         // centerZ
            0f,         // upX
            1.0f,       // upY
            0.0f        // upZ
        );
        
        GLES20.glClearColor(0.3f, 0.9f, 0.9f, 1.0f);
        
        try
        {
            int program = createProgram();
            final int BYTES_PER_FLOAT = Float.SIZE / 8;
            int[] vertexBuffers = new int[2];
            int a_position_loc = GLES20.glGetAttribLocation(program, "a_position");
            int a_normal_loc = GLES20.glGetAttribLocation(program, "a_normal");
            muMVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
            u_lightPosHandle = GLES20.glGetUniformLocation(program, "u_lightPos");
            
            float verticesData[] = {
                -1f, -1f, 0.0f,     -1f, -1f, -1f,
                1f, -1f, 0.0f,      1f, -1f, -1f,
                0.0f, 1f, 0.0f,     0.0f, 1f, -1f,
            };
            byte indicesData[] = {
                0, 1, 2,
            };
            
            FloatBuffer vertices = ByteBuffer.allocateDirect(verticesData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
            
            ByteBuffer indices = ByteBuffer.allocateDirect(indicesData.length)
                .order(ByteOrder.nativeOrder());
            
            vertices.put(verticesData).position(0);
            indices.put(indicesData).position(0);
            
            GLES20.glGenBuffers(vertexBuffers.length, vertexBuffers, 0);
            
            GLES20.glBindBuffer (GLES20.GL_ARRAY_BUFFER, vertexBuffers[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesData.length*BYTES_PER_FLOAT, vertices, GLES20.GL_STATIC_DRAW);
            
            GLES20.glBindBuffer (GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexBuffers[1]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesData.length, indices, GLES20.GL_STATIC_DRAW);
            
            GLES20.glVertexAttribPointer(a_position_loc, 
                3,//int size, 
                GLES20.GL_FLOAT,//int type, 
                false,//boolean normalized, 
                6*BYTES_PER_FLOAT,//int stride,
                0//int offset
            );
            GLES20.glVertexAttribPointer(a_normal_loc, 
                3,//int size, 
                GLES20.GL_FLOAT,//int type, 
                false,//boolean normalized, 
                6*BYTES_PER_FLOAT,//int stride,
                3//int offset
            );
            
            GLES20.glEnableVertexAttribArray(a_position_loc);
            
            
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }
    
    public void onDrawFrame(GL10 unused)
    {
        // Combine the projection and camera view matrices
            Matrix.multiplyMM(mMVPMatrix, 0, projMatrix, 0, viewMatrix, 0);
            
        // Apply the combined projection and camera view transformations
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(u_lightPosHandle, 1, false, cameraPosition, 0);
            
            /*for(float i: viewMatrix)
                Log.e(TAG, i+"");
                Log.e(TAG, "===");*/
            
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3, GLES20.GL_UNSIGNED_BYTE, 0);
    }
    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        Log.i(TAG, "MyRenderer.onSurfaceChanged");
        float ratio = (float) width / height;
        
    // create a projection matrix from device screen geometry
        Matrix.frustumM(
            projMatrix, // m
            0,          // offset
            -ratio,     // bottom
            ratio,      // top
            -1,         // left
            1,          // right
            1,          // near
            50           // far
        );
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
    
    public void changeViewPosition(final float x, final float y)
    {
        final float
            angleX = (float)(y * Math.PI / 180),
            angleY = (float)(x * Math.PI / 180),
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
            resMatrix = new float[16];
        
        Matrix.multiplyMM(resMatrix, 0, matrixY, 0, matrixX, 0);
        
        Matrix.multiplyMV(cameraPosition, 
                0, 
                resMatrix, 
                0, 
                cameraPosition, 
                0
        );
        
        Matrix.setLookAtM(
            viewMatrix, // rm
            0,          // rmOffset
            cameraPosition[0],          // eyeX
            cameraPosition[1],       // eyeY
            cameraPosition[2],        // eyeZ
            0f,         // centerX
            0f,         // centerY
            0f,         // centerZ
            0f,         // upX
            1.0f,       // upY
            0.0f        // upZ
        );
    }
}