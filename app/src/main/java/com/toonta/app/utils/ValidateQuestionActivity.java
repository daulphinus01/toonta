package com.toonta.app.utils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
                startActivity(new Intent(ValidateQuestionActivity.this, HomePageActivity.class));
                finish();
            }
        });
    }
}
