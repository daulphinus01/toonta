package com.toonta.app.forms;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.activities.login.LoginInteractor;

import java.io.IOException;
import java.io.InputStream;

/**
 * A login screen that offers login via email/password.
 */
public class ToontaLogin extends AppCompatActivity{

    /*****************************************************
     *              TOONTA PROPERTIES                    *
     *****************************************************/

    private ImageView toontaLogo;
    private EditText passwordView;
    private ProgressDialog progressDialog;
    private AutoCompleteTextView loginView;
    private LoginInteractor loginInteractor;
    public static String NEED_FIRST_USE_SLIDER = "NEED_FIRST_USE_SLIDER";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toonta_login);
        setupActionBar();

        toontaLogo = (ImageView) findViewById(R.id.toonta_logo);
        loadLogo();

        // Set up the login form.
        loginView = (AutoCompleteTextView) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);

        // Sign up button
        Button toontaSignUp = (Button) findViewById(R.id.toonta_sign_up);
        assert toontaSignUp != null;
        toontaSignUp.setTransformationMethod(null);
        toontaSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });


        // Sign in button
        Button toontaSignIn = (Button) findViewById(R.id.toonta_sign_in);
        assert toontaSignIn != null;
        toontaSignIn.setTransformationMethod(null);
        toontaSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginInteractor.submitLogin(loginView.getText().toString(), passwordView.getText().toString());
            }
        });

        // Progress mechanisme when verifying credential
        progressDialog = new ProgressDialog(ToontaLogin.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.string_login_activity_authenticating));

        // Logging mechanisme
        loginInteractor = new LoginInteractor(getApplicationContext(), new LoginInteractor.LoginViewUpdater() {
            @Override
            public void updateView(LoginInteractor.LoginViewModel loginViewModel) {
                if (loginViewModel.emailError.equals("")) {
                    loginView.setError(null);
                } else {
                    loginView.setError(loginViewModel.emailError);
                }
                if (loginViewModel.passwordError.equals("")) {
                    passwordView.setError(null);
                } else {
                    passwordView.setError(loginViewModel.passwordError);
                }
            }

            @Override
            public void onLoginProgress() {
                progressDialog.show();
            }

            @Override
            public void onLoginSuccess() {
                progressDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), HomeConnectedActivity.class));
                finish();
            }

            @Override
            public void onLoginFailure(String errorToDisplay) {
                progressDialog.dismiss();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passwordView.getWindowToken(), 0);
                Toast.makeText(getApplicationContext(), "Wrong login and / or password", Toast.LENGTH_LONG);
                Snackbar.make(findViewById(android.R.id.content), errorToDisplay, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onStop() {
        this.finish();
        super.onStop();
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

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }
}