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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import android.opengl.Matrix;

public class MyRenderer implements Renderer
{
    protected Cube[] objects;
    protected final Camera camera = new Camera();
    //GL program
        int program;
    
    //attribute & uniform pointers
        int uMVPMatrixPointer;
        int uLightPositionPointer;
        int aNormalPointer;
        int aPositionPointer;
    
    final
        String TAG = "MyRenderer",
        
        vertexShaderSource = "precision mediump float;\n"
                           + "attribute vec3 a_Normal;\n"
                           + "attribute vec4 a_Position;\n"
                           + "uniform mat4 u_MVPMatrix;\n"
                           + "uniform vec4 u_LightPosition;\n"
                           + "varying vec4 v_color;"
                           + "void main()"
                           + "{"
                           + "gl_Position = u_MVPMatrix*a_Position;"
                           + "vec3 normal = normalize(a_Normal);"
                           + "vec3 lightDirection = normalize(vec3(u_LightPosition));"
                           + "vec3 lightColor = vec3(1.0, 1.0, 1.0);"
                           + "vec3 color = vec3(0.7, 0.7, 0.7);"
                           + "float nDotL = abs(dot(normal, lightDirection));"
                           + "vec3 diffuse = lightColor * color * nDotL;"
                           + "v_color = vec4(diffuse + vec3(0.4, 0.4, 0.4), 1.0);"
                           + "}",
        
        fragmentShaderSource = "precision mediump float;\n"
                           + "varying vec4 v_color;"
                           + ""
                           + "void main()"
                           + "{"
                           + "gl_FragColor = v_color;"
                           + ""
                           + ""
                           + ""
                           + ""
                           + "}";
    
    MyRenderer()
    {
        init();
    }
    
    protected void init()
    {
        objects = new Cube[Cube.count()];
        
        for(int i = 0; i < objects.length; i++)
            objects[i] = new Cube(i);
        
        camera.rotateAroundCenter(0, -25);
        camera.rotateAroundCenter(25, 0);
    }
    
    public Camera getCamera()
    {
        return camera;
    }
    
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        Log.i(TAG, "MyRenderer.onSurfaceCreated");
        
        try
        {
            program = createProgram();
            
            uMVPMatrixPointer = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
            uLightPositionPointer = GLES20.glGetUniformLocation(program, "u_LightPosition");
            aNormalPointer = GLES20.glGetAttribLocation(program, "a_Normal");
            aPositionPointer = GLES20.glGetAttribLocation(program, "a_Position");
            
            final int BYTES_PER_FLOAT = Float.SIZE / 8;
            
            final int VERTICES_LENGTH = objects.length * Cube.getVerticesLegth();
            final int INDICES_LENGTH = objects.length * Cube.getIndicesLegth();
            
            final int VERTICES_SIZE = VERTICES_LENGTH * BYTES_PER_FLOAT;
            final int INDICES_SIZE = INDICES_LENGTH * Cube.BYTES_PER_INDEX;
            
            FloatBuffer vertices = ByteBuffer.allocateDirect(VERTICES_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
            
            ShortBuffer indices = ByteBuffer.allocateDirect(INDICES_SIZE)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
            
            for(int i = 0; i < objects.length; i++)
            {
                vertices
                    .put(objects[i].getVertices())
                    .position((i + 1) * Cube.getVerticesLegth());
                
                indices
                    .put(objects[i].getIndices())
                    .position((i+1) * Cube.getIndicesLegth());
            }
            
            vertices.position(0);
            indices.position(0);
            
            int[] vertexBuffers = new int[2];
            
            GLES20.glGenBuffers(vertexBuffers.length, vertexBuffers, 0);
            
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffers[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VERTICES_SIZE, vertices, GLES20.GL_STATIC_DRAW);
            
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexBuffers[1]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, INDICES_SIZE, indices, GLES20.GL_STATIC_DRAW);
            
            GLES20.glVertexAttribPointer(aPositionPointer, 
                3,//int size,
                GLES20.GL_FLOAT,//int type,
                false,//boolean normalized,
                6*BYTES_PER_FLOAT,//int stride,
                0//int offset
            );
            
            GLES20.glVertexAttribPointer(aNormalPointer, 
                3,//int size,
                GLES20.GL_FLOAT,//int type,
                false,//boolean normalized,
                6*BYTES_PER_FLOAT,//int stride,
                3*BYTES_PER_FLOAT//int offset
            );
            
            GLES20.glEnableVertexAttribArray(aPositionPointer);
            
            GLES20.glEnableVertexAttribArray(aNormalPointer);
            
            GLES20.glClearColor(0.3f, 0.9f, 0.9f, 1.0f);
            
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }
    
    @Override
    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        GLES20.glUniform4fv(uLightPositionPointer, 1, camera.getEyeVec4(), 0);
        
        final int count = 4;
        int offset_step = count * Cube.BYTES_PER_INDEX;
        int offset = -offset_step;
        
        for(Cube cube : objects)
        {
            GLES20.glUniformMatrix4fv(uMVPMatrixPointer, 1, false, getMVPMatrix(cube), 0);
            
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, count, GLES20.GL_UNSIGNED_SHORT, offset += offset_step);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, count, GLES20.GL_UNSIGNED_SHORT, offset += offset_step);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, count, GLES20.GL_UNSIGNED_SHORT, offset += offset_step);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, count, GLES20.GL_UNSIGNED_SHORT, offset += offset_step);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, count, GLES20.GL_UNSIGNED_SHORT, offset += offset_step);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, count, GLES20.GL_UNSIGNED_SHORT, offset += offset_step);
        }
    }
    
    protected float[] getMVPMatrix(final Cube cube)
    {
        float[] mvp = new float[16];
        
        Matrix.multiplyMM(mvp, 0, camera.getVPMatrix(), 0, cube.getMatrix(), 0);
        
        return mvp;
    }
    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        
        camera.setSize(width, height);
    }
    
    private int loadShader(int type, String shaderSource) throws Exception
    {
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
        
        //__logShaderInfo(shader);
        
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
        
        //__logProgramInfo(program);
        
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
        
        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, params, 0);
        Log.i(TAG, "GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS: "+ params[0]);
        
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
    
    private void logMatrix(float[] matrix)
    {
        int j = 0;
        String str = "";
        
        for(int i = 0; i < matrix.length; i++)
        {
            j++;
            
            str += matrix[i] +"\t";
            
            if(j % 4 == 0)
                str += "\n";
        }
        
        str += "=============================";
        
        Log.i(TAG, str);
    }
}