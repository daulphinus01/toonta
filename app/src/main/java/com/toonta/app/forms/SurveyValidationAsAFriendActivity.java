package com.toonta.app.forms;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.model.Responses;
import com.toonta.app.utils.ToontaConstants;
import com.toonta.app.utils.Utils;

public class SurveyValidationAsAFriendActivity extends AppCompatActivity {

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
        final Responses responsesToBeSent= getIntent().getParcelableExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT);

        // Screen title
        TextView screenTitle = (TextView) findViewById(R.id.toonta_validate_question_form_screen_title);
        assert screenTitle != null;
        screenTitle.setText(titleQuestionScreen);

        // Various text areas
        final TextView firstNameValidationAsAFriend = (TextView) findViewById(R.id.first_name_validation_as_afriend);
        final TextView lastNameValidationAsAFriend = (TextView) findViewById(R.id.last_name_validation_as_afriend);
        final TextView birthDateValidationAsAFriend = (TextView) findViewById(R.id.birth_date_validation_as_afriend);
        TextView emailAddressValidationAsAFriend = (TextView) findViewById(R.id.email_address_validation_as_afriend);
        final TextView phoneNumberValidationAsAFriend = (TextView) findViewById(R.id.phone_number_validation_as_afriend);
        TextView professionalActivityValidationAsAFriend = (TextView) findViewById(R.id.professional_activity_validation_as_afriend);

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
                        phoneNumberValidationAsAFriend.getText().toString()
                );

                if (formValidation.isEmpty()) {
                    // Start intent of phone number validated, after sending user newly edited info
                    Intent smsValidationActivityIntent = new Intent(SurveyValidationAsAFriendActivity.this, SMSValidationActivity.class);
                    smsValidationActivityIntent.putExtra(ToontaConstants.QUESTION_TITLE, titleQuestionScreen);
                    smsValidationActivityIntent.putExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT, responsesToBeSent);
                    startActivity(smsValidationActivityIntent);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), formValidation, Snackbar.LENGTH_LONG).show();
                }
            }
        });
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

    private String isValideForm(String fName, String lName, String bDate, String phoneNbr) {
        StringBuilder error = new StringBuilder();

        if (fName.isEmpty() || fName.length() <= 3)
            return error.append("Fill correctely the First name").toString();
        if (lName.isEmpty() || lName.length() <= 3)
            return error.append("Fill correctely the last name").toString();
        if (bDate.isEmpty() || bDate.length() <= 8)
            return error.append("Fill correctely the birthday date").toString();
        if (phoneNbr.isEmpty() || phoneNbr.length() <= 10)
            return error.append("Fill correctely the phone number").toString();

        return error.toString();
    }
}
