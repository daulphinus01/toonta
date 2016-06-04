/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.io.InputStream;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomePageActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private View mControlsView;

    private ViewFlipper viewFlipper;

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

        // mVisible = true;
        // mControlsView = findViewById(R.id.fullscreen_content_controls);
        // mContentView = findViewById(R.id.fullscreen_content);

        // Disabling actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        screen1ImgTop = (ImageView) findViewById(R.id.screen1_img_top);
        screen1ImgBottom = (ImageView) findViewById(R.id.screen1_img_bottom);
        screen2ImgTop = (ImageView) findViewById(R.id.screen2_img_top);
        screen2ImgBottom = (ImageView) findViewById(R.id.screen2_img_bottom);
        screen3ImgTop = (ImageView) findViewById(R.id.screen3_img_top);
        screen3ImgBottom = (ImageView) findViewById(R.id.screen3_img_bottom);
        loadImages();

        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        viewFlipper.startFlipping();


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // findViewById(R.id.toonta_logo).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        // delayedHide(100);
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewFlipper.stopFlipping();
        viewFlipper = null;
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
        }
        catch(IOException ignored) {

        }
    }
}
