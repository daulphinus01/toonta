package com.toonta.app.utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.toonta.app.R;

public class ProfileActivit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}
