package com.toonta.app.utils;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.HomePageActivity;
import com.toonta.app.R;
import com.toonta.app.forms.SurveyValidationAsAFriendActivity;
import com.toonta.app.model.Responses;

public class ValidateQuestionActivity extends AppCompatActivity {

    private Responses responsesToBeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_question);

        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(ValidateQuestionActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(ValidateQuestionActivity.this);
            }
        });

        // Getting bundles
        responsesToBeSent = getIntent().getParcelableExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT);
        final String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);

        TextView screenTitle = (TextView) findViewById(R.id.toonta_validate_question_screen_title);
        assert screenTitle != null;
        screenTitle.setText(titleQuestionScreen);

        LinearLayout validateAsFriend = (LinearLayout) findViewById(R.id.validate_as_a_friend);
        assert validateAsFriend != null;
        validateAsFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO On doit passer
                // On demarre l'intent de formulaire de validation pour autrui
                Intent surveyValidationAsAFriendActivityIntent = new Intent(ValidateQuestionActivity.this, SurveyValidationAsAFriendActivity.class);
                surveyValidationAsAFriendActivityIntent.putExtra(ToontaConstants.QUESTION_TITLE, titleQuestionScreen);
                surveyValidationAsAFriendActivityIntent.putExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT, responsesToBeSent);

                startActivity(surveyValidationAsAFriendActivityIntent);
            }
        });

        LinearLayout validateAsYourSelf = (LinearLayout) findViewById(R.id.toonta_validate_as_yourself);
        assert validateAsYourSelf != null;
        validateAsYourSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Envoie de la reponse et retour a l'ecran d'accueil
                // TODO Envoyer la reponse au serveur
                AlertDialog.Builder builder = new AlertDialog.Builder(ValidateQuestionActivity.this);
                builder.setMessage(getString(R.string.toonta_survey_validation_dialog_msg))
                        .setTitle(getString(R.string.toonta_survey_validation_dialog_title));
                builder.setPositiveButton(R.string.toonat_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ValidateQuestionActivity.this, HomeConnectedActivity.class));
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
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
}
