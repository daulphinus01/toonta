/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.toonta.app.com.toonta.app.forms.ToontaLogin;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [03/05/2016]
 */
public class HomePageActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;

    private Context context;

    // Images used on home screen
    private ImageView screen1ImgTop;
    private ImageView screen1ImgBottom;
    private ImageView screen2ImgTop;
    private ImageView screen2ImgBottom;
    private ImageView screen3ImgTop;
    private ImageView screen3ImgBottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);

        Iconify.with(new EntypoModule());

        context = this;

        // Disabling actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        screen1ImgTop = (ImageView) findViewById(R.id.screen1_img_top);
        screen1ImgBottom = (ImageView) findViewById(R.id.screen1_img_bottom);
        screen2ImgTop = (ImageView) findViewById(R.id.screen2_img_top);
        screen2ImgBottom = (ImageView) findViewById(R.id.screen2_img_bottom);
        screen3ImgTop = (ImageView) findViewById(R.id.screen3_img_top);
        screen3ImgBottom = (ImageView) findViewById(R.id.screen3_img_bottom);

        Button skipThisButton = (Button) findViewById(R.id.skip_this_button);
        skipThisButton.setTransformationMethod(null);

        AppCompatImageButton toontaLogoButton = (AppCompatImageButton) findViewById(R.id.toonta_logo);
        toontaSetOnClickListener(toontaLogoButton);
        toontaSetOnClickListener(skipThisButton);

        // Loading Home screen images
        loadImages();

        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        if (viewFlipper != null) {
            viewFlipper.startFlipping();
        }


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // findViewById(R.id.toonta_logo).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (viewFlipper != null) {
            viewFlipper.stopFlipping();
            viewFlipper = null;
        }
    }

    @Override
    protected void onResume() {
        if (viewFlipper != null) {
            if (!viewFlipper.isFlipping()) {
                viewFlipper.startFlipping();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (viewFlipper != null) {
            viewFlipper.stopFlipping();
        }
        super.onPause();
    }

    /**
     * Loads images from /assets
     */
    private void loadImages() {
        try {
            // get input stream
            InputStream ims1 = getAssets().open("image1.png");
            InputStream ims2 = getAssets().open("image2.png");
            InputStream ims3 = getAssets().open("image3.png");
            InputStream ims4 = getAssets().open("image4.png");
            InputStream ims5 = getAssets().open("image5.png");
            InputStream ims6 = getAssets().open("image6.png");

            // load image as Drawable and setting images
            screen1ImgTop.setImageDrawable(Drawable.createFromStream(ims1, null));
            screen1ImgBottom.setImageDrawable(Drawable.createFromStream(ims2, null));
            screen2ImgTop.setImageDrawable(Drawable.createFromStream(ims3, null));
            screen2ImgBottom.setImageDrawable(Drawable.createFromStream(ims4, null));
            screen3ImgTop.setImageDrawable(Drawable.createFromStream(ims5, null));
            screen3ImgBottom.setImageDrawable(Drawable.createFromStream(ims6, null));
        } catch(IOException ignored) {

        }
    }

    private void toontaSetOnClickListener(View button) {
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(context, ToontaLogin.class);
                    startActivity(loginIntent);
                }
            });
        }
    }
}
