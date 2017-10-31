package com.example.admin.shaderdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FindView findView = findViewById(R.id.findView);
//        findView.startFindAnimation();
        RadarView radarView = findViewById(R.id.findview);
        radarView.startFindAnimation();
    }
}
