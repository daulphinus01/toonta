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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.Map;

import static com.toonta.app.utils.ToontaConstants.YES_RADIO_BUTTON_ID;

public class ToontaQuestionActivity extends AppCompatActivity {

    private String surveyId;
    private Button previousButton;
    private Button nextSubmitButton;
    private LinearLayout qstRespPart;
    private TextView textViewQuestionPart;
    private LinearLayout questionProgressBar;
    private ToontaDAO.QuestionsList questionsList;
    private static String TAG = "ToontaQuestionActivity ";
    private Responses responsesToBeSent = new Responses();

    // Indicates the current question being displayed
    private int currentQuestionPos = 0;

    // Message shown when Next / Submit is hit without any answer.
    private String errorMsg = "";

    // Number of pages
    private int nbrTotalPages = 0;

    // It is used to map questions ids to index used on views
    private Map<Integer, String> intToStringIndex = new HashMap<>();

    private TextView[] progressBarDots;

    private ViewPager mViewPager;

    private LinearLayout[] questionLinearLayouts;
    private LinearLayout questionArea;

    public ToontaQuestionActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toonta_question);

        // SurveyId
        surveyId = getIntent().getStringExtra(ToontaConstants.SURVEY_ID);

        responsesToBeSent.respondentId = ToontaSharedPreferences.toontaSharedPreferences.userId;
        responsesToBeSent.surveyId = surveyId;

        setupActionBar();
        ImageView upButton = (ImageView) findViewById(R.id.toonta_question_up_button);
        assert upButton != null;
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ToontaQuestionActivity.this);
            }
        });

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(ToontaQuestionActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(ToontaQuestionActivity.this);
            }
        });

        questionsList = dummyData1();
        // Le compteur commence a zero, d'ou le moins un
        nbrTotalPages = questionsList.questionResponseElements.size() - 1;

        questionProgressBar = (LinearLayout) findViewById(R.id.toonta_question_answering_progress_bar);
        progressBarDots = Utils.initProgressBar(dummyData1().questionResponseElements.size(), questionProgressBar, ToontaQuestionActivity.this);

        // Screen title
        final String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);
        TextView textViewTitle = (TextView) findViewById(R.id.qustion_screen_title);
        assert textViewTitle != null;
        textViewTitle.setText(titleQuestionScreen);

        // Screen Previous Buttons
        previousButton = (Button) findViewById(R.id.button_previous);
        nextSubmitButton = (Button) findViewById(R.id.button_submit_next);

        assert previousButton != null;
        previousButton.setEnabled(false);
        previousButton.setTransformationMethod(null);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verification que les questions ont bien ete repondues
                String msgRetour = validateQuestionAndPrepareToSend(questionLinearLayouts[currentQuestionPos]);
                if (msgRetour.trim().isEmpty()) {
                    // TODO Supprimer ce log
                    Log.v("=======================", responsesToBeSent.toString());

                    if (currentQuestionPos > 0) {
                        currentQuestionPos--;
                        if (currentQuestionPos == 0) {
                            previousButton.setEnabled(false);
                        }
                        textViewQuestionPart.setText(questionsList.questionResponseElements.get(currentQuestionPos).question);

                        // On met a jour la barre de progression
                        progressBarDots[currentQuestionPos + 1].setTextColor(Color.BLACK);
                        progressBarDots[currentQuestionPos].setTextColor(Color.WHITE);

                        // La partie de reponse
                        questionArea.removeAllViews();
                        questionArea.addView(questionLinearLayouts[currentQuestionPos]);

                        // If we were on the last page, we have to change submit text to next
                        nextSubmitButton.setText(R.string.next_button_next_as_text);
                    }
                } else {
                    // Validation de question echouee
                    Snackbar.make(findViewById(android.R.id.content), msgRetour, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        nextSubmitButton.setTransformationMethod(null);
        nextSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verification que les questions ont bien ete repondues
                String msgRetour = validateQuestionAndPrepareToSend(questionLinearLayouts[currentQuestionPos]);
                if (msgRetour.trim().isEmpty()) {
                    // TODO Supprimer ce log
                    Log.v("=======================", "=======================");
                    Log.v("=======================", responsesToBeSent.toString());
                    Log.v("***********************", "***********************");

                    if (currentQuestionPos == nbrTotalPages) {
                        // TODO Renvoyer les reponses au serveur
                        // On envoie les reponses au serveur, on passe a une autre activite
                        Intent validateQuestionActivityIntent = new Intent(ToontaQuestionActivity.this, ValidateQuestionActivity.class);
                        validateQuestionActivityIntent.putExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT, responsesToBeSent);
                        validateQuestionActivityIntent.putExtra(ToontaConstants.QUESTION_TITLE, titleQuestionScreen);
                        startActivity(validateQuestionActivityIntent);
                    } else {
                        System.out.println(currentQuestionPos);
                        currentQuestionPos++;
                        System.out.println(currentQuestionPos);
                        textViewQuestionPart.setText(questionsList.questionResponseElements.get(currentQuestionPos).question);

                        // Si on est a la derniere page, le bouton next devient submit
                        if (currentQuestionPos == nbrTotalPages) {
                            nextSubmitButton.setText(R.string.submit);
                        }

                        questionArea.removeAllViews();
                        questionArea.addView(questionLinearLayouts[currentQuestionPos]);

                        // On met a jour la barre de progression
                        progressBarDots[currentQuestionPos - 1].setTextColor(Color.BLACK);
                        progressBarDots[currentQuestionPos].setTextColor(Color.WHITE);

                        // On desactive le bouton previous
                        previousButton.setEnabled(true);
                    }
                } else {
                    // Validation de question echouee
                    Snackbar.make(findViewById(android.R.id.content), msgRetour, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        questionArea = (LinearLayout) findViewById(R.id.toonta_question_view_pager_area);

        // TODO When there is non question for this survey, qstRespPart is hidden
        qstRespPart = (LinearLayout) findViewById(R.id.qst_resp_part_screen);
        if (questionsList.questionResponseElements.size() < 1) {
            qstRespPart.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(android.R.id.content), R.string.toonta_no_qst_for_survey, Snackbar.LENGTH_LONG).show();
        } else {
            // Par defaut, le buouton next n'a pas de text. Il est settee pour la premiere fois
            nextSubmitButton.setText(R.string.next_button_next_as_text);

            // Barre de progrssion initialisee
            progressBarDots[currentQuestionPos].setTextColor(Color.WHITE);

            // La question posee
            textViewQuestionPart = (TextView) findViewById(R.id.qustion_screen_question);
            textViewQuestionPart.setText(questionsList.questionResponseElements.get(currentQuestionPos).question);

            // Tous les linearlayout pour toutes les reponses
            questionLinearLayouts = Utils.instantiateItem(questionsList.questionResponseElements, ToontaQuestionActivity.this);

            // La partie de reponse
            questionArea.addView(questionLinearLayouts[currentQuestionPos]);
        }



        /*ToontaQuestionPageAdapter toontaQuestionPageAdapter = new ToontaQuestionPageAdapter(questionsList);
        assert mViewPager != null;
        mViewPager.setAdapter(toontaQuestionPageAdapter);

        // Fetching survies from server
        newSurveyInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.OneSurveyViewUpdator() {
            @Override
            public void onGetSurvey(ToontaDAO.QuestionsList qstList) {
                Log.v(TAG, qstList.toString());


            }

            @Override
            public void onPostResponse(String statusCode) {
                // TODO Deal with 200 status code!
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });*/

        // Fecthing survey
        //newSurveyInteractor.fetchSurvey(surveyId);

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

    private void populateSurveyResponse(ToontaDAO.QuestionsList qstList, SurveyResponse surveyResp) {
        surveyResp.respondentId = ToontaSharedPreferences.toontaSharedPreferences.userId;
        surveyResp.surveyId = surveyId;
        for (int i = 0; i < qstList.questionResponseElements.size(); i++) {
            // ToontaDAO.QuestionsList.QuestionResponse qstResp : qstList.questionResponseElements
            SurveyResponse.AtomicResponseRequest atomicResponseRequest = new SurveyResponse.AtomicResponseRequest();
            atomicResponseRequest.questionId = qstList.questionResponseElements.get(i).id;
            surveyResp.responses.add(atomicResponseRequest);
        }
    }

    /*public class ToontaQuestionPageAdapter extends PagerAdapter {

        private ToontaDAO.QuestionsList mQuestionsList;
        Map<Integer, LinearLayout> adapterViews;

        public ToontaQuestionPageAdapter(ToontaDAO.QuestionsList questionsList) {
            this.mQuestionsList = questionsList;
            this.adapterViews = new HashMap<>();
            if (questionsList.questionResponseElements.size() >= 1) {
                nextSubmitButton.setText(R.string.next_button_next_as_text);
                // Premiere question
                textViewQuestionPart.setText(questionsList.questionResponseElements.get(0).question);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // container ==> ViwePager
            ToontaDAO.QuestionsList.QuestionResponse questionResponse = mQuestionsList.questionResponseElements.get(position);

            // Next button becomes submit if we rich the last question
            if (position == mQuestionsList.questionResponseElements.size()) {
                nextSubmitButton.setText(R.string.submit);
            }

            if (adapterViews.get(position) != null) {
                container.addView(adapterViews.get(position));
                return adapterViews.get(position);
            } else {
                LinearLayout linearLayout = Utils.instantiateItem(questionResponse, ToontaQuestionActivity.this);
                this.adapterViews.put(position, linearLayout);
                container.addView(linearLayout);
                return linearLayout;
            }
        }

        @Override
        public int getCount() {
            return mQuestionsList.questionResponseElements.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }*/

    private String validateQuestionAndPrepareToSend(LinearLayout currentPage) {
        boolean qstAnswered = false;

        ToontaDAO.QuestionsList.QuestionResponse qstResp = questionsList.questionResponseElements.get(currentQuestionPos);
        String questionId = qstResp.id;
        switch (qstResp.type) {
            case "YES_NO" :
                RadioGroup radioGroup = (RadioGroup) currentPage.findViewWithTag(ToontaConstants.TOONTA_YES_NO_TAG + qstResp.id);
                int selectedButtonId = radioGroup.getCheckedRadioButtonId();
                if (selectedButtonId == -1) {
                    return getString(R.string.select_one_button);
                } else {
                    // Un radiobutton a ete coche
                    String yesNoAnswer = "";
                    switch (selectedButtonId) {
                        // 1111111 corresponds to YES radiobutton id
                        case 1111111 :
                            yesNoAnswer = "YES";
                            break;
                        // 2222222 corresponds to NO radiobutton id
                        case 2222222 :
                            yesNoAnswer = "NO";
                            break;
                        default:
                            return getString(R.string.smth_wrong_re_answer_the_qst);
                    }

                    // Verifier si la question n'existe PAS dans l'enregistrement des reponses
                    // Probablement parce qu'on a clique sur PREVIOUS
                    for (Responses.Response resp : responsesToBeSent.responses) {
                        if (resp.questionId.equals(questionId)) {
                            resp.yesNo = yesNoAnswer;
                            qstAnswered = true;
                            break;
                        }
                    }
                    // La question vient d'etre abordee pour la premiere fois
                    if (!qstAnswered) {
                        // La question n'a pas encore ete repondue
                        Responses.Response resp = responsesToBeSent.createResponse();
                        resp.yesNo = yesNoAnswer;
                        resp.questionId = questionId;
                        responsesToBeSent.responses.add(resp);
                    }

                    // On retourne une chaine vide pour signaler que tout s'est bien passe
                    return "";
                }
            case "BASIC" :
                EditText respEditText = (EditText) currentPage.getChildAt(0);
                if (respEditText == null || respEditText.getText().toString().trim().length() <= 2) {
                    return getString(R.string.answer_qst_before_moving_to_next);
                } else {
                    // Verifier si la question n'existe PAS dans l'enregistrement des reponses
                    // Probablement parce qu'on a clique sur PREVIOUS
                    for (Responses.Response resp : responsesToBeSent.responses) {
                        if (resp.questionId.equals(questionId)) {
                            resp.textAnswer = respEditText.getText().toString().trim();
                            qstAnswered = true;
                            break;
                        }
                    }

                    // La question vient d'etre abordee pour la premiere fois
                    if (!qstAnswered) {
                        // La question n'a pas encore ete repondue
                        Responses.Response resp = responsesToBeSent.createResponse();
                        resp.textAnswer = respEditText.getText().toString().trim();
                        resp.questionId = questionId;
                        responsesToBeSent.responses.add(resp);
                    }

                    // On retourne une chaine vide pour signaler que tout s'est bien passe
                    return "";
                }
            case "MULTIPLE_CHOICE" :
                int nbrCheckBoxes = currentPage.getChildCount();
                // Si ce boolean est toujours faulse a la sortie de la boucle, aucune une case
                // n'aura ete cochee. Et donc renvoyer une chaine contenant le message d'erreur.
                boolean atleastOneChoiceIsSelected = false;
                for (int i = 0; i < nbrCheckBoxes; i++) {
                    CheckBox checkBox = (CheckBox) currentPage.findViewWithTag(ToontaConstants.TOONTA_MULTIPLE_CHOICE_TAG + qstResp.choices.get(i).id);
                    if (checkBox.isChecked()) {
                        atleastOneChoiceIsSelected = true;
                        String choiceId = intToStringIndex.get(checkBox.getId());
                        // Verifier si la question n'existe PAS dans l'enregistrement des reponses
                        // Probablement parce qu'on a clique sur PREVIOUS
                        for (Responses.Response resp : responsesToBeSent.responses) {
                            if (resp.questionId.equals(questionId)) {
                                if ((resp.choiceId != null) && resp.choiceId.equals(choiceId)) {
                                    resp.textAnswer = checkBox.getText().toString();
                                    qstAnswered = true;
                                    break;
                                }
                            }
                        }

                        // La question vient d'etre abordee pour la premiere fois
                        if (!qstAnswered) {
                            // La question n'a pas encore ete repondue
                            Responses.Response resp = responsesToBeSent.createResponse();
                            resp.textAnswer = checkBox.getText().toString();
                            resp.questionId = questionId;
                            resp.choiceId = choiceId;
                            responsesToBeSent.responses.add(resp);
                        }
                    }
                }

                if (!atleastOneChoiceIsSelected) {
                    // Aucune case n'a ete cochee
                    return getString(R.string.select_one_button);
                }

                return "";

            default:
                // Type de question inconu
                return "";
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

        ToontaDAO.QuestionsList.QuestionResponse questionn3 = new ToontaDAO.QuestionsList.QuestionResponse();
        questionn3.type = "BASIC";
        questionn3.id = "1111";
        questionn3.question = "Comment appelle-t-on la capitale de la France ?";

        ToontaDAO.QuestionsList.QuestionResponse questionn4 = new ToontaDAO.QuestionsList.QuestionResponse();
        questionn4.type = "BASIC";
        questionn4.id = "1211";
        questionn4.question = "Comment vas-tu cher ami ?";

        ToontaDAO.QuestionsList.QuestionResponse questionn5 = new ToontaDAO.QuestionsList.QuestionResponse();
        questionn5.type = "BASIC";
        questionn5.id = "1011";
        questionn5.question = "Comment t-appelles tu ?";


        list.add(questionn);
        list.add(questionn1);
        list.add(questionn3);
        list.add(questionn4);
        list.add(questionn5);

        returnedList.questionResponseElements.addAll(list);

        return returnedList;
    }
}