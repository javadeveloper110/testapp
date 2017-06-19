package com.example.testapp;

import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ByteOrder;
import android.opengl.GLES20;

/**
 * Rubik's scheme
 * top view
 * coords - x, y, z
 
 TOP VIEW
 
 top layer
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1,  1,  1  |   0,  1,  1  |   1,  1,  1  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1,  1,  0  |   0,  1,  0  |   1,  1,  0  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1,  1, -1  |   0,  1, -1  |   1,  1, -1  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
 middle layer
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1,  0,  1  |   0,  0,  1  |   1,  0,  1  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1,  0,  0  |   0,  0,  0  |   1,  0,  0  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1,  0, -1  |   0,  0, -1  |   1,  0, -1  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
 bottom layer
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1, -1,  1  |   0, -1,  1  |   1, -1,  1  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1, -1,  0  |   0, -1,  0  |   1, -1,  0  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
 |              |              |              |
 |              |              |              |
 |  -1, -1, -1  |   0, -1, -1  |   1, -1, -1  |
 |              |              |              |
 |              |              |              |
 ----------------------------------------------
*/
final class RubiksCube
{
    public final Cube[] cubes = new Cube[Cube.positions.length];
    
    private FloatBuffer vertices;
    private IntBuffer indices;
    
    final String TAG = "MyRenderer";
    
    RubiksCube()
    {
        for(int index = 0; index < cubes.length; index++)
        {
            cubes[index] = new Cube(index);
        }
        
        prepareVerticesData();
    }
    
    private void prepareVerticesData()
    {
        final int BYTES_PER_FLOAT = Float.SIZE / 8;
        
        vertices = ByteBuffer.allocateDirect(cubes.length * Cube.vertices.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
        
        indices = ByteBuffer.allocateDirect(cubes.length * Cube.indices.length * Cube.BYTES_PER_INDEX)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer();
        
        for(int i = 0; i < cubes.length; i++)
        {
            vertices
                .put(cubes[i].getVerticesData())
                .position((i + 1) * Cube.vertices.length);
            
            indices
                .put(cubes[i].getIndicesByIndex(i))
                .position((i+1) * Cube.indices.length);
        }
        
        vertices.position(0);
        indices.position(0);
    }
    
    public FloatBuffer getVertices()
    {
        return vertices;
    }
    
    public IntBuffer getIndices()
    {
        return indices;
    }
    
    public void render(final int model_matrix_location)
    {
        final int offset_step = Cube.indices.length * Cube.BYTES_PER_INDEX;
        
        int offset = -offset_step;
        
        for(Cube cube : cubes)
        {
            GLES20.glUniformMatrix4fv(model_matrix_location, 1, false, cube.getMatrix(), 0);
            
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, Cube.indices.length, GLES20.GL_UNSIGNED_INT, offset += offset_step);
        }
    }
}