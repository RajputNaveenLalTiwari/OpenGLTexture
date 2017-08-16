package com.example.workingonopengl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    private GLSurfaceView view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);
        view.setRenderer(new MyRenderer(this));
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setContentView(view);
    }
}
