package com.toonta.app.utils;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.activities.new_surveys.NewSurveysInteractor;

import java.util.Collections;

public class ToontaQuestionNoAnswerActivity extends AppCompatActivity {

    private Button previousButton;
    private Button nextSubmitButton;
    private LinearLayout qstRespPart;
    private TextView[] progressBarDots;
    private LinearLayout questionsProgressBar;
    private ToontaDAO.QuestionsList questionsList;

    // Number of pages
    private int nbrTotalPages = 0;

    // Indicates the current question being displayed
    private int currentQuestionPos = 0;
    private LinearLayout questionArea;
    //private LinearLayout answersArea;
    private TextView textViewQuestionPart;
    private LinearLayout[] questionLinearLayouts;

    // Used to store questions ids that will be used to fetch answers
    private String[] questionsIds;
    // Recupere les reponses
    // private QuestionReportInteractor questionReportInteractor;

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

        // Linear ou sont affichees les questions
        questionArea = (LinearLayout) findViewById(R.id.toonta_question_view_pager_area_no_answer);

        // Linear ou sont affichees les reponses
        //answersArea = (LinearLayout) findViewById(R.id.toonta_question_view_pager_area_answer);

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
                if (currentQuestionPos >= 1) {
                    currentQuestionPos--;
                    if (currentQuestionPos == 0) {
                        // On s'apprete a passer a la premiere page
                        previousButton.setVisibility(View.INVISIBLE);
                    }

                    // On met a jour la barre de progression
                    progressBarDots[currentQuestionPos + 1].setTextColor(Color.BLACK);
                    progressBarDots[currentQuestionPos].setTextColor(Color.WHITE);

                    questionArea.removeAllViews();
                    questionArea.addView(questionLinearLayouts[currentQuestionPos]);

                    // questionReportInteractor.getQuestionReportByQuestionId(questionsIds[currentQuestionPos]);

                    // If we were on the last page, we have to change submit text to next
                    nextSubmitButton.setVisibility(View.VISIBLE);
                    nextSubmitButton.setEnabled(true);
                }
            }
        });

        nextSubmitButton.setTransformationMethod(null);
        nextSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionPos++;
                if (currentQuestionPos == nbrTotalPages) {   // Avant derniere page
                    nextSubmitButton.setVisibility(View.INVISIBLE);
                }

                questionArea.removeAllViews();
                questionArea.addView(questionLinearLayouts[currentQuestionPos]);

                // questionReportInteractor.getQuestionReportByQuestionId(questionsIds[currentQuestionPos]);

                // On met a jour la barre de progression
                progressBarDots[currentQuestionPos - 1].setTextColor(Color.BLACK);
                progressBarDots[currentQuestionPos].setTextColor(Color.WHITE);

                // On desactive le bouton previous
                previousButton.setVisibility(View.VISIBLE);
                previousButton.setEnabled(true);
            }
        });

        /*******************************************************************
         *                  RECUPERATION DES QUESTIONS
         *******************************************************************/
        NewSurveysInteractor oneNewSurveysInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.OneSurveyViewUpdator() {
            @Override
            public void onGetSurvey(ToontaDAO.QuestionsList qstList) {
                questionsList = qstList;
                if (questionsList.questionResponseElements.size() <= 0) {
                    qstRespPart.setVisibility(View.INVISIBLE);
                    Snackbar.make(findViewById(android.R.id.content), "No questions available for this survey", Snackbar.LENGTH_LONG).show();
                } else {
                    // Par defaut, le buouton next n'a pas de text. Il est settee pour la premiere fois
                    nextSubmitButton.setText(R.string.next_button_next_as_text);
                    previousButton.setText(R.string.button_previous);

                    qstRespPart.setVisibility(View.VISIBLE);

                    // Barre de progression mise a jour
                    progressBarDots = Utils.initProgressBar(questionsList.questionResponseElements.size(), questionsProgressBar, ToontaQuestionNoAnswerActivity.this);

                    // Barre de progrssion initialisee
                    progressBarDots[currentQuestionPos].setTextColor(Color.WHITE);

                    // Tous les linearlayout pour toutes les reponses
                    Collections.sort(questionsList.questionResponseElements);
                    questionsIds = new String[questionsList.questionResponseElements.size()];
                    for (int i = 0; i < questionsList.questionResponseElements.size(); i++)
                        questionsIds[i] = questionsList.questionResponseElements.get(i).id;
                    questionLinearLayouts = Utils.instantiateItemNoAnswerScreen(questionsList.questionResponseElements, ToontaQuestionNoAnswerActivity.this);

                    // La partie de reponse
                    questionArea.addView(questionLinearLayouts[currentQuestionPos]);

                    // Recuperation de la reponse
                    // questionReportInteractor.getQuestionReportByQuestionId(questionsIds[currentQuestionPos]);

                    // Le compteur commence a zero, d'ou le moins un
                    nbrTotalPages = questionsList.questionResponseElements.size() - 1;
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

        /*******************************************************************
         *           RECUPERATION DES RESPONSES AUX QUESTIONS
         *******************************************************************/
        /*
        questionReportInteractor = new QuestionReportInteractor(getApplicationContext(), new QuestionReportInteractor.QuestionReportGetter() {
            @Override
            public void onQuestionReportSuccess(List<String> reponseDUneQuestion) {
                answersArea.removeAllViews();
                for (int z = 0; z < reponseDUneQuestion.size(); z++) {
                    TextView respViewArea = new TextView(ToontaQuestionNoAnswerActivity.this);
                    String respStr = "";
                    if (reponseDUneQuestion.get(z).equals("No anwers")) {
                        respStr = reponseDUneQuestion.get(z);
                    } else {
                        respStr = z + 1 + ". " + reponseDUneQuestion.get(z);
                    }
                    respViewArea.setText(respStr);
                    respViewArea.setTextSize(22f);
                    answersArea.addView(respViewArea);
                }
            }

            @Override
            public void onFailure(String error) {
                answersArea.removeAllViews();
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });*/
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar mActionBar = getSupportActionBar();
            assert mActionBar != null;
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);

            View mCustomView = getLayoutInflater().inflate(R.layout.custom_actionbar_with_up_button, null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            mActionBar.setCustomView(mCustomView, layoutParams);
            Toolbar parent = (Toolbar) mCustomView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    public void goUp(View view) {
        NavUtils.navigateUpFromSameTask(ToontaQuestionNoAnswerActivity.this);
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

            final LinearLayout linearLayout = new LinearLayout(ToontaQuestionNoAnswerActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            linearLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
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