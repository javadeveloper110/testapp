package com.example.testapp;

class Cube
{
    public final int VERTEX_STRIDE = 6;
    
    public float[] getVertices()
    {
        float v[] = {
            -1f,  1f, -1f, 0, 0, -1f,
            -1f, -1f, -1f, 0, 0, -1f,
             1f,  1f, -1f, 0, 0, -1f,
             1f, -1f, -1f, 0, 0, -1f,
        };
        
        return v;
    }
    
    public byte[] getIndices()
    {
        byte i[] = {
            0, 1, 2, 3,
        };
        
        return i;
    }
    
    public int[] getDrawSequence()
    {
        int s[] = {
            4,
        };
        
        return s;
    }
}