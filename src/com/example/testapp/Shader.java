package com.example.testapp;

public class Shader
{
    final static String
        MAIN_VERTEX_SHADER_SRC = "precision mediump float;\n"
                           + "attribute vec2 a_texCoord;\n"
                           + "attribute vec3 a_normal;\n"
                           + "attribute vec4 a_position;\n"
                           + "uniform mat4 u_VPMatrix;\n"
                           + "uniform mat4 u_ModelMatrix;\n"
                           + "uniform vec4 u_lightPosition;\n"
                           + "varying vec4 v_diffuse;"
                           + "varying vec2 v_texCoord;"
                           + "void main()"
                           + "{"
                           + "  mat4 MVPatrix = u_VPMatrix * u_ModelMatrix;"
                           + "  v_texCoord = a_texCoord;"
                           + "  gl_Position = MVPatrix * a_position;"
                           + "  vec3 normal = normalize(a_normal);"
                           + "  vec3 lightDirection = normalize(vec3(u_lightPosition));"
                           + "  vec4 lightColor = vec4(1.0, 1.0, 1.0, 1.0);"
                           + "  float nDotL = abs(dot(normal, lightDirection));"
                           + "  v_diffuse = lightColor * nDotL;"
                           + "}",
        
        MAIN_FRAGMENT_SHADER_SRC = "precision mediump float;\n"
                           + "varying vec4 v_diffuse;"
                           + "varying vec2 v_texCoord;"
                           + "uniform sampler2D s_texture;"
                           + "void main()"
                           + "{"
                           + "  gl_FragColor = texture2D(s_texture, v_texCoord);"
                           + "}",
        
        TOUCH_VERTEX_SHADER_SRC = "precision highp float;\n"
                           + "attribute vec4 a_position;\n"
                           + "attribute vec4 a_color;\n"
                           + "uniform mat4 u_VPMatrix;\n"
                           + "uniform mat4 u_ModelMatrix;\n"
                           + "varying vec4 v_color;"
                           + "void main()"
                           + "{"
                           + "  mat4 MVPatrix = u_VPMatrix * u_ModelMatrix;"
                           + "  gl_Position = MVPatrix * a_position;"
                           + "  v_color = a_color;"
                           + "}",
        
        TOUCH_FRAGMENT_SHADER_SRC = "precision highp float;\n"
                           + "varying vec4 v_color;"
                           + "void main()"
                           + "{"
                           + "  gl_FragColor = v_color;"
                           + "}";
}