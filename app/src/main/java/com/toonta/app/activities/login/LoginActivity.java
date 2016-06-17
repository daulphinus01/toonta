package com.toonta.app.activities.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.toonta.app.R;
import com.toonta.app.activities.FirstUseSlider;
import com.toonta.app.activities.LoadingActivity;
import com.toonta.app.activities.dashboard.DashboardActivity;
import com.toonta.app.activities.signup.SignupActivity;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    private LoginInteractor loginInteractor;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumberEditText = (EditText) findViewById(R.id.login_activity_edit_text_email);
        passwordEditText = (EditText) findViewById(R.id.login_activity_edit_text_password);

        loginButton = (Button) findViewById(R.id.login_activity_button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginInteractor.submitLogin(phoneNumberEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        signupButton = (Button) findViewById(R.id.login_activity_button_sign_up);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.string_login_activity_authenticating));

        loginInteractor = new LoginInteractor(getApplicationContext(), new LoginInteractor.LoginViewUpdater() {
            @Override
            public void updateView(LoginInteractor.LoginViewModel loginViewModel) {
                if (loginViewModel.emailError.equals("")) {
                    phoneNumberEditText.setError(null);
                } else {
                    phoneNumberEditText.setError(loginViewModel.emailError);
                }
                if (loginViewModel.passwordError.equals("")) {
                    passwordEditText.setError(null);
                } else {
                    passwordEditText.setError(loginViewModel.passwordError);
                }
            }

            @Override
            public void onLoginProgress() {
                progressDialog.show();
            }

            @Override
            public void onLoginSuccess() {
                progressDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                finish();
            }

            @Override
            public void onLoginFailure(String errorToDisplay) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), errorToDisplay, Snackbar.LENGTH_LONG).show();
            }
        });

        if (getIntent().hasExtra(LoadingActivity.NEED_FIRST_USE_SLIDER) && getIntent().getExtras().getBoolean(LoadingActivity.NEED_FIRST_USE_SLIDER, false)) {
            startActivity(new Intent(getApplicationContext(), FirstUseSlider.class));
        }

    }
}