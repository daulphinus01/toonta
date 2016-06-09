package com.toonta.app.utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);

        TextView textView = (TextView) findViewById(R.id.qustion_screen_title);
        assert textView != null;
        textView.setText(titleQuestionScreen);

        ToontaQuestion toontaQuestion = new ToontaQuestion("What does't fit?",
                QuestionType.MULT_CHOICE, Arrays.asList("Dog", "Capybara", "Pizza"));

        // Creation du fragment contenant la question
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.question_fragment, ToontaQuestionFragment.newInstance(toontaQuestion))
                .commit();

    }
}
