package com.toonta.app.forms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.activities.signup.SignupInteractor;
import com.toonta.app.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {

    /*****************************************************
     *              TOONTA PROPERTIES                    *
     *****************************************************/
    private TextView password;
    private TextView passwordConf;
    private ImageView toontaLogo;
    private TextView phoneNumber;
    private ProgressDialog progressDialog;
    private SignupInteractor signupInteractor;
    private static final String TAG = "SignupActivity";
    public static String NEED_FIRST_USE_SLIDER = "NEED_FIRST_USE_SLIDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneNumber = (TextView) findViewById(R.id.phone_number_sign_up);
        password = (TextView) findViewById(R.id.password_sign_up);
        passwordConf = (TextView) findViewById(R.id.password_confirm);

        toontaLogo = (ImageView) findViewById(R.id.toonta_logo);
        loadLogo();

        Button toontaSignUp = (Button) findViewById(R.id.toonta_sign_up);
        assert toontaSignUp != null;
        toontaSignUp.setTransformationMethod(null);
        toontaSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPwdNullOrEmpty = password.getText().toString() == null || password.getText().toString().isEmpty();
                if (isPwdNullOrEmpty
                        || !Utils.bothPwdHaveToBeTheSame(password.getText().toString(),
                                passwordConf.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Password wrong format", Toast.LENGTH_LONG).show();
                    return;
                }
                signupInteractor.submitSignup(phoneNumber.getText().toString(), password.getText().toString());
            }
        });

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.string_sign_up_activity_creating_new_account));

        signupInteractor = new SignupInteractor(getApplicationContext(), new SignupInteractor.SignupViewUpdater() {
            @Override
            public void updateView(SignupInteractor.SignupViewModel signupViewModel){
                if (signupViewModel.phoneNumberError.equals("")) {
                    phoneNumber.setError(null);
                } else {
                    phoneNumber.setError(signupViewModel.phoneNumberError);
                }
                if (signupViewModel.passwordError.equals("")) {
                    password.setError(null);
                } else {
                    password.setError(signupViewModel.passwordError);
                }
            }

            @Override
            public void onSignupSuccess() {
                progressDialog.dismiss();
                Intent homeConIntent = new Intent(getApplicationContext(), HomeConnectedActivity.class);
                homeConIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeConIntent);
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

/*    @Override
    public void onResume() {
        super.onResume();
        if (!ToontaSharedPreferences.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), ToontaLogin.class);
            if (!ToontaSharedPreferences.isFistUseSliderValidated()) {
                intent.putExtra(NEED_FIRST_USE_SLIDER, true);
            }
            startActivity(intent);
        } else {
            startActivity(new Intent(getApplicationContext(), HomeConnectedActivity.class));
        }
        finish();
    }*/

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
