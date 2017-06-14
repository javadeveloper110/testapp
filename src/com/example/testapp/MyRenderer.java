package com.example.testapp;

import android.opengl.GLSurfaceView.Renderer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import java.util.Map;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Date;

import java.io.ByteArrayOutputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import android.opengl.Matrix;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class MyRenderer implements Renderer
{
    protected Context context;
    protected final RubiksCube Rubix;
    protected final Camera camera = new Camera();
    //GL program
        int mainProgram;
        int touchProgram;
    
    //framebuffers
        int[] framebuffers = new int[1];
    
    //attribute & uniform pointers
        int uMVPMatrixPointer;
        int uLightPositionPointer;
        int aNormalPointer;
        int aPositionPointer;
        int aTexCoordPointer;
        int sTexturePointer;
        int aColorPointer;
        
        float _cacheVPMatrix[] = new float[16];
    
    final String TAG = "MyRenderer";
    
    MyRenderer(Context c)
    {
        context = c;
        
        Rubix = new RubiksCube();
        
        init();
    }
    
    protected void init()
    {
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
            mainProgram = createProgram(Shader.MAIN_VERTEX_SHADER_SRC, Shader.MAIN_FRAGMENT_SHADER_SRC);
            touchProgram = createProgram(Shader.TOUCH_VERTEX_SHADER_SRC, Shader.TOUCH_FRAGMENT_SHADER_SRC);
            
            uMVPMatrixPointer = GLES20.glGetUniformLocation(mainProgram, "u_MVPMatrix");
            sTexturePointer = GLES20.glGetUniformLocation(mainProgram, "s_texture");
            uLightPositionPointer = GLES20.glGetUniformLocation(mainProgram, "u_lightPosition");
            aNormalPointer = GLES20.glGetAttribLocation(mainProgram, "a_normal");
            aPositionPointer = GLES20.glGetAttribLocation(mainProgram, "a_position");
            aTexCoordPointer = GLES20.glGetAttribLocation(mainProgram, "a_texCoord");
            aColorPointer = GLES20.glGetAttribLocation(touchProgram, "a_color");
            
            prepareVertexBuffers();
            
            GLES20.glClearColor(0.3f, 0.9f, 0.9f, 1.0f);
            
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            
        //texturing
            prepareTextures();
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }
    
    protected void prepareVertexBuffers()
    {
        final int BYTES_PER_FLOAT = Float.SIZE / 8;
        final int STRIDE = Cube.getVerticesStride() * BYTES_PER_FLOAT;
        int OFFSET = 0;
        
        int[] vertexBuffers = new int[2];
        
        GLES20.glGenBuffers(vertexBuffers.length, vertexBuffers, 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, Rubix.getVertices().limit() * BYTES_PER_FLOAT, Rubix.getVertices(), GLES20.GL_STATIC_DRAW);
        
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexBuffers[1]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, Rubix.getIndices().limit() * Cube.BYTES_PER_INDEX, Rubix.getIndices(), GLES20.GL_STATIC_DRAW);
        
        ////  coords                  normal                  //color             //tex coords
        GLES20.glVertexAttribPointer(aPositionPointer,
            Cube.COORDS_LENGTH,//int size,
            GLES20.GL_FLOAT,//int type,
            false,//boolean normalized,
            STRIDE,//int stride,
            OFFSET//int offset
        );
        
        OFFSET += Cube.COORDS_LENGTH * BYTES_PER_FLOAT;
        
        GLES20.glVertexAttribPointer(aNormalPointer,
            Cube.NORMAL_LENGTH,//int size,
            GLES20.GL_FLOAT,//int type,
            false,//boolean normalized,
            STRIDE,//int stride,
            OFFSET//int offset
        );
        
        OFFSET += Cube.NORMAL_LENGTH * BYTES_PER_FLOAT;
        OFFSET += Cube.COLOR_LENGTH * BYTES_PER_FLOAT;
        
        GLES20.glVertexAttribPointer(aTexCoordPointer, 
            2,//int size,
            GLES20.GL_FLOAT,//int type,
            false,//boolean normalized,
            STRIDE,//int stride,
            OFFSET//int offset
        );
        
        GLES20.glEnableVertexAttribArray(aPositionPointer);
        GLES20.glEnableVertexAttribArray(aNormalPointer);
        GLES20.glEnableVertexAttribArray(aTexCoordPointer);
    }
    
    protected void prepareFrameBuffer() throws Exception
    {
        if(GLES20.glIsFramebuffer(framebuffers[0]))
            GLES20.glDeleteFramebuffers(framebuffers.length, framebuffers, 0);
        
        int[] renderbuffers = new int[1];
        
        GLES20.glGenRenderbuffers(renderbuffers.length, renderbuffers, 0);
        
        GLES20.glGenFramebuffers(framebuffers.length, framebuffers, 0);
        
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderbuffers[0]);
        
        GLES20.glRenderbufferStorage(
            GLES20.GL_RENDERBUFFER,
            GLES20.GL_RGB565,
            camera.getWidth(),
            camera.getHeight()
        );
        
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffers[0]);
        
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_RENDERBUFFER,
            renderbuffers[0]
        );
        
        checkFramebufferStatus();
    }
    
    protected void checkFramebufferStatus() throws Exception
    {
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        
        switch(status)
        {
            case GLES20.GL_FRAMEBUFFER_COMPLETE:
                Log.i(TAG, "Framebuffer is complete");
            break;
            
            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                throw new Exception("The framebuffer attachment points are not complete");
            
            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                throw new Exception("Attachments do not have the same width and height");
            
            case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
                throw new Exception("Combination of internal formats used by attachments in the frmabuffer results in a nonrenderable target");
            
            default:
                throw new Exception("Undefined framebuffer status");
        }
    }
    
    protected void prepareTextures()
    {
        Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.tex1)).getBitmap();
        
        int[] textures = new int[1];
        final int FORMAT = GLES20.GL_RGBA;
        ByteBuffer b = getPixelsBuffer(bitmap);
        
        GLES20.glGenTextures(textures.length, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE); 
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        
        GLES20.glUniform1i(sTexturePointer, GLES20.GL_TEXTURE0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    }
    
    protected ByteBuffer getPixelsBuffer(Bitmap bitmap)
    {
        ByteBuffer b = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(b);
        
        return b;
    }
    
    @Override
    public void onDrawFrame(GL10 unused)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffers[0]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        cacheVPMatrix();
        
        GLES20.glUseProgram(mainProgram);
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        GLES20.glUniform4fv(uLightPositionPointer, 1, camera.getEyeVec4(), 0);
        
        final int count = Cube.indices.length;
        int offset_step = count * Cube.BYTES_PER_INDEX;
        int offset = -offset_step;
        
        
        
        for(Cube cube : Rubix.cubes)
        {
            GLES20.glUniformMatrix4fv(uMVPMatrixPointer, 1, false, getMVPMatrix(cube), 0);
            
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, Cube.indices.length, GLES20.GL_UNSIGNED_INT, offset += offset_step);
        }
    }
    
    protected void cacheVPMatrix()
    {
        _cacheVPMatrix = camera.getVPMatrix().clone();
    }
    
    protected float[] getMVPMatrix(final Cube cube)
    {
        float[] mvp = new float[16];
        
        Matrix.multiplyMM(mvp, 0, _cacheVPMatrix, 0, cube.getMatrix(), 0);
        
        return mvp;
    }
    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        
        camera.setSize(width, height);
        
        try {
            prepareFrameBuffer();
            Log.i(TAG, "FBO name: "+ framebuffers[0]);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
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
    
    private int createProgram(final String vShaderSource, final String fShaderSource) throws Exception
    {
         int
            program = GLES20.glCreateProgram(),
            params[] = new int[1];
        
        if(program == 0)
            throw new Exception("can't create a gl program object");
        
        GLES20.glAttachShader(program, loadShader(GLES20.GL_VERTEX_SHADER, vShaderSource));
        GLES20.glAttachShader(program, loadShader(GLES20.GL_FRAGMENT_SHADER, fShaderSource));
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