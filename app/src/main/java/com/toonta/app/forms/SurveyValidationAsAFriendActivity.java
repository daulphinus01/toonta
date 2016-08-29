package com.toonta.app.forms;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.activities.signup.SignupInteractor;
import com.toonta.app.model.Responses;
import com.toonta.app.model.SurveyResponse;
import com.toonta.app.utils.ToontaConstants;
import com.toonta.app.utils.ToontaUserInterceptor;
import com.toonta.app.utils.Utils;

import java.util.Calendar;

public class SurveyValidationAsAFriendActivity extends AppCompatActivity {

    private SignupInteractor signupInteractor;
    private ProgressDialog progressDialog;
    private ToontaUserInterceptor toontaUserInterceptor;

    // Form
    private TextView birthDateValidationAsAFriend;

    // Bithdate
    private int toontaUserBDyear;
    private int toontaUserBDmonth;
    private int toontaUserBDday;
    static final int DATE_DIALOG_ID = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_validation_as_afriend);

        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(SurveyValidationAsAFriendActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(SurveyValidationAsAFriendActivity.this);
            }
        });

        // Getting bundles
        final String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);
        final SurveyResponse responsesToBeSent= getIntent().getParcelableExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT);

        // Screen title
        TextView screenTitle = (TextView) findViewById(R.id.toonta_validate_question_form_screen_title);
        assert screenTitle != null;
        screenTitle.setText(titleQuestionScreen);

        // Various text areas
        final TextView firstNameValidationAsAFriend = (TextView) findViewById(R.id.first_name_validation_as_afriend);
        final TextView lastNameValidationAsAFriend = (TextView) findViewById(R.id.last_name_validation_as_afriend);

        birthDateValidationAsAFriend = (TextView) findViewById(R.id.birth_date_validation_as_afriend);
        birthDateValidationAsAFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        final TextView emailAddressValidationAsAFriend = (TextView) findViewById(R.id.email_address_validation_as_afriend);
        final TextView phoneNumberValidationAsAFriend = (TextView) findViewById(R.id.phone_number_validation_as_afriend);
        final Spinner professionalActivityValidationAsAFriend = (Spinner) findViewById(R.id.professional_activity_validation_as_afriend);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.toonta_profession_type_as_a_friend, R.layout.spinner_item);
        assert professionalActivityValidationAsAFriend != null;
        professionalActivityValidationAsAFriend.setAdapter(adapter);

        // Validate button
        AppCompatButton validateButton = (AppCompatButton) findViewById(R.id.toonta_validate_question_form_screen_validate_button);
        assert validateButton != null;
        validateButton.setTransformationMethod(null);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert birthDateValidationAsAFriend != null;
                assert lastNameValidationAsAFriend != null;
                assert firstNameValidationAsAFriend != null;
                assert phoneNumberValidationAsAFriend != null;
                String formValidation = isValideForm(
                        firstNameValidationAsAFriend.getText().toString(),
                        lastNameValidationAsAFriend.getText().toString(),
                        birthDateValidationAsAFriend.getText().toString(),
                        phoneNumberValidationAsAFriend.getText().toString(),
                        String.valueOf(professionalActivityValidationAsAFriend.getSelectedItem())
                );

                if (formValidation.isEmpty()) {
                    ToontaDAO.isAsFriendUserLogged = true;
                    signupInteractor.submitSignup(
                            phoneNumberValidationAsAFriend.getText().toString().trim(),
                            birthDateValidationAsAFriend.getText().toString().trim());
                } else {
                    Snackbar.make(findViewById(android.R.id.content), formValidation, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        progressDialog = new ProgressDialog(SurveyValidationAsAFriendActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.string_sign_up_activity_creating_new_account));

        signupInteractor = new SignupInteractor(getApplicationContext(), new SignupInteractor.SignupViewUpdater() {
            @Override
            public void updateView(SignupInteractor.SignupViewModel signupViewModel){
                assert phoneNumberValidationAsAFriend != null;
                if (signupViewModel.phoneNumberError.equals("")) {
                    phoneNumberValidationAsAFriend.setError(null);
                } else {
                    phoneNumberValidationAsAFriend.setError(signupViewModel.phoneNumberError);
                }
                if (signupViewModel.passwordError.equals("")) {
                    birthDateValidationAsAFriend.setError(null);
                } else {
                    birthDateValidationAsAFriend.setError(signupViewModel.passwordError);
                }
            }

            @Override
            public void onSignupSuccess() {
                // Sending other infos to server
                toontaUserInterceptor.updateToontaUser(makeToontaUser(
                    firstNameValidationAsAFriend,
                    lastNameValidationAsAFriend,
                    birthDateValidationAsAFriend,
                    emailAddressValidationAsAFriend,
                    phoneNumberValidationAsAFriend,
                    String.valueOf(professionalActivityValidationAsAFriend.getSelectedItem())));
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

        toontaUserInterceptor = new ToontaUserInterceptor(getBaseContext(), new ToontaUserInterceptor.ToontaUserViewUpdater() {
            @Override
            public void onToontaUserGet(ToontaUser toontaUser) {
                // Never used on this screen
            }

            @Override
            public void onToontaUserUpdate(String responseStatus) {
                progressDialog.dismiss();
                ToontaDAO.isAsFriendUserLogged = false;

                // Start intent of phone number validated, after sending user newly edited info
                Intent smsValidationActivityIntent = new Intent(SurveyValidationAsAFriendActivity.this, SMSValidationActivity.class);
                smsValidationActivityIntent.putExtra(ToontaConstants.QUESTION_TITLE, titleQuestionScreen);
                smsValidationActivityIntent.putExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT, responsesToBeSent);
                startActivity(smsValidationActivityIntent);

                finish();

                // Snackbar.make(findViewById(android.R.id.content), responseStatus, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                // TODO Use Theme_Material_Dialog_Alert instead of AlertDialog.THEME_HOLO_DARK
                return new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, datePickerListener,
                        year, month,day);
        }
        return null;
    }

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

    private String isValideForm(String fName, String lName, String bDate, String phoneNbr, String profession) {
        StringBuilder error = new StringBuilder();

        if (fName.isEmpty() || fName.length() <= 3)
            return error.append("Fill correctely the First name").toString();
        if (lName.isEmpty() || lName.length() <= 3)
            return error.append("Fill correctely the last name").toString();
        if (bDate.isEmpty())
            return error.append("Select your birthdate").toString();
        if (phoneNbr.isEmpty() || phoneNbr.length() <= 10)
            return error.append("Fill correctely the phone number").toString();
        if (profession.equals("Activity / Profession"))
            return error.append("Choose a profession").toString();

        return error.toString();
    }

    private ToontaUser makeToontaUser(
            TextView firstNameValidationAsAFriend,
            TextView lastNameValidationAsAFriend,
            TextView birthDateValidationAsAFriend,
            TextView emailAddressValidationAsAFriend,
            TextView phoneNumberValidationAsAFriend,
            String professionalActivityValidationAsAFriend) {
        return new ToontaUser(
                birthDateValidationAsAFriend.getText().toString().trim(),
                emailAddressValidationAsAFriend.getText().toString().trim(),
                firstNameValidationAsAFriend.getText().toString().trim(),
                "",
                lastNameValidationAsAFriend.getText().toString().trim(),
                "",
                phoneNumberValidationAsAFriend.getText().toString().trim(),
                professionalActivityValidationAsAFriend.trim(),
                "U");
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            toontaUserBDyear = selectedYear;
            toontaUserBDmonth = selectedMonth + 1;
            toontaUserBDday = selectedDay;

            // set selected date into textview
            birthDateValidationAsAFriend.setText(new StringBuilder()
                    .append(toontaUserBDyear).append("-")
                    .append(toontaUserBDmonth).append("-")
                    .append(toontaUserBDday));
        }
    };
}
