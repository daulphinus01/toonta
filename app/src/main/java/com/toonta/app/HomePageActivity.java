/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.crashlytics.android.Crashlytics;
import com.toonta.app.forms.ToontaLogin;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [03/05/2016]
 */
public class HomePageActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private float lastX;

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
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_home_page);

        ToontaSharedPreferences.init(getApplicationContext());
        ToontaDAO.init(getApplicationContext());

        final TextView[] toontaDots = new TextView[3];
        toontaDots[0] = (TextView) findViewById(R.id.toonta_dot_1);
        toontaDots[1] = (TextView) findViewById(R.id.toonta_dot_2);
        toontaDots[2] = (TextView) findViewById(R.id.toonta_dot_3);

        for (int i = 0; i < toontaDots.length; i++) {
            final int tmp = i;
            toontaDots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (viewFlipper.getCurrentView().getId()) {
                        case R.id.screen1 :
                            if (tmp == 1) {
                                viewFlipper.showNext();
                            } else if (tmp == 2) {
                                viewFlipper.showNext();
                                viewFlipper.showNext();
                            }
                            break;
                        case R.id.screen2 :
                            if (tmp == 0) {
                                viewFlipper.showPrevious();
                            } else if (tmp == 2) {
                                viewFlipper.showNext();
                            }
                            break;
                        case R.id.screen3 :
                            if (tmp == 0) {
                                viewFlipper.showPrevious();
                                viewFlipper.showPrevious();
                            } else if (tmp == 1) {
                                viewFlipper.showPrevious();
                            }
                            break;
                    }
                }
            });
        }


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
        assert skipThisButton != null;
        skipThisButton.setTransformationMethod(null);

        AppCompatImageButton toontaLogoButton = (AppCompatImageButton) findViewById(R.id.toonta_logo);
        toontaSetOnClickListener(toontaLogoButton);
        toontaSetOnClickListener(skipThisButton);

        // Loading Home screen images
        loadImages();

        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        if (viewFlipper != null) {
            viewFlipper.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    switch (viewFlipper.getCurrentView().getId()) {
                        case R.id.screen1 :
                            toontaDots[0].setTextColor(Color.WHITE);
                            toontaDots[1].setTextColor(Color.BLACK);
                            toontaDots[2].setTextColor(Color.BLACK);
                            break;
                        case R.id.screen2 :
                            toontaDots[0].setTextColor(Color.BLACK);
                            toontaDots[1].setTextColor(Color.WHITE);
                            toontaDots[2].setTextColor(Color.BLACK);
                            break;
                        case R.id.screen3 :
                            toontaDots[0].setTextColor(Color.BLACK);
                            toontaDots[1].setTextColor(Color.BLACK);
                            toontaDots[2].setTextColor(Color.WHITE);
                            break;
                    }
                }
            });
            viewFlipper.setFlipInterval(4000);
            viewFlipper.startFlipping();

            viewFlipper.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN :
                            lastX = event.getX();
                            return true;

                        case MotionEvent.ACTION_UP :
                            float currentX = event.getX();
                            // Handling left to right screen swap.
                            if (lastX < currentX) {
                                // If there aren't any other children, just break.
                                if (viewFlipper.getDisplayedChild() == 0)
                                    break;
                                viewFlipper.showNext();
                            }
                            // Handling right to left screen swap.
                            if (lastX > currentX) {
                                // If there is a child (to the left), kust break.
                                if (viewFlipper.getDisplayedChild() == 1)
                                    break;
                                // Display previous screen.
                                viewFlipper.showPrevious();
                            }
                            return true;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
                    Intent intent = null;
                    if (ToontaSharedPreferences.isLoggedIn()) {
                        intent = new Intent(context, HomeConnectedActivity.class);
                    } else {
                        intent = new Intent(context, ToontaLogin.class);
                    }
                    startActivity(intent);
                }
            });
        }
    }
}
