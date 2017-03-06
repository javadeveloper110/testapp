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
    //matrix
    private
        float[] projectionMatrix = new float[16];
        float[] MVPMatrix = new float[16];
        float[] lightPosition = new float[4];
    
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
                            +"attribute vec3 a_Normal;\n"
                            +"attribute vec4 a_Position;\n"
                            +"uniform mat4 u_MVPMatrix;\n"
                            +"uniform vec4 u_LightPosition;\n"
                            +"varying vec4 v_color;"
                            +"void main()"
                            +"{"
                            +"gl_Position = u_MVPMatrix*a_Position;"
                            //+"gl_Position = a_Position;"
                            //+"v_color = a_Normal;"
                            +"vec3 normal = normalize(a_Normal);"
                            +"vec3 lightDirection = normalize(vec3(u_LightPosition));"
                            +"vec3 lightColor = vec3(1.0, 1.0, 1.0);"
                            +"vec3 color = vec3(1.0, 0.5, 1.0);"
                            +"float nDotL = abs(dot(normal, lightDirection));"
                            +"vec3 diffuse = lightColor * color * nDotL;"
                            +"v_color = vec4(diffuse + vec3(0.2, 0.2, 0.2), 1.0);"
                            //+"v_color = vec4(color, 1.0);"
                            +"}",
        
        fragmentShaderSource = "precision mediump float;\n"
                            +"varying vec4 v_color;"
                            +""
                            +"void main()"
                            +"{"
                            +"gl_FragColor = v_color;"
                            +""
                            +""
                            +""
                            +""
                            +"}";
        
        
        //float verticesSrc[];
        
        //byte indicesSrc[];
    
    MyRenderer()
    {
        
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
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        
            final int BYTES_PER_FLOAT = Float.SIZE / 8;
            
            int[] vertexBuffers = new int[2];
            
            //verticesSrc = new float[24];
            //indicesSrc = new float[4];
            
            float[] verticesSrc = {
                -0.5f,  0.5f, -0.5f,       0,   0, -1f,
                -0.5f, -0.5f, -0.5f,       0,   0, -1f,
                 0.5f,  0.5f, -0.5f,       0,   0, -1f,
                 0.5f, -0.5f, -0.5f,       0,   0, -1f,
                 
                 0.5f,  0.5f, -0.5f,      1f,   0,   0,
                 0.5f, -0.5f, -0.5f,      1f,   0,   0,
                 0.5f,  0.5f,  0.5f,      1f,   0,   0,
                 0.5f, -0.5f,  0.5f,      1f,   0,   0,
                 
                 0.5f, -0.5f,  0.5f,       0,   0,  1f,
                 0.5f,  0.5f,  0.5f,       0,   0,  1f,
                -0.5f, -0.5f,  0.5f,       0,   0,  1f,
                -0.5f,  0.5f,  0.5f,       0,   0,  1f,
                
                -0.5f, -0.5f,  0.5f,     -1f,   0,   0,
                -0.5f,  0.5f,  0.5f,     -1f,   0,   0,
                -0.5f, -0.5f, -0.5f,     -1f,   0,   0,
                -0.5f,  0.5f, -0.5f,     -1f,   0,   0,
                
                 0.5f, -0.5f, -0.5f,       0, -1f,   0,
                 0.5f, -0.5f,  0.5f,       0, -1f,   0,
                -0.5f, -0.5f, -0.5f,       0, -1f,   0,
                -0.5f, -0.5f,  0.5f,       0, -1f,   0,
                
                 0.5f,  0.5f, -0.5f,       0,  1f,   0,
                -0.5f,  0.5f, -0.5f,       0,  1f,   0,
                 0.5f,  0.5f,  0.5f,       0,  1f,   0,
                -0.5f,  0.5f,  0.5f,       0,  1f,   0,
            };
            byte[] indicesSrc = {
                0, 1, 2, 3,
                4, 5, 6, 7,
                8, 9, 10, 11,
                12, 13, 14, 15,
                16, 17, 18, 19,
                20, 21, 22, 23,
            };
            
            FloatBuffer vertices = ByteBuffer.allocateDirect(verticesSrc.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
            
            ByteBuffer indices = ByteBuffer.allocateDirect(indicesSrc.length)
                .order(ByteOrder.nativeOrder());
            
            vertices.put(verticesSrc).position(0);
            indices.put(indicesSrc).position(0);
            
            GLES20.glGenBuffers(vertexBuffers.length, vertexBuffers, 0);
            
            GLES20.glBindBuffer (GLES20.GL_ARRAY_BUFFER, vertexBuffers[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesSrc.length*BYTES_PER_FLOAT, vertices, GLES20.GL_STATIC_DRAW);
            
            GLES20.glBindBuffer (GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexBuffers[1]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesSrc.length, indices, GLES20.GL_STATIC_DRAW);
            
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
    
    public void onDrawFrame(GL10 unused)
    {
    // Apply the combined projection and camera view transformations
        GLES20.glUniformMatrix4fv(uMVPMatrixPointer, 1, false, MVPMatrix, 0);
        GLES20.glUniform4fv(uLightPositionPointer, 1, lightPosition, 0);
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, 4);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, 8);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, 12);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, 16);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, 20);
    }
    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        
        float ratio = (float) width / height;
        
    // create a projection matrix from device screen geometry
        Matrix.frustumM(
            projectionMatrix, // m
            0,          // offset
            -ratio,     // bottom
            ratio,      // top
            -1,         // left
            1,          // right
            1,          // near
            50           // far
        );
    }
    
    public void setViewMatrix(final float[] viewMatrix, final float[] lp)
    {
    // Combine the projection and camera view matrices
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        
        lightPosition = lp;
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