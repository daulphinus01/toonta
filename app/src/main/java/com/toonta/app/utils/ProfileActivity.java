package com.toonta.app.utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.forms.ToontaUser;

public class ProfileActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ToontaUserInterceptor toontaUserInterceptor;

    private LinearLayout saveChangesLayout;
    private Button saveChangesButton;

    private EditText firstName;
    private EditText lastName;
    private EditText birthDate;
    private EditText emailAddress;
    private EditText phoneNumber;
    private EditText professionalActivity;
    private EditText residencePlace;
    private EditText cumulatedPoint;
    private EditText labelThree;

    // Other infos that needs to be sent when updating user's info
    private String sexe = "";
    private ToontaUser.Address address;
    private ToontaUser.Bank_ bank_;

    // Indicates that some fields have been modified
    private boolean updatesAvailable = false;

    private EditText[] allEditTexts = new EditText[9];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupActionBar();

        ImageView profileButtonUp = (ImageView) findViewById(R.id.profile_button_up);
        assert profileButtonUp != null;
        profileButtonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ProfileActivity.this);
            }
        });

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(ProfileActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(ProfileActivity.this);
            }
        });

        // Shown when user decides to modify personal info
        saveChangesLayout = (LinearLayout) findViewById(R.id.toonta_layout_save_changes);
        assert saveChangesLayout != null;
        saveChangesLayout.setVisibility(View.INVISIBLE);

        saveChangesButton = (Button) findViewById(R.id.toonta_save_changes_button);
        assert saveChangesButton != null;
        saveChangesButton.setTransformationMethod(null);

        firstName = (EditText) findViewById(R.id.first_name);
        assert firstName != null;
        firstName.setEnabled(false);
        allEditTexts[0] = firstName;

        lastName = (EditText) findViewById(R.id.last_name);
        assert lastName != null;
        lastName.setEnabled(false);
        allEditTexts[1] = lastName;

        birthDate = (EditText) findViewById(R.id.birth_date);
        assert birthDate != null;
        birthDate.setEnabled(false);
        allEditTexts[2] = birthDate;

        emailAddress = (EditText) findViewById(R.id.email_address);
        assert emailAddress != null;
        emailAddress.setEnabled(false);
        allEditTexts[3] = emailAddress;

        phoneNumber = (EditText) findViewById(R.id.phone_number);
        assert phoneNumber != null;
        phoneNumber.setEnabled(false);
        allEditTexts[4] = phoneNumber;

        professionalActivity = (EditText) findViewById(R.id.professional_activity);
        assert professionalActivity != null;
        professionalActivity.setEnabled(false);
        allEditTexts[5] = professionalActivity;

        residencePlace = (EditText) findViewById(R.id.residence_place);
        assert residencePlace != null;
        residencePlace.setEnabled(false);
        allEditTexts[6] = residencePlace;

        cumulatedPoint = (EditText) findViewById(R.id.toonta_cumilated_points);
        assert cumulatedPoint != null;
        cumulatedPoint.setEnabled(false);
        allEditTexts[7] = cumulatedPoint;

        labelThree = (EditText) findViewById(R.id.label_three);
        assert labelThree != null;
        labelThree.setEnabled(false);
        allEditTexts[8] = labelThree;


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

                sexe = toontaUser.sexe;
                address = toontaUser.address;
                bank_ = toontaUser.bank_;
            }

            @Override
            public void onToontaUserUpdate(String responseStatus) {
                Snackbar.make(findViewById(android.R.id.content), responseStatus, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });

        toontaUserInterceptor.fetchToontaUser(ToontaSharedPreferences.toontaSharedPreferences.userId);

        // Setting saveChanges button click listener
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToontaUser user = new ToontaUser(
                        birthDate.getText().toString(),
                        emailAddress.getText().toString(),
                        firstName.getText().toString(),
                        "",
                        lastName.getText().toString(),
                        "",
                        phoneNumber.getText().toString(),
                        professionalActivity.getText().toString(),
                        sexe,
                        address,
                        bank_);

                // Sending changes to server
                toontaUserInterceptor.updateToontaUser(user);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar mActionBar = getSupportActionBar();
            assert mActionBar != null;
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void setEditable(View view) {
        int saveChangesLayoutVisibility =  saveChangesLayout.getVisibility();
        if (saveChangesLayoutVisibility == View.INVISIBLE) {
            saveChangesLayout.setVisibility(View.VISIBLE);
        }

        int viewId = view.getId();

        switch (viewId) {
            case R.id._first_name :
                firstName.setEnabled(true);
                break;
            case R.id._last_name :
                lastName.setEnabled(true);
                break;
            case R.id._birth_date :
                birthDate.setEnabled(true);
                break;
            case R.id._email_address :
                emailAddress.setEnabled(true);
                break;
            case R.id._phone_number :
                phoneNumber.setEnabled(true);
                break;
            case R.id._professional_activity :
                professionalActivity.setEnabled(true);
                break;
            case R.id._residence_place :
                residencePlace.setEnabled(true);
                break;
            case R.id._toonta_cumilated_points :
                cumulatedPoint.setEnabled(true);
                break;
            case R.id._label_three :
                labelThree.setEnabled(true);
                break;
        }
    }

    private void showAlertDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

        builder.setMessage(R.string.toonta_field_modif_msg)
                .setTitle(R.string.toonta_field_modif);

        builder.setPositiveButton(R.string.toonat_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updatesAvailable = true;
            }
        });
        builder.setNegativeButton(R.string.toonta_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Not necessary
                updatesAvailable = false;
            }
        });


        AlertDialog dialog = builder.create();

        dialog.show();
    }

    //Own methods

    private String getDescriptionForError(ToontaDAO.NetworkAnswer networkAnswer) {
        if (networkAnswer == ToontaDAO.NetworkAnswer.AUTH_FAILURE) {
            return getBaseContext().getString(R.string.string_error_auth_failure);
        } else if (networkAnswer == ToontaDAO.NetworkAnswer.NO_SERVER) {
            return getBaseContext().getString(R.string.string_error_no_server);
        } else if (networkAnswer == ToontaDAO.NetworkAnswer.FAILED_UPDATING) {
            return "Failed to update modified fields";
        } else {
            return getBaseContext().getString(R.string.string_error_no_network);
        }
    }
}
