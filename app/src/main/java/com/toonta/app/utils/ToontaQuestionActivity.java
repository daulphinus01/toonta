package com.toonta.app.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.ToontaQuestionFragment;
import com.toonta.app.model.QuestionType;
import com.toonta.app.model.ToontaQuestion;

import java.util.Arrays;

public class ToontaQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toonta_question);

        setupActionBar();

        String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);
        // TODO Connect to server and fetch questions (this titleQuestionScreen may be used as parameter
        // to question that has to be fetched from server). It is got from BANK screen.

        ToontaQuestion toontaQuestion = new ToontaQuestion("What doesn't fit?",
                QuestionType.OPEN_QUESTION, Arrays.asList("Dog", "Capybara", "Pizza"));

        Button buttonSubmit = (Button) findViewById(R.id.button_submit);
        assert buttonSubmit != null;
        buttonSubmit.setTransformationMethod(null);

        TextView textViewTitle = (TextView) findViewById(R.id.qustion_screen_title);
        assert textViewTitle != null;
        textViewTitle.setText(titleQuestionScreen);

        TextView textViewQuestion = (TextView) findViewById(R.id.qustion_screen_question);
        assert textViewQuestion != null;
        textViewQuestion.setText(toontaQuestion.getQuestion());



        // Creation du fragment contenant la question
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.question_fragment, ToontaQuestionFragment.newInstance(toontaQuestion))
                .commit();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }
}