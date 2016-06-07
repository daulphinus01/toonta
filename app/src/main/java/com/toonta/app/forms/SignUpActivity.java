package com.toonta.app.forms;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import com.toonta.app.R;

import java.io.IOException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {


    private ImageView toontaLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toontaLogo = (ImageView) findViewById(R.id.toonta_logo);
        loadLogo();

        Button toontaSignUp = (Button) findViewById(R.id.toonta_sign_up);
        assert toontaSignUp != null;
        toontaSignUp.setTransformationMethod(null);
    }

    private void loadLogo() {
        try {
            // get input stream
            InputStream ims1 = getAssets().open("toonta_logo_3.png");

            // load image as Drawable and setting images
            toontaLogo.setImageDrawable(Drawable.createFromStream(ims1, null));

        } catch(IOException ignored) {

        }
    }
}
