package com.example.testapp;

import android.opengl.GLSurfaceView.Renderer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import java.util.Date;

import java.io.ByteArrayOutputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
    protected GLProgram
        mainProgram,
        touchProgram;
    
//framebuffers
    int[] framebuffers = new int[1];
    int[] renderbuffers = new int[1];
    
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
            mainProgram = new GLProgram(Shader.MAIN_VERTEX_SHADER_SRC, Shader.MAIN_FRAGMENT_SHADER_SRC);
            touchProgram = new GLProgram(Shader.TOUCH_VERTEX_SHADER_SRC, Shader.TOUCH_FRAGMENT_SHADER_SRC);
            
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
    
    protected void prepareVertexBuffers() throws Exception
    {
        final int BYTES_PER_FLOAT = Float.SIZE / 8;
        final int STRIDE = Cube.getVerticesStride() * BYTES_PER_FLOAT;
        
        int[] vertexBuffers = new int[2];
        
        GLES20.glGenBuffers(vertexBuffers.length, vertexBuffers, 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, Rubix.getVertices().limit() * BYTES_PER_FLOAT, Rubix.getVertices(), GLES20.GL_STATIC_DRAW);
        
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexBuffers[1]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, Rubix.getIndices().limit() * Cube.BYTES_PER_INDEX, Rubix.getIndices(), GLES20.GL_STATIC_DRAW);
        
    //to main program
        int OFFSET = 0;
        
        GLES20.glVertexAttribPointer(mainProgram.getAttributeLocation("a_position"),
            Cube.COORDS_LENGTH,//int size,
            GLES20.GL_FLOAT,//int type,
            false,//boolean normalized,
            STRIDE,//int stride,
            OFFSET//int offset
        );
        
        OFFSET += Cube.COORDS_LENGTH * BYTES_PER_FLOAT;
        
        GLES20.glVertexAttribPointer(mainProgram.getAttributeLocation("a_normal"),
            Cube.NORMAL_LENGTH,//int size,
            GLES20.GL_FLOAT,//int type,
            false,//boolean normalized,
            STRIDE,//int stride,
            OFFSET//int offset
        );
        
        OFFSET += Cube.NORMAL_LENGTH * BYTES_PER_FLOAT;
        OFFSET += Cube.COLOR_LENGTH * BYTES_PER_FLOAT;
        
        GLES20.glVertexAttribPointer(mainProgram.getAttributeLocation("a_texCoord"),
            2,//int size,
            GLES20.GL_FLOAT,//int type,
            false,//boolean normalized,
            STRIDE,//int stride,
            OFFSET//int offset
        );
        
        GLES20.glEnableVertexAttribArray(mainProgram.getAttributeLocation("a_position"));
        GLES20.glEnableVertexAttribArray(mainProgram.getAttributeLocation("a_normal"));
        GLES20.glEnableVertexAttribArray(mainProgram.getAttributeLocation("a_texCoord"));
        
    //to touch program
        OFFSET = 0;
        
        GLES20.glVertexAttribPointer(touchProgram.getAttributeLocation("a_position"),
            Cube.COORDS_LENGTH,//int size,
            GLES20.GL_FLOAT,//int type,
            false,//boolean normalized,
            STRIDE,//int stride,
            OFFSET//int offset
        );
        
        OFFSET += (Cube.COORDS_LENGTH + Cube.NORMAL_LENGTH) * BYTES_PER_FLOAT;
        
        GLES20.glVertexAttribPointer(touchProgram.getAttributeLocation("a_color"),
            Cube.COLOR_LENGTH,//int size,
            GLES20.GL_FLOAT,//int type,
            false,//boolean normalized,
            STRIDE,//int stride,
            OFFSET//int offset
        );
        
        GLES20.glEnableVertexAttribArray(touchProgram.getAttributeLocation("a_position"));
        GLES20.glEnableVertexAttribArray(touchProgram.getAttributeLocation("a_color"));
        
        Log.i(TAG, "mainProgram.getAttributeLocation(\"a_position\"): "+ mainProgram.getAttributeLocation("a_position"));
        Log.i(TAG, "touchProgram.getAttributeLocation(\"a_color\"): "+ touchProgram.getAttributeLocation("a_color"));
    }
    
    protected void renderToFBO()
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffers[0]);
        //GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        
        touchProgram.use();
        
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //Log.i(TAG, "u_VPMatrix: "+ touchProgram.getUniformLocation("u_VPMatrix"));
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        GLES20.glUniform4fv(touchProgram.getUniformLocation("u_lightPosition"), 1, camera.getEyeVec4(), 0);
        
        GLES20.glUniformMatrix4fv(touchProgram.getUniformLocation("u_VPMatrix"), 1, false, camera.getVPMatrix(), 0);
        
        Rubix.render(touchProgram.getUniformLocation("u_ModelMatrix"));
    }
    
    protected void getPixRgbColor(final int x, final int y)
    {
        renderToFBO();
            
        readPixel(x, y);
    }
    
    protected void readPixel(final int x, final int y)
    {
        ShortBuffer pixels = ByteBuffer.allocateDirect(Short.SIZE/8).order(ByteOrder.nativeOrder()).asShortBuffer();
        
        pixels.position(0);
        
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffers[0]);
        //Log.i(TAG, "fbid: "+ framebuffers[0]);
        GLES20.glReadPixels(
            x,
            y,
            1,
            1,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_SHORT_5_5_5_1,
            pixels
        );
        
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        
        //short _color5551 = pixels.get(0);
        int color5551 = pixels.get(0) & 0xffff;
        Log.i(TAG, "color5551: "+ color5551);
        int[] rgb = {
            color5551 >>> 11,
            (color5551 << 21) >>> 27,
            (color5551 << 26) >>> 27,
        };
        
        Log.i(TAG, "rgb: "+ rgb[0] +", "+ rgb[1] +", "+ rgb[2]);
    }
    
    protected void prepareFrameBuffer(final int width, final int height) throws Exception
    {
        if(GLES20.glIsRenderbuffer(renderbuffers[0]))
            GLES20.glDeleteRenderbuffers(renderbuffers.length, renderbuffers, 0);
        if(GLES20.glIsFramebuffer(framebuffers[0]))
            GLES20.glDeleteFramebuffers(framebuffers.length, framebuffers, 0);
        
        GLES20.glGenRenderbuffers(renderbuffers.length, renderbuffers, 0);
        
        GLES20.glGenFramebuffers(framebuffers.length, framebuffers, 0);
        
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderbuffers[0]);
        
        GLES20.glRenderbufferStorage(
            GLES20.GL_RENDERBUFFER,
            GLES20.GL_RGB5_A1,
            width,
            height
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
                throw new Exception("Undefined framebuffer status: "+ status);
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
        
        GLES20.glUniform1i(mainProgram.getUniformLocation("s_texture"), GLES20.GL_TEXTURE0);
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
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        
        mainProgram.use();
        
        GLES20.glClearColor(0.3f, 0.9f, 0.9f, 1.0f);
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        GLES20.glUniform4fv(mainProgram.getUniformLocation("u_lightPosition"), 1, camera.getEyeVec4(), 0);
        
        GLES20.glUniformMatrix4fv(mainProgram.getUniformLocation("u_VPMatrix"), 1, false, camera.getVPMatrix(), 0);
        
        Rubix.render(mainProgram.getUniformLocation("u_ModelMatrix"));
    }
    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        
        camera.setSize(width, height);
        
        try
        {
            prepareFrameBuffer(camera.getWidth(),camera.getHeight());
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
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