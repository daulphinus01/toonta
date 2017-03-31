package com.toonta.app.utils;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toonta.app.BuildConfig;
import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.forms.SurveyValidationAsAFriendActivity;
import com.toonta.app.model.SurveyResponse;

public class ValidateQuestionActivity extends AppCompatActivity {

    private NewSurveysInteractor surveysInteractor;
    private boolean questionAlreadyAnwered = false;
    private SurveyResponse responsesToBeSent;
    private AlertDialog.Builder builder;
    private String authorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_question);

        // Author id
        authorId = getIntent().getStringExtra(ToontaConstants.SURVEY_AUTHOR_ID);

        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new SettingsClickListener(ValidateQuestionActivity.this));

        // Getting bundles
        responsesToBeSent = getIntent().getParcelableExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT);
        if (BuildConfig.DEBUG)
        Log.v("=============>", responsesToBeSent.toString());
        final String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);

        TextView screenTitle = (TextView) findViewById(R.id.toonta_validate_question_screen_title);
        assert screenTitle != null;
        screenTitle.setText(titleQuestionScreen);

        // Affiche le msg selon si la reponse a bien ete envoye au serveur ou pas
        builder = new AlertDialog.Builder(ValidateQuestionActivity.this);
        builder.setTitle(getString(R.string.toonta_survey_validation_dialog_title));

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

        final LinearLayout validateAsYourSelf = (LinearLayout) findViewById(R.id.toonta_validate_as_yourself);
        assert validateAsYourSelf != null;
        validateAsYourSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Envoie des reponses au serveur et retour a l'ecran d'accueil if success
                if (questionAlreadyAnwered) {
                    Snackbar.make(findViewById(android.R.id.content), "Survey already validated 'as yourself'", Snackbar.LENGTH_LONG).show();
                } else {
                    createNewSurveysInteractorAndSendResponses(responsesToBeSent);
                }
            }
        });

        NewSurveysInteractor newSurveysInteractor = new NewSurveysInteractor(ValidateQuestionActivity.this, new NewSurveysInteractor.SurviesIDsUpdater() {
            @Override
            public void onSuccess(boolean existAnsweredId) {
                if (existAnsweredId) {
                    questionAlreadyAnwered = true;
                } else {
                    questionAlreadyAnwered = false;
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
        newSurveysInteractor.existAnsweredId(authorId, responsesToBeSent.surveyId);
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

    private NewSurveysInteractor createNewSurveysInteractorAndSendResponses(SurveyResponse surveyResponse) {

        NewSurveysInteractor interactor = new NewSurveysInteractor(ValidateQuestionActivity.this, new NewSurveysInteractor.OneSurveyViewUpdator() {
            @Override
            public void onGetSurvey(ToontaDAO.QuestionsList QuestionsList) {
                // Rien a faire ici
            }

            @Override
            public void onPostResponse(String statusCode) {
                // Msg retourne lors de l'envoie des reponses
                builder.setMessage(getString(R.string.toonta_survey_validation_dialog_msg));
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

            @Override
            public void onFailure(String error) {
                builder.setMessage(getString(R.string.toonta_survey_validation_dialog_msg_error));
                builder.setPositiveButton(R.string.toonat_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // startActivity(new Intent(ValidateQuestionActivity.this, HomeConnectedActivity.class));
                        return;
                    }
                });
                builder.setCancelable(false);
                builder.create().show();

            }
        });

        interactor.postSurveyResponse(surveyResponse);

        return interactor;
    }
}
