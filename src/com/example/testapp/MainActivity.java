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
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        glSurfaceView = new MyGLSurfaceView(this);
        
        glSurfaceView.setEGLContextClientVersion(2);
        
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        
        glSurfaceView.setRenderer(new MyRenderer());
        
        setContentView(glSurfaceView);
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        
        glSurfaceView.onPause();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        
        glSurfaceView.onResume();
    }
}