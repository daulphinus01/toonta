package com.toonta.app.forms;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.model.SurveyResponse;
import com.toonta.app.utils.SettingsClickListener;
import com.toonta.app.utils.ToontaConstants;

public class SMSValidationActivity extends AppCompatActivity {

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsvalidation);

        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new SettingsClickListener(SMSValidationActivity.this));

        // Getting bundles
        final String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);
        // TODO Quand le code renvoye par SMS est valide, on renvoie la/les reponse(s)
        final SurveyResponse responsesToBeSent= getIntent().getParcelableExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT);

        // Screen title
        TextView screenTitle = (TextView) findViewById(R.id.toonta_validate_sms_form_screen_title);
        assert screenTitle != null;
        screenTitle.setText(titleQuestionScreen);

        // Affiche le msg selon si la reponse a bien ete envoye au serveur ou pas
        builder = new AlertDialog.Builder(SMSValidationActivity.this);
        builder.setTitle(getString(R.string.toonta_survey_validation_dialog_title));

        Button confirm = (Button) findViewById(R.id.toonta_validate_question_form_screen_validate_button);
        assert confirm != null;
        confirm.setTransformationMethod(null);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewSurveysInteractorAndSendResponses(responsesToBeSent);
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

    private void createNewSurveysInteractorAndSendResponses(SurveyResponse surveyResponse) {
        NewSurveysInteractor interactor = new NewSurveysInteractor(SMSValidationActivity.this, new NewSurveysInteractor.OneSurveyViewUpdator() {
            @Override
            public void onGetSurvey(ToontaDAO.QuestionsList QuestionsList) {
                // Rien a faire ici
            }

            @Override
            public void onPostResponse(String statusCode) {
                // Msg retourne lors de l'envoie des reponses
                builder.setMessage(getString(R.string.toonta_survey_validation_dialog_msg_account_created));
                builder.setPositiveButton(R.string.toonat_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(SMSValidationActivity.this, HomeConnectedActivity.class));
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

        interactor.postSurveyResponseAsAFriend(surveyResponse);
    }
}
