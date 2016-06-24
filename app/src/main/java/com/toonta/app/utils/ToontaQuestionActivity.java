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
import java.util.Map;

import static com.toonta.app.utils.ToontaConstants.YES_RADIO_BUTTON_ID;

public class ToontaQuestionActivity extends AppCompatActivity {

    private String surveyId;
    private int surveyQstCptr = 0;
    private Button previousButton;
    private Button nextSubmitButton;
    private LinearLayout qstRespPart;
    private String currentQstId = "";
    private TextView textViewQuestionPart;
    private ToontaDAO.QuestionsList questionsList;
    private NewSurveysInteractor newSurveyInteractor;
    private NewSurveysInteractor newSurveyPostInteractor;
    private static String TAG = "ToontaQuestionActivity ";
    private Responses responsesToBeSent = new Responses();
    private SurveyResponse surveyResponse = new SurveyResponse();

    // Indicates the current question being displayed
    private int currentQuestionPos = 0;

    // Message shown when Next / Submit is hit without any answer.
    private String errorMsg = "";

    // Number of pages
    private int nbrTotalPages = 0;

    // It is used to map questions ids to index used on views
    private Map<Integer, String> intToStringIndex = new HashMap<>();

    // Id des radio button YES et NO
    private int yesRadioButtonId = "YES".hashCode();
    private int noRadioButtonId = "NO".hashCode();

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toonta_question);

        // SurveyId
        surveyId = getIntent().getStringExtra(ToontaConstants.SURVEY_ID);

        responsesToBeSent.respondentId = ToontaSharedPreferences.toontaSharedPreferences.userId;
        responsesToBeSent.surveyId = surveyId;

        setupActionBar();

        questionsList = dummyData1();

        // Screen title
        final String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);
        TextView textViewTitle = (TextView) findViewById(R.id.qustion_screen_title);
        assert textViewTitle != null;
        textViewTitle.setText(titleQuestionScreen);

        // Question area
        textViewQuestionPart = (TextView) findViewById(R.id.qustion_screen_question);

        // Screen Previous Buttons
        previousButton = (Button) findViewById(R.id.button_previous);
        nextSubmitButton = (Button) findViewById(R.id.button_submit_next);

        assert previousButton != null;
        previousButton.setEnabled(false);
        previousButton.setTransformationMethod(null);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currPos = mViewPager.getCurrentItem();
                // Verification que les questions ont bien ete repondues
                String msgRetour = validateQuestionAndPrepareToSend(questionsList.questionResponseElements.get(currPos));
                if (msgRetour.trim().isEmpty()) {
                    // TODO Supprimer ce log
                    Log.v("=======================", responsesToBeSent.toString());

                    if (currPos > 0) {
                        textViewQuestionPart.setText(questionsList.questionResponseElements.get(currPos - 1).question);
                    }
                    if (currPos >= 1) {
                        if ((currPos - 1) == 0) {
                            previousButton.setEnabled(false);
                        }
                        mViewPager.setCurrentItem(currPos - 1);
                        currentQuestionPos--;
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
                int currPos = mViewPager.getCurrentItem();
                // Verification que les questions ont bien ete repondues
                String msgRetour = validateQuestionAndPrepareToSend(questionsList.questionResponseElements.get(currPos));
                if (/*msgRetour.trim().isEmpty()*/ true) {
                    // TODO Supprimer ce log
                    Log.v("=======================", responsesToBeSent.toString());

                    /*if (currPos < nbrTotalPages) {
                        textViewQuestionPart.setText(questionsList.questionResponseElements.get(currPos + 1).question);
                    }*/
                    if (/*currPos == nbrTotalPages*/ true) {
                        // TODO Renvoyer les reponses au serveur
                        // On envoie les reponses au serveur, om passe a une autre activite
                        Intent validateQuestionActivityIntent = new Intent(ToontaQuestionActivity.this, ValidateQuestionActivity.class);
                        validateQuestionActivityIntent.putExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT, responsesToBeSent);
                        validateQuestionActivityIntent.putExtra(ToontaConstants.QUESTION_TITLE, titleQuestionScreen);
                        startActivity(validateQuestionActivityIntent);
                    } else if (currPos == (nbrTotalPages - 1)) {
                        nextSubmitButton.setText(R.string.submit);
                        mViewPager.setCurrentItem(currPos + 1);
                        currentQuestionPos++;
                    } else {
                        mViewPager.setCurrentItem(currPos + 1);
                        currentQuestionPos++;
                        previousButton.setEnabled(true);
                    }
                } else {
                    // Validation de question echouee
                    Snackbar.make(findViewById(android.R.id.content), msgRetour, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // TODO When there is non question for this survey, qstRespPart is hidden
        qstRespPart = (LinearLayout) findViewById(R.id.qst_resp_part_screen);

        ToontaQuestionPageAdapter toontaQuestionPageAdapter = new ToontaQuestionPageAdapter(questionsList);
        mViewPager = (ViewPager) findViewById(R.id.toonta_question_view_pager_area);
        assert mViewPager != null;
        mViewPager.setAdapter(toontaQuestionPageAdapter);

        // Fetching survies from server
        /*newSurveyInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.OneSurveyViewUpdator() {
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




    public class ToontaQuestionPageAdapter extends PagerAdapter {

        private ToontaDAO.QuestionsList mQuestionsList;

        public ToontaQuestionPageAdapter(ToontaDAO.QuestionsList questionsList) {
            this.mQuestionsList = questionsList;
            if (questionsList.questionResponseElements.size() >= 1) {
                nextSubmitButton.setText(R.string.next_button_next_as_text);
                // Le compteur commence a zero, d'ou le moins un
                nbrTotalPages = questionsList.questionResponseElements.size() - 1;
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

            LinearLayout linearLayout = new LinearLayout(ToontaQuestionActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setLayoutParams(layoutParams);

            if (questionResponse.type != null) {
                switch (questionResponse.type) {
                    case "YES_NO":
                        RadioGroup rg = new RadioGroup(ToontaQuestionActivity.this);
                        rg.setOrientation(RadioGroup.VERTICAL);
                        rg.setTag(ToontaConstants.TOONTA_YES_NO_TAG + questionResponse.id);

                        RadioGroup.LayoutParams buttonGroupLayoutParams =
                                new RadioGroup.LayoutParams(
                                        RadioGroup.LayoutParams.WRAP_CONTENT,
                                        RadioGroup.LayoutParams.WRAP_CONTENT,
                                        1f);

                        RadioButton yesRB = new RadioButton(ToontaQuestionActivity.this);
                        yesRB.setText(R.string.toonta_radio_button_yes);
                        rg.addView(yesRB, buttonGroupLayoutParams);
                        yesRB.setId(getResources().getInteger(R.integer.YES_RADIO_BUTTON_ID));

                        RadioButton noRB = new RadioButton(ToontaQuestionActivity.this);
                        noRB.setText(R.string.toonta_radio_button_no);
                        rg.addView(noRB, buttonGroupLayoutParams);
                        noRB.setId(getResources().getInteger(R.integer.NO_RADIO_BUTTON_ID));

                        linearLayout.addView(rg);
                        break;

                    case "BASIC":
                        EditText editText = new EditText(ToontaQuestionActivity.this);
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                        editText.setMinLines(3);
                        editText.setTag(ToontaConstants.TOONTA_BASIC_TAG + questionResponse.id);
                        System.out.println(ToontaConstants.TOONTA_BASIC_TAG + questionResponse.id);
                        editText.setLayoutParams(layoutParams);

                        linearLayout.addView(editText);
                        break;

                    case "MULTIPLE_CHOICE":
                        CheckBox[] tabCheckBoxes = new CheckBox[questionResponse.choices.size()];
                        for (int i = 0; i < questionResponse.choices.size(); i++) {
                            LinearLayout.LayoutParams boxLayoutParams =
                                    new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            tabCheckBoxes[i] = new CheckBox(ToontaQuestionActivity.this);
                            tabCheckBoxes[i].setText(questionResponse.choices.get(i).value);
                            tabCheckBoxes[i].setId(questionResponse.choices.get(i).id.hashCode());
                            intToStringIndex.put(questionResponse.choices.get(i).id.hashCode(), questionResponse.choices.get(i).id);
                            tabCheckBoxes[i].setTag(ToontaConstants.TOONTA_MULTIPLE_CHOICE_TAG + questionResponse.choices.get(i).id);
                            tabCheckBoxes[i].setLayoutParams(boxLayoutParams);
                            linearLayout.addView(tabCheckBoxes[i]);
                        }
                        break;
                }
            }

            container.addView(linearLayout);
            return linearLayout;
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
    }

    private String validateQuestionAndPrepareToSend(ToontaDAO.QuestionsList.QuestionResponse qstResp) {
        // TODO To be deleted
        Log.v("  0 - instantiateItem ", mViewPager.getCurrentItem() + " ");
        Log.v("  1 - instantiateItem ", qstResp.toString());
        String questionId = qstResp.id;
        boolean qstAnswered = false;
        LinearLayout currentPage = (LinearLayout) mViewPager.getChildAt(currentQuestionPos);

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
                EditText respEditText = (EditText) currentPage.getChildAt(currentQuestionPos);
                if (respEditText == null || respEditText.getText().toString().trim().length() <= 2) {
                    System.out.println(respEditText);
                    System.out.println(ToontaConstants.TOONTA_BASIC_TAG + qstResp.id);
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
                            if (resp.questionId.equals(questionId) && resp.choiceId.equals(choiceId)) {
                                resp.textAnswer = checkBox.getText().toString();
                                qstAnswered = true;
                                break;
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
        questionn4.type = "BASIC";
        questionn4.id = "12111";
        questionn4.question = "Comment t-appelles tu ?";


        list.add(questionn);
        list.add(questionn1);
        list.add(questionn3);
        list.add(questionn4);
        list.add(questionn5);

        returnedList.questionResponseElements.addAll(list);

        return returnedList;
    }
}