package com.toonta.app.activities.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.toonta.app.R;
import com.toonta.app.activities.dashboard.DashboardActivity;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private Button signupButton;

    private SignupInteractor signupInteractor;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        phoneNumberEditText = (EditText) findViewById(R.id.signup_activity_edit_text_phone_number);
        passwordEditText = (EditText) findViewById(R.id.signup_activity_edit_text_password);

        signupButton = (Button) findViewById(R.id.signup_activity_button_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupInteractor.submitSignup(phoneNumberEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.string_sign_up_activity_creating_new_account));

        signupInteractor = new SignupInteractor(getApplicationContext(), new SignupInteractor.SignupViewUpdater() {
            @Override
            public void updateView(SignupInteractor.SignupViewModel signupViewModel){
                if (signupViewModel.phoneNumberError.equals("")) {
                    phoneNumberEditText.setError(null);
                } else {
                    phoneNumberEditText.setError(signupViewModel.phoneNumberError);
                }
                if (signupViewModel.passwordError.equals("")) {
                    passwordEditText.setError(null);
                } else {
                    passwordEditText.setError(signupViewModel.passwordError);
                }
            }

            @Override
            public void onSignupSuccess() {
                progressDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                finish();
            }

            @Override
            public void onSignupProgress() {
                progressDialog.show();
            }

            @Override
            public void onSignupFailure(String errorToDisplay) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), errorToDisplay, Snackbar.LENGTH_LONG).show();
            }
        });
    }


}
