package com.toonta.app.utils;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.toonta.app.BuildConfig;
import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.forms.SurveyValidationAsAFriendActivity;
import com.toonta.app.model.SurveyResponse;
import com.toonta.app.model.UserMode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ToontaQuestionActivity extends AppCompatActivity {

    private String authorId;
    private Button previousButton;
    private Button nextSubmitButton;
    private LinearLayout qstRespPart;
    private TextView textViewQuestionPart;
    private LinearLayout questionProgressBar;
    private ToontaDAO.QuestionsList questionsList;
    private static String TAG = "ToontaQuestionActivity ";
    private SurveyResponse responsesToBeSent = new SurveyResponse();

    // Indicates the current question being displayed
    private int currentQuestionPos = 0;

    // Number of pages
    private int nbrTotalPages = 0;

    // It is used to map questions ids to index used on views
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<Integer, String> intToStringIndex = new HashMap<>();

    private TextView[] progressBarDots;

    private LinearLayout[] questionLinearLayouts;
    private LinearLayout questionArea;

    private AlertDialog.Builder builder;

    public ToontaQuestionActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toonta_question);

        // SurveyId & AuthorId
        String surveyId = getIntent().getStringExtra(ToontaConstants.SURVEY_ID);
        authorId = getIntent().getStringExtra(ToontaConstants.SURVEY_AUTHOR_ID);

        responsesToBeSent.respondentId = ToontaSharedPreferences.toontaSharedPreferences.userId;
        responsesToBeSent.surveyId = surveyId;

        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new SettingsClickListener(ToontaQuestionActivity.this));

        questionProgressBar = (LinearLayout) findViewById(R.id.toonta_question_answering_progress_bar);

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
            }
        });

        nextSubmitButton.setTransformationMethod(null);
        nextSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageNextAction(titleQuestionScreen);
            }
        });

        questionArea = (LinearLayout) findViewById(R.id.toonta_question_view_pager_area);

        // Fetching survy from server (with its questions)
        NewSurveysInteractor newSurveyInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.OneSurveyViewUpdator() {
            @Override
            public void onGetSurvey(ToontaDAO.QuestionsList qstList) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, qstList.toString());
                }
                questionsList = qstList;

                // When there is non question for this survey, qstRespPart is hidden
                qstRespPart = (LinearLayout) findViewById(R.id.qst_resp_part_screen);
                if (questionsList.questionResponseElements == null || questionsList.questionResponseElements.size() < 1) {
                    qstRespPart.setVisibility(View.INVISIBLE);
                    Snackbar.make(findViewById(android.R.id.content), R.string.toonta_no_qst_for_survey, Snackbar.LENGTH_LONG).show();
                } else {
                    // Par defaut, le buouton next n'a pas de text. Il est settee pour la premiere fois
                    nextSubmitButton.setText(R.string.next_button_next_as_text);

                    // Barre de progression mise a jour
                    progressBarDots = Utils.initProgressBar(questionsList.questionResponseElements.size(), questionProgressBar, ToontaQuestionActivity.this);

                    // Barre de progrssion initialisee
                    progressBarDots[currentQuestionPos].setTextColor(Color.WHITE);

                    // Sorting questions by order
                    Collections.sort(questionsList.questionResponseElements);

                    // La question posee
                    textViewQuestionPart = (TextView) findViewById(R.id.qustion_screen_question);
                    textViewQuestionPart.setText(questionsList.questionResponseElements.get(currentQuestionPos).question);

                    // Tous les linearlayout pour toutes les reponses
                    questionLinearLayouts = Utils.instantiateItem(questionsList.questionResponseElements, ToontaQuestionActivity.this);

                    // Ajout des click listeners pour tous les EditText
                    addClickListenerToAllEditText(questionLinearLayouts, titleQuestionScreen);

                    // La partie de reponse
                    questionArea.addView(questionLinearLayouts[currentQuestionPos]);

                    // Le compteur commence a zero, d'ou le moins un
                    nbrTotalPages = questionsList.questionResponseElements.size() - 1;
                }
            }

            @Override
            public void onPostResponse(String statusCode) {
                // TODO Deal with 200 status code!
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });

        // Fecthing survey
        newSurveyInteractor.fetchSurvey(surveyId);

        // Affiche le msg selon si la reponse a bien ete envoye au serveur ou pas
        builder = new AlertDialog.Builder(ToontaQuestionActivity.this);
        builder.setTitle(getString(R.string.toonta_survey_validation_dialog_title));

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

            View mCustomView = getLayoutInflater().inflate(R.layout.custom_actionbar_with_exit_survey_button, null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            mActionBar.setCustomView(mCustomView, layoutParams);
            Toolbar parent = (Toolbar) mCustomView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    public void goUp(View view) {
        NavUtils.navigateUpFromSameTask(ToontaQuestionActivity.this);
    }

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
                    for (SurveyResponse.AtomicResponseRequest resp : responsesToBeSent.responses) {
                        if (resp.questionId.equals(questionId)) {
                            resp.yesNoAnswer = yesNoAnswer;
                            qstAnswered = true;
                            break;
                        }
                    }
                    // La question vient d'etre abordee pour la premiere fois
                    if (!qstAnswered) {
                        // La question n'a pas encore ete repondue
                        SurveyResponse.AtomicResponseRequest resp = new SurveyResponse.AtomicResponseRequest();
                        resp.yesNoAnswer = yesNoAnswer;
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
                    for (SurveyResponse.AtomicResponseRequest resp : responsesToBeSent.responses) {
                        if (resp.questionId.equals(questionId)) {
                            resp.textAnswer = respEditText.getText().toString().trim();
                            qstAnswered = true;
                            break;
                        }
                    }

                    // La question vient d'etre abordee pour la premiere fois
                    if (!qstAnswered) {
                        // La question n'a pas encore ete repondue
                        SurveyResponse.AtomicResponseRequest resp = new SurveyResponse.AtomicResponseRequest();
                        resp.textAnswer = respEditText.getText().toString().trim();
                        resp.questionId = questionId;
                        responsesToBeSent.responses.add(resp);
                    }

                    // On retourne une chaine vide pour signaler que tout s'est bien passe
                    return "";
                }
            case "MULTIPLE_CHOICE" :
                int nbrCheckBoxes = currentPage.getChildCount();
                // Si ce boolean est toujours faulse a la sortie de la boucle, aucune case
                // n'aura ete cochee. Et donc renvoyer une chaine contenant le message d'erreur.
                boolean atleastOneChoiceIsSelected = false;

                if (qstResp.category.equals("RADIO")) {
                    RadioGroup multipleChoiceRG = (RadioGroup) currentPage.findViewWithTag(ToontaConstants.TOONTA_YES_NO_TAG + qstResp.id);
                    int selectedMultipleChoiceRGId = multipleChoiceRG.getCheckedRadioButtonId();
                    if (selectedMultipleChoiceRGId == -1) {
                        return getString(R.string.select_one_button);
                    } else {
                        // Un radiobutton a ete cochee
                        RadioButton selectedRB = (RadioButton) multipleChoiceRG.findViewById(selectedMultipleChoiceRGId);
                        if (selectedRB == null) {
                            return getString(R.string.smth_wrong_re_answer_the_qst);
                        }

                        // Verifier si la question n'existe PAS dans l'enregistrement des reponses
                        // Probablement parce qu'on a clique sur PREVIOUS
                        for (SurveyResponse.AtomicResponseRequest resp : responsesToBeSent.responses) {
                            if (resp.questionId.equals(questionId) && selectedRB.getText() != null && !selectedRB.getText().toString().trim().isEmpty()) {
                                System.out.println(selectedRB.getText().toString().trim());
                                resp.textAnswer = selectedRB.getText().toString().trim();
                                qstAnswered = true;
                                break;
                            }
                        }
                        // La question vient d'etre abordee pour la premiere fois
                        if (!qstAnswered) {
                            SurveyResponse.AtomicResponseRequest resp = new SurveyResponse.AtomicResponseRequest();
                            resp.textAnswer = selectedRB.getText().toString().trim();
                            resp.questionId = questionId;
                            resp.choiceId = (String) selectedRB.getTag();
                            responsesToBeSent.responses.add(resp);
                        }

                        // On retourne une chaine vide pour signaler que tout s'est bien passe
                        return "";
                    }
                } else {
                    for (int i = 0; i < nbrCheckBoxes; i++) {
                        CheckBox checkBox = (CheckBox) currentPage.findViewWithTag(ToontaConstants.TOONTA_MULTIPLE_CHOICE_TAG + qstResp.choices.get(i).id);
                        if (checkBox.isChecked()) {
                            atleastOneChoiceIsSelected = true;
                            String choiceId = intToStringIndex.get(checkBox.getId());
                            // Verifier si la question n'existe PAS dans l'enregistrement des reponses
                            // Probablement parce qu'on a clique sur PREVIOUS
                            for (SurveyResponse.AtomicResponseRequest resp : responsesToBeSent.responses) {
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
                                SurveyResponse.AtomicResponseRequest resp = new SurveyResponse.AtomicResponseRequest();
                                resp.textAnswer = checkBox.getText().toString();
                                resp.questionId = questionId;
                                resp.choiceId = choiceId;
                                responsesToBeSent.responses.add(resp);
                            }
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
                return "Unknown type of question";
        }
    }

    /**
     * Gestion de l'évenement "Suivant" lorsqu'on est en train de répondre aux questionnaires
     *
     * @param titleQuestionScreen titre de l'écran
     */
    private void manageNextAction(String titleQuestionScreen) {
        // Verification que les questions ont bien ete repondues
        String msgRetour = validateQuestionAndPrepareToSend(questionLinearLayouts[currentQuestionPos]);
        if (msgRetour.trim().isEmpty()) {
            if (currentQuestionPos == nbrTotalPages) {
                int userMode = ToontaSharedPreferences.getUserMode();
                if (userMode == UserMode.USER.getMode()) {
                    NewSurveysInteractor newSurveysInteractorBis = new NewSurveysInteractor(ToontaQuestionActivity.this, new NewSurveysInteractor.SurviesIDsUpdater() {
                        @Override
                        public void onSuccess(boolean existAnsweredId) {
                            if (existAnsweredId) {
                                Snackbar.make(findViewById(android.R.id.content), "Survey already validated 'as yourself'", Snackbar.LENGTH_LONG).show();
                            } else {
                                createNewSurveysInteractorAndSendResponses(responsesToBeSent);
                            }
                        }

                        @Override
                        public void onFailure(String error) {

                        }
                    });
                    newSurveysInteractorBis.existAnsweredId(authorId, responsesToBeSent.surveyId);
                } else {
                    // On demarre l'intent de formulaire de validation pour autrui
                    Intent surveyValidationAsAFriendActivityIntent = new Intent(ToontaQuestionActivity.this, SurveyValidationAsAFriendActivity.class);
                    surveyValidationAsAFriendActivityIntent.putExtra(ToontaConstants.QUESTION_TITLE, titleQuestionScreen);
                    surveyValidationAsAFriendActivityIntent.putExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT, responsesToBeSent);

                    startActivity(surveyValidationAsAFriendActivityIntent);
                }
            } else {
                currentQuestionPos++;
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

    /**
     * Lorsque l'utilisateur clique sur DONE, on fait la même action que le bouton Next/Submit
     * @param linearLayouts l'ensemble des viues dans lesquelles se trouvent les EditText qui
     *                      nous intéressent
     * @param titleQuestionScreen le titre de l'écran
     */
    private void addClickListenerToAllEditText(LinearLayout[] linearLayouts, final String titleQuestionScreen) {
        for (LinearLayout linearLayout : linearLayouts) {
            final View editTextView = linearLayout.getChildAt(0);
            if (editTextView instanceof EditText) {
                ((EditText) editTextView).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            manageNextAction(titleQuestionScreen);
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private NewSurveysInteractor createNewSurveysInteractorAndSendResponses(SurveyResponse surveyResponse) {

        NewSurveysInteractor interactor = new NewSurveysInteractor(ToontaQuestionActivity.this, new NewSurveysInteractor.OneSurveyViewUpdator() {
            @Override
            public void onGetSurvey(ToontaDAO.QuestionsList QuestionsList) {
                // Rien a faire ici
            }

            @Override
            public void onPostResponse(String statusCode) {
                // Msg retourne lors de l'envoie des reponses
                builder.setMessage(getString(R.string.toonta_survey_validation_dialog_msg));
                builder.setPositiveButton(R.string.toonat_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ToontaQuestionActivity.this, HomeConnectedActivity.class));
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
                        return;
                    }
                });
                builder.setCancelable(false);
                builder.create().show();

            }
        });

        interactor.postSurveyResponse(surveyResponse);

        return interactor;
    }
}