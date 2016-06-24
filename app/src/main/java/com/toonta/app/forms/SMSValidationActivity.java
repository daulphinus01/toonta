package com.toonta.app.forms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.model.Responses;
import com.toonta.app.utils.ToontaConstants;

public class SMSValidationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsvalidation);

        // Getting bundles
        final String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);
        // TODO Quand le code renvoye par SMS est valide, on renvoie la/les reponse(s)
        Responses responsesToBeSent= getIntent().getParcelableExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT);

        // Screen title
        TextView screenTitle = (TextView) findViewById(R.id.toonta_validate_sms_form_screen_title);
        assert screenTitle != null;
        screenTitle.setText(titleQuestionScreen);
    }
}
