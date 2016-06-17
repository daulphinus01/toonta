package com.toonta.app.utils;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.toonta.app.R;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.forms.ToontaUser;

public class ProfileActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ToontaUserInterceptor toontaUserInterceptor;

    private EditText firstName;
    private EditText lastName;
    private EditText birthDate;
    private EditText emailAddress;
    private EditText phoneNumber;
    private EditText professionalActivity;
    private EditText residencePlace;
    private EditText cumulatedPoint;
    private EditText labelThree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = (EditText) findViewById(R.id.first_name);
        assert firstName != null;
        firstName.setEnabled(false);

        lastName = (EditText) findViewById(R.id.last_name);
        assert lastName != null;
        lastName.setEnabled(false);

        birthDate = (EditText) findViewById(R.id.birth_date);
        assert birthDate != null;
        birthDate.setEnabled(false);

        emailAddress = (EditText) findViewById(R.id.email_address);
        assert emailAddress != null;
        emailAddress.setEnabled(false);

        phoneNumber = (EditText) findViewById(R.id.phone_number);
        assert phoneNumber != null;
        phoneNumber.setEnabled(false);

        professionalActivity = (EditText) findViewById(R.id.professional_activity);
        assert professionalActivity != null;
        professionalActivity.setEnabled(false);

        residencePlace = (EditText) findViewById(R.id.residence_place);
        assert residencePlace != null;
        residencePlace.setEnabled(false);

        cumulatedPoint = (EditText) findViewById(R.id.toonta_cumilated_points);
        assert cumulatedPoint != null;
        cumulatedPoint.setEnabled(false);

        labelThree = (EditText) findViewById(R.id.label_three);
        assert labelThree != null;
        labelThree.setEnabled(false);


        toontaUserInterceptor = new ToontaUserInterceptor(getBaseContext(), new ToontaUserInterceptor.ToontaUserViewUpdater() {
            @Override
            public void onToontaUserGet(ToontaUser toontaUser) {
                Log.v("ProfileActivity ", toontaUser.toString());

                firstName.setText(toontaUser.firstname);
                lastName.setText(toontaUser.lastname);
                birthDate.setText(toontaUser.birthdate);
                emailAddress.setText(toontaUser.email);
                phoneNumber.setText(toontaUser.phoneNumber);
                professionalActivity.setText(toontaUser.profession);
                residencePlace.setText(toontaUser.address.city);
                //cumulatedPoint.setText(toontaUser.bank_.balance);
                labelThree.setText(toontaUser.lastname);
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });

        toontaUserInterceptor.fetchToontaUser(ToontaSharedPreferences.toontaSharedPreferences.userId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}
