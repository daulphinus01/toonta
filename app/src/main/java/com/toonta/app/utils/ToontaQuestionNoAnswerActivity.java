package com.toonta.app.utils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.model.Responses;
import com.toonta.app.model.SurveyResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToontaQuestionNoAnswerActivity extends AppCompatActivity {

    private Button previousButton;
    private Button nextSubmitButton;
    private LinearLayout qstRespPart;
    private ToontaDAO.QuestionsList mQuestionsList;

    // Number of pages
    private int nbrTotalPages = 0;

    private ToontaViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toonta_question_no_answer);

        // SurveyId
        String surveyId = getIntent().getStringExtra(ToontaConstants.SURVEY_ID);
        // Screen title
        String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);

        // TODO When there is non question for this survey, qstRespPart is hidden
        qstRespPart = (LinearLayout) findViewById(R.id.qst_resp_part_screen_no_answer);

        setupActionBar();

        TextView textViewTitle = (TextView) findViewById(R.id.qustion_screen_title_no_answer);
        assert textViewTitle != null;
        textViewTitle.setText(titleQuestionScreen);

        // Screen Previous Buttons
        previousButton = (Button) findViewById(R.id.button_previous_no_answer);
        nextSubmitButton = (Button) findViewById(R.id.button_submit_next_no_answer);

        assert previousButton != null;
        previousButton.setEnabled(false);
        previousButton.setTransformationMethod(null);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currPos = mViewPager.getCurrentItem();
                if (currPos >= 1) {
                    if ((currPos - 1) == 0) {
                        // On s'apprete a passer a la premiere page
                        previousButton.setEnabled(false);
                    }
                    mViewPager.setCurrentItem(currPos - 1);
                    // If we were on the last page, we have to change submit text to next
                    nextSubmitButton.setEnabled(true);
                }
            }
        });

        nextSubmitButton.setTransformationMethod(null);
        nextSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currPos = mViewPager.getCurrentItem();
                if (currPos == (nbrTotalPages - 1)) {   // Avant derniere page
                    nextSubmitButton.setEnabled(false);
                    mViewPager.setCurrentItem(currPos + 1);
                } else {
                    mViewPager.setCurrentItem(currPos + 1);
                    previousButton.setVisibility(View.VISIBLE);
                    previousButton.setText(R.string.button_previous);
                    previousButton.setEnabled(true);
                }
            }
        });

        NewSurveysInteractor oneNewSurveysInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.OneSurveyViewUpdator() {
            @Override
            public void onGetSurvey(ToontaDAO.QuestionsList questionsList) {
                // TODO Supprimer dummy data et les commentaires dans if
                ToontaDAO.QuestionsList tmpData = dummyData1();
                if (/*questionsList*/tmpData.questionResponseElements.size() <= 0) {
                    qstRespPart.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), "No questions available for this survey", Snackbar.LENGTH_LONG).show();
                } else {
                    qstRespPart.setVisibility(View.VISIBLE);
                    mQuestionsList = tmpData /*questionsList*/;

                    ToontaQuestionPageAdapter toontaQuestionPageAdapter = new ToontaQuestionPageAdapter(mQuestionsList);
                    mViewPager = (ToontaViewPager) findViewById(R.id.toonta_question_view_pager_area_no_answer);
                    assert mViewPager != null;
                    mViewPager.setAdapter(toontaQuestionPageAdapter);
                }
            }

            @Override
            public void onPostResponse(String statusCode) {
                // TODO Empty method
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });

        oneNewSurveysInteractor.fetchSurvey(surveyId);
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

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    public class ToontaQuestionPageAdapter extends PagerAdapter {

        private ToontaDAO.QuestionsList pagerQuestionsList;

        public ToontaQuestionPageAdapter(ToontaDAO.QuestionsList questionsList) {
            this.pagerQuestionsList = questionsList;
            if (pagerQuestionsList.questionResponseElements.size() >= 1) {
                nextSubmitButton.setText(R.string.next_button_next_as_text);
                // Le compteur commence a zero, d'ou le moins un
                nbrTotalPages = questionsList.questionResponseElements.size() - 1;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // container ==> ViwePager
            ToontaDAO.QuestionsList.QuestionResponse questionResponse = pagerQuestionsList.questionResponseElements.get(position);

            LinearLayout linearLayout = new LinearLayout(ToontaQuestionNoAnswerActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            linearLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);

            TextView textViewQuestionNoAnswer = new TextView(ToontaQuestionNoAnswerActivity.this);
            textViewQuestionNoAnswer.setText(questionResponse.question);
            textViewQuestionNoAnswer.setTextSize(22f);
            linearLayout.addView(textViewQuestionNoAnswer);

            container.addView(linearLayout);
            return linearLayout;
        }

        @Override
        public int getCount() {
            return pagerQuestionsList.questionResponseElements.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private ToontaDAO.QuestionsList dummyData1() {

        ToontaDAO.QuestionsList returnedList = new ToontaDAO.QuestionsList();

        ArrayList<ToontaDAO.QuestionsList.QuestionResponse> list = new ArrayList<>();

        ToontaDAO.QuestionsList.QuestionResponse questionn = new ToontaDAO.QuestionsList.QuestionResponse();
        questionn.type = "MULTIPLE_CHOICE";
        questionn.id = "1100";

        ToontaDAO.QuestionsList.ResponseChoiceResponse resp1 = new ToontaDAO.QuestionsList.ResponseChoiceResponse();
        ToontaDAO.QuestionsList.ResponseChoiceResponse resp2 = new ToontaDAO.QuestionsList.ResponseChoiceResponse();
        ToontaDAO.QuestionsList.ResponseChoiceResponse resp3 = new ToontaDAO.QuestionsList.ResponseChoiceResponse();
        ToontaDAO.QuestionsList.ResponseChoiceResponse resp4 = new ToontaDAO.QuestionsList.ResponseChoiceResponse();
        resp1.id = "1A";
        resp1.value = "Response dos";
        resp2.id = "2A";
        resp2.value = "Response tres";
        resp3.id = "3A";
        resp3.value = "Response ynui";
        resp4.id = "4A";
        resp4.value = "Response quatro";
        questionn.choices = new ArrayList<>();
        questionn.choices.add(resp1);
        questionn.choices.add(resp2);
        questionn.choices.add(resp3);
        questionn.choices.add(resp4);

        questionn.question = "Que pasa amigo ?";

        ToontaDAO.QuestionsList.QuestionResponse questionn1 = new ToontaDAO.QuestionsList.QuestionResponse();
        questionn1.type = "YES_NO";
        questionn1.id = "1110";
        questionn1.question = "Fait-il chaud aujourd'hui ?";

        ToontaDAO.QuestionsList.QuestionResponse questionn2 = new ToontaDAO.QuestionsList.QuestionResponse();
        questionn2.type = "BASIC";
        questionn2.id = "1111";
        questionn2.question = "Comment appelle-t-on la capitale de la France ?";

        ToontaDAO.QuestionsList.QuestionResponse questionn3 = new ToontaDAO.QuestionsList.QuestionResponse();
        questionn3.type = "BASIC";
        questionn3.id = "1211";
        questionn3.question = "Comment vas-tu cher ami ?";

        ToontaDAO.QuestionsList.QuestionResponse questionn4 = new ToontaDAO.QuestionsList.QuestionResponse();
        questionn4.type = "BASIC";
        questionn4.id = "12111";
        questionn4.question = "Comment t-appelles tu ?";


        list.add(questionn);
        list.add(questionn1);
        list.add(questionn2);
        list.add(questionn3);
        list.add(questionn4);

        returnedList.questionResponseElements.addAll(list);

        return returnedList;
    }

}