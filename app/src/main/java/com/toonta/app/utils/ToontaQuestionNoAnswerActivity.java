package com.toonta.app.utils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
    private TextView[] progressBarDots;
    private LinearLayout questionsProgressBar;
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
        ImageView upButton = (ImageView) findViewById(R.id.toonta_question_no_answer_up_button);
        assert upButton != null;
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ToontaQuestionNoAnswerActivity.this);
            }
        });

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(ToontaQuestionNoAnswerActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(ToontaQuestionNoAnswerActivity.this);
            }
        });

        TextView textViewTitle = (TextView) findViewById(R.id.qustion_screen_title_no_answer);
        assert textViewTitle != null;
        textViewTitle.setText(titleQuestionScreen);

        questionsProgressBar = (LinearLayout) findViewById(R.id.toonta_question_no_answer_progress_bar);

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
                    progressBarDots[currPos].setTextColor(Color.BLACK);
                    progressBarDots[currPos-1].setTextColor(Color.WHITE);
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
                }
                mViewPager.setCurrentItem(currPos + 1);
                previousButton.setVisibility(View.VISIBLE);
                previousButton.setText(R.string.button_previous);
                previousButton.setEnabled(true);
                progressBarDots[currPos].setTextColor(Color.BLACK);
                progressBarDots[currPos+1].setTextColor(Color.WHITE);
            }
        });

        NewSurveysInteractor oneNewSurveysInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.OneSurveyViewUpdator() {
            @Override
            public void onGetSurvey(ToontaDAO.QuestionsList questionsList) {
                if (questionsList.questionResponseElements.size() <= 0) {
                    qstRespPart.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), "No questions available for this survey", Snackbar.LENGTH_LONG).show();
                } else {
                    qstRespPart.setVisibility(View.VISIBLE);
                    mQuestionsList = questionsList;
                    progressBarDots = Utils.initProgressBar(
                            questionsList.questionResponseElements.size(),
                            questionsProgressBar,
                            ToontaQuestionNoAnswerActivity.this);

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

}