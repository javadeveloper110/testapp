package com.example.testapp;

import android.util.Log;
import java.util.Arrays;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ByteOrder;

class Cube
{
    private final int index;
    
    private int[] current_position = new int[3];
    
    private float[] vertices_data;
    
    private final float SHIFT_STEP = 2.0f;
    
    private float rotationMatrix[] = {
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    };
    private float translationMatrix[] = {
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    };
    private float transformationMatrix[] = {
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    };
    
    public static final int BYTES_PER_INDEX = Integer.SIZE / 8;
    
    Cube(final int i)
    {
        index = i;
        
        initPosition();
        
        initVerticesData();
        
        initTranslationMatrix();
    }
    
    public void initPosition()
    {
        current_position = positions[index];
    }
    
    public void initTranslationMatrix()
    {
        Matrix.translateM(
            translationMatrix,
            0,
            current_position[0] * SHIFT_STEP,
            current_position[1] * SHIFT_STEP,
            current_position[2] * SHIFT_STEP
        );
    }
    
    public float[] getMatrix()
    {
        return translationMatrix;
    }
    
    private void initVerticesData()
    {
        FloatBuffer buffer = FloatBuffer.allocate(vertices.length);
        
        float[]
            tc_extra[] = getTexCoordsExtra(),
            part;
        
        final int
            STRIDE = getVerticesStride(),
            COLOR_OFFSET = COORDS_LENGTH + NORMAL_LENGTH,
            TEX_COORD_OFFSET = COLOR_LENGTH + COLOR_OFFSET;
        
        int
            from,
            to;
        
        float
            red_component = (float)(index + 1) / positions.length,
            green_component,
            blue_component;
        
        for(int face = 0; face < 6; face++)
        {
            green_component = (float)(face + 1) / 10;
            blue_component = (float)(6 - face) / 10;
            
            for(int vertex = 0; vertex < 4; vertex++)
            {
                from = (face * 4 + vertex) * STRIDE;
                to = from + STRIDE;
                
                part = Arrays.copyOfRange(vertices, from, to);
                
            //change grain color
                part[COLOR_OFFSET + 0] = red_component;
                part[COLOR_OFFSET + 1] = green_component;
                part[COLOR_OFFSET + 2] = blue_component;
                
            //change tex coord
                part[TEX_COORD_OFFSET + 0] += tc_extra[face][0];//s
                part[TEX_COORD_OFFSET + 1] += tc_extra[face][1];//t
                
            //put part to buffer
                buffer.position(from);
                buffer.put(part);
            }
        }
        
        vertices_data = buffer.array();
    }
    
    public float[] getVerticesData()
    {
        return vertices_data;
    }
    
    public int[] getIndicesByIndex(final int index)
    {
        int[] _indices = new int[indices.length];
        
        for(int i = 0; i < indices.length; i++)
        {
            _indices[i] = indices[i] + index * vertices.length / getVerticesStride();
        }
        
        return _indices;
    }
    
    public static short[] indices = {
        0,  1,  2,  1,  2,  3,
        4,  5,  6,  5,  6,  7,
        8,  9,  10, 9,  10, 11,
        12, 13, 14, 13, 14, 15,
        16, 17, 18, 17, 18, 19,
        20, 21, 22, 21, 22, 23,
    };
    
    public static int getVerticesStride()
    {
        return COORDS_LENGTH + NORMAL_LENGTH + COLOR_LENGTH + TEX_COORDS_LENGTH;
    }
    
    private float[][] getTexCoordsExtra()
    {
        float extra[][] = {
        //  bottom        front         left          back          right         top
            {0.0f, 0.0f}, {0.0f, 0.0f}, {0.0f, 0.0f}, {0.0f, 0.0f}, {0.0f, 0.0f}, {0.0f, 0.0f}
        };
        
        if(current_position[0] == -1)
            extra[2] = new float[]{0.75f, 0.0f};//left grain is blue
        else if(current_position[0] == 1)
            extra[4] = new float[]{0.25f, 0.0f};//right grain is green
        
        if(current_position[1] == -1)
            extra[0] = new float[]{0.5f, 0.0f};//bottom grain is red
        else if(current_position[1] == 1)
            extra[5] = new float[]{0.25f, 0.5f};//top grain is orange
        
        if(current_position[2] == -1)
            extra[1] = new float[]{0.5f, 0.5f};//front grain is yellow
        else if(current_position[2] == 1)
            extra[3] = new float[]{0.0f, 0.5f};//back grain is white
        
        return extra;
    }
    
    final static int COORDS_LENGTH = 3;
    final static int NORMAL_LENGTH = 3;
    final static int COLOR_LENGTH = 3;
    final static int TEX_COORDS_LENGTH = 2;
    
    public static float[] vertices = {
    //  coords                  normal                  //color             //tex coords
    //bottom face
       -1.0f, -1.0f,  1.0f,     0.0f, -1.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.0f,
       -1.0f, -1.0f, -1.0f,     0.0f, -1.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.5f,
        1.0f, -1.0f,  1.0f,     0.0f, -1.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.0f,
        1.0f, -1.0f, -1.0f,     0.0f, -1.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.5f,
        
    //front face
       -1.0f, -1.0f, -1.0f,     0.0f,  0.0f, -1.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.0f,
       -1.0f,  1.0f, -1.0f,     0.0f,  0.0f, -1.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.5f,
        1.0f, -1.0f, -1.0f,     0.0f,  0.0f, -1.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.0f,
        1.0f,  1.0f, -1.0f,     0.0f,  0.0f, -1.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.5f,
        
    //left face
       -1.0f, -1.0f,  1.0f,    -1.0f,  0.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.0f,
       -1.0f,  1.0f,  1.0f,    -1.0f,  0.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.5f,
       -1.0f, -1.0f, -1.0f,    -1.0f,  0.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.0f,
       -1.0f,  1.0f, -1.0f,    -1.0f,  0.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.5f,
        
    //back face
        1.0f, -1.0f,  1.0f,     0.0f,  0.0f,  1.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.0f,
        1.0f,  1.0f,  1.0f,     0.0f,  0.0f,  1.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.5f,
       -1.0f, -1.0f,  1.0f,     0.0f,  0.0f,  1.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.0f,
       -1.0f,  1.0f,  1.0f,     0.0f,  0.0f,  1.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.5f,
       
    //right face
        1.0f, -1.0f, -1.0f,     1.0f,  0.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.0f,
        1.0f,  1.0f, -1.0f,     1.0f,  0.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.5f,
        1.0f, -1.0f,  1.0f,     1.0f,  0.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.0f,
        1.0f,  1.0f,  1.0f,     1.0f,  0.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.5f,
        
    //top face
       -1.0f,  1.0f, -1.0f,     0.0f,  1.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.0f,
       -1.0f,  1.0f,  1.0f,     0.0f,  1.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.0f,  0.5f,
        1.0f,  1.0f, -1.0f,     0.0f,  1.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.0f,
        1.0f,  1.0f,  1.0f,     0.0f,  1.0f,  0.0f,     0.0f, 0.0f, 0.0f,   0.25f, 0.5f,
    };
    
    static int[][] positions = {//position xyz by index
        {-1, -1, -1}, { 0, -1, -1}, { 1, -1, -1}, {-1, -1,  0}, { 0, -1,  0}, { 1, -1,  0}, {-1, -1,  1}, { 0, -1,  1}, { 1, -1,  1},
        {-1,  0, -1}, { 0,  0, -1}, { 1,  0, -1}, {-1,  0,  0}, { 0,  0,  0}, { 1,  0,  0}, {-1,  0,  1}, { 0,  0,  1}, { 1,  0,  1},
        {-1,  1, -1}, { 0,  1, -1}, { 1,  1, -1}, {-1,  1,  0}, { 0,  1,  0}, { 1,  1,  0}, {-1,  1,  1}, { 0,  1,  1}, { 1,  1,  1},
    };
}