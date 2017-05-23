package com.example.testapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    final String TAG = "MyRenderer";
    
    private
        MyGLSurfaceView glSurfaceView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "MainActivity.onCreate");
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        glSurfaceView = new MyGLSurfaceView(this);
        
        glSurfaceView.setEGLContextClientVersion(2);
        
        glSurfaceView.setRenderer(new MyRenderer());
        
        setContentView(glSurfaceView);
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        //Log.i(TAG, "MainActivity.onPause");
        glSurfaceView.onPause();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //Log.i(TAG, "MainActivity.onDestroy");
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        //Log.i(TAG, "MainActivity.onResume");
        glSurfaceView.onResume();
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        //Log.i(TAG, "MainActivity.onRestoreInstanceState");
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        //Log.i(TAG, "MainActivity.onSaveInstanceState");
    }
}