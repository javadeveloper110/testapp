package com.example.testapp;

import android.util.Log;
import android.opengl.Matrix;

class Cube
{
    final String TAG = "MyRenderer";
    
    private final float SHIFT_STEP = 2.05f;
    
    public static final int BYTES_PER_INDEX = Short.SIZE / 8;
    
    private float transformationMatrix[] = {
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    };
    
    private static int[][] shift = {
        {-1, -1, -1}, {0, -1, -1}, {1, -1, -1}, {-1, -1,  0}, {0, -1,  0}, {1, -1,  0}, {-1, -1,  1}, {0, -1,  1}, {1, -1,  1},
        {-1,  0, -1}, {0,  0, -1}, {1,  0, -1}, {-1,  0,  0}, {0,  0,  0}, {1,  0,  0}, {-1,  0,  1}, {0,  0,  1}, {1,  0,  1},
        {-1,  1, -1}, {0,  1, -1}, {1,  1, -1}, {-1,  1,  0}, {0,  1,  0}, {1,  1,  0}, {-1,  1,  1}, {0,  1,  1}, {1,  1,  1},
    };
    
    private final int index;
    
    private static short[] indices = {
        0, 1, 2, 3,
        4, 5, 6, 7,
        8, 9, 10, 11,
        12, 13, 14, 15,
        16, 17, 18, 19,
        20, 21, 22, 23,
    };
    
    public float[] getMatrix()
    {
        return transformationMatrix;
    }
    
    private static float[] vertices = {
        -1f,  1f, -1f,       0,   0, -1f,
        -1f, -1f, -1f,       0,   0, -1f,
         1f,  1f, -1f,       0,   0, -1f,
         1f, -1f, -1f,       0,   0, -1f,
         
         1f,  1f, -1f,      1f,   0,   0,
         1f, -1f, -1f,      1f,   0,   0,
         1f,  1f,  1f,      1f,   0,   0,
         1f, -1f,  1f,      1f,   0,   0,
        
         1f, -1f,  1f,       0,   0,  1f,
         1f,  1f,  1f,       0,   0,  1f,
        -1f, -1f,  1f,       0,   0,  1f,
        -1f,  1f,  1f,       0,   0,  1f,
        
        -1f, -1f,  1f,     -1f,   0,   0,
        -1f,  1f,  1f,     -1f,   0,   0,
        -1f, -1f, -1f,     -1f,   0,   0,
        -1f,  1f, -1f,     -1f,   0,   0,
        
         1f, -1f, -1f,       0, -1f,   0,
         1f, -1f,  1f,       0, -1f,   0,
        -1f, -1f, -1f,       0, -1f,   0,
        -1f, -1f,  1f,       0, -1f,   0,
        
         1f,  1f, -1f,       0,  1f,   0,
        -1f,  1f, -1f,       0,  1f,   0,
         1f,  1f,  1f,       0,  1f,   0,
        -1f,  1f,  1f,       0,  1f,   0,
    };
    
    public final int VERTEX_STRIDE = 6;
    
    Cube(final int i)
    {
        index = i;
        
        init();
    }
    
    protected void init()
    {
        int[] t = shift[index];
        translate(t[0] * SHIFT_STEP, t[1] * SHIFT_STEP, t[2] * SHIFT_STEP);
    }
    
    public void translate(final float x, final float y, final float z)
    {
        Matrix.translateM(transformationMatrix, 0, x, y, z);
    }
    
    public static int count()
    {
        return shift.length;
    }
    
    public float[] getVertices()
    {
        return vertices;
    }
    
    public short[] getIndices()
    {
        return indices;
    }
    
    public static int getVerticesLegth()
    {
        return vertices.length;
    }
    
    public static int getIndicesLegth()
    {
        return indices.length;
    }
    
    public int[] getDrawSequence()
    {
        int s[] = {
            4,
        };
        
        return s;
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