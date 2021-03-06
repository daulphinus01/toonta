package com.toonta.app.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.toonta.app.BuildConfig;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.model.SurveyResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String computeBanksTotalToons(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> bankList) {
        assert bankList != null;
        int total = 0;
        for (ToontaDAO.SurveysListAnswer.SurveyElement bank : bankList) {
            total += bank.reward;
        }
        return total + " toons";
    }

    public static ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> getAnsweredSurvies(ToontaDAO.SurveysListAnswer answeredSurvies) {
        ToontaDAO.SurveysListAnswer tmp = new ToontaDAO.SurveysListAnswer();
        for (ToontaDAO.SurveysListAnswer.SurveyElement se : answeredSurvies.surveyElements) {
            if (se.answered) {
                tmp.surveyElements.add(se);
            }
        }
        return new ArrayList<>(tmp.surveyElements);
    }

    public static boolean bothPwdHaveToBeTheSame(String pwd, String pwdConfrm) {
        return pwd.equals(pwdConfrm);
    }

    public static void startShareActionIntent(Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.toonta_share_url));
        context.startActivity(Intent.createChooser(sharingIntent,"Share using"));
    }

    public static TextView[] initProgressBar(int progressBarSize, LinearLayout parentLineraLayout, Context context) {
        TextView[] tvs = new TextView[progressBarSize];
        for (int i = 0; i < progressBarSize; i++) {
            tvs[i] = new TextView(context);
            tvs[i].setText(".");
            if (i == 0){
                tvs[i].setTextColor(Color.WHITE);
            }
            tvs[i].setTextSize(50);
            tvs[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            parentLineraLayout.addView(tvs[i], i);
        }
        return tvs;
    }

    public static LinearLayout[] instantiateItem(List<ToontaDAO.QuestionsList.QuestionResponse> questionResponse, Context context) {
        LinearLayout returnedLayout[] = new LinearLayout[questionResponse.size()];

        for (int i = 0; i < questionResponse.size(); i++) {
            returnedLayout[i] = new LinearLayout(context);
            returnedLayout[i].setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            returnedLayout[i].setLayoutParams(layoutParams);

            if (questionResponse.get(i).type != null) {
                switch (questionResponse.get(i).type) {
                    case "YES_NO":
                        RadioGroup rg = new RadioGroup(context);
                        rg.setOrientation(RadioGroup.VERTICAL);
                        rg.setTag(ToontaConstants.TOONTA_YES_NO_TAG + questionResponse.get(i).id);

                        RadioGroup.LayoutParams buttonGroupLayoutParams =
                                new RadioGroup.LayoutParams(
                                        RadioGroup.LayoutParams.WRAP_CONTENT,
                                        RadioGroup.LayoutParams.WRAP_CONTENT,
                                        1f);

                        RadioButton yesRB = new RadioButton(context);
                        yesRB.setText(R.string.toonta_radio_button_yes);
                        yesRB.setTextColor(context.getResources().getColor(R.color.radiobutton_selector));
                        rg.addView(yesRB, buttonGroupLayoutParams);
                        // yesRB.setId(context.getResources().getInteger(R.integer.RADIO_BUTTON_ID_YES));

                        RadioButton noRB = new RadioButton(context);
                        noRB.setText(R.string.toonta_radio_button_no);
                        noRB.setTextColor(context.getResources().getColor(R.color.radiobutton_selector));
                        rg.addView(noRB, buttonGroupLayoutParams);
                        // noRB.setId(context.getResources().getInteger(R.integer.RADIO_BUTTON_ID_NO));

                        returnedLayout[i].addView(rg);
                        break;

                    case "BASIC":
                        EditText editText = new EditText(context);
                        // Permet d'avoir le bouton Enter
                        //editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                        editText.setMinLines(3);
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        editText.setTag(ToontaConstants.TOONTA_BASIC_TAG + questionResponse.get(i).id);
                        editText.setLayoutParams(layoutParams);

                        returnedLayout[i].addView(editText);
                        break;

                    case "MULTIPLE_CHOICE":
                        if (questionResponse.get(i).category.equals("RADIO")) {
                            RadioGroup multipleChoiceRG = new RadioGroup(context);
                            multipleChoiceRG.setOrientation(RadioGroup.VERTICAL);
                            multipleChoiceRG.setTag(ToontaConstants.TOONTA_YES_NO_TAG + questionResponse.get(i).id);

                            RadioGroup.LayoutParams multipleChoiceRGGroupLayoutParams =
                                    new RadioGroup.LayoutParams(
                                            RadioGroup.LayoutParams.WRAP_CONTENT,
                                            RadioGroup.LayoutParams.WRAP_CONTENT,
                                            1f);

                            RadioButton[] tabRB = new RadioButton[questionResponse.get(i).choices.size()];
                            for (int k = 0; k < questionResponse.get(i).choices.size(); k++) {
                                tabRB[k] = new RadioButton(context);
                                tabRB[k].setText(questionResponse.get(i).choices.get(k).value);
                                tabRB[k].setTextColor(ContextCompat.getColorStateList(context, R.color.radiobutton_selector));
                                tabRB[k].setTag(questionResponse.get(i).choices.get(k).id);
                                multipleChoiceRG.addView(tabRB[k], multipleChoiceRGGroupLayoutParams);
                            }
                            returnedLayout[i].addView(multipleChoiceRG);

                        }else {
                            CheckBox[] tabCheckBoxes = new CheckBox[questionResponse.get(i).choices.size()];
                            for (int k = 0; k < questionResponse.get(i).choices.size(); k++) {
                                LinearLayout.LayoutParams boxLayoutParams =
                                        new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                tabCheckBoxes[k] = new CheckBox(context);
                                tabCheckBoxes[k].setText(questionResponse.get(i).choices.get(k).value);
                                tabCheckBoxes[k].setTextColor(ContextCompat.getColorStateList(context, R.color.radiobutton_selector));
                                tabCheckBoxes[k].setId(questionResponse.get(i).choices.get(k).id.hashCode());
                                tabCheckBoxes[k].setTag(ToontaConstants.TOONTA_MULTIPLE_CHOICE_TAG + questionResponse.get(i).choices.get(k).id);
                                tabCheckBoxes[k].setLayoutParams(boxLayoutParams);
                                returnedLayout[i].addView(tabCheckBoxes[k]);
                            }
                        }
                        break;
                }
            }
        }
        return returnedLayout;
    }

    public static LinearLayout[] instantiateItemNoAnswerScreen(List<ToontaDAO.QuestionsList.QuestionResponse> questionResponse, Context context) {
        LinearLayout returnedLayout[] = new LinearLayout[questionResponse.size()];
        StringBuilder stringBuilder = new StringBuilder("Answers:");
        for (int z = 0; z < questionResponse.size(); z++) {
            returnedLayout[z] = new LinearLayout(context);
            returnedLayout[z].setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            returnedLayout[z].setLayoutParams(layoutParams);

            TextView respViewArea = new TextView(context);
            respViewArea.setText(questionResponse.get(z).question);
            respViewArea.setTextSize(22f);
            respViewArea.setTag(questionResponse.get(z).id);

            returnedLayout[z].addView(respViewArea);
        }

        return returnedLayout;
    }

    public static JSONObject prepareSurveyResponseAsJSONObject(SurveyResponse surveyResponse) {
        try {
            JSONObject content = new JSONObject();
            JSONArray respArray = new JSONArray();
            for (int i = 0; i < surveyResponse.responses.size(); i++) {
                JSONObject resp = new JSONObject();
                resp.put("choiceId", (surveyResponse.responses.get(i).choiceId == null) ? null : surveyResponse.responses.get(i).choiceId);
                resp.put("questionId", surveyResponse.responses.get(i).questionId);
                resp.put("textAnswer", (surveyResponse.responses.get(i).textAnswer == null) ? null : surveyResponse.responses.get(i).textAnswer);
                resp.put("yesNoAnswer", (surveyResponse.responses.get(i).yesNoAnswer == null) ? null : surveyResponse.responses.get(i).yesNoAnswer);
                respArray.put(i, resp);
            }
            // TODO Utiliser asAFriendUserId. Le setter dans ToontaDAO ou dans cette methode.
            content.put("respondentId", surveyResponse.respondentId);
            content.put("responses", respArray);
            content.put("surveyId", surveyResponse.surveyId);
            if (BuildConfig.DEBUG)
            Log.v("Utils ", content.toString());
            return content;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function used to prepare SurveyResponse as a JSON object that to sent.
     * The respondent id used is that of the friend who answered the survey
     * @param asAFriendUserId the friend's user id
     * @param surveyResponse the survey to send
     * @return JSON object to be sent.
     */
    public static JSONObject prepareSurveyResponseAsJSONObjectUsedAsAFriend(String asAFriendUserId, SurveyResponse surveyResponse) {
        try {
            JSONObject content = new JSONObject();
            JSONArray respArray = new JSONArray();
            for (int i = 0; i < surveyResponse.responses.size(); i++) {
                JSONObject resp = new JSONObject();
                resp.put("choiceId", (surveyResponse.responses.get(i).choiceId == null) ? null : surveyResponse.responses.get(i).choiceId);
                resp.put("questionId", surveyResponse.responses.get(i).questionId);
                resp.put("textAnswer", (surveyResponse.responses.get(i).textAnswer == null) ? null : surveyResponse.responses.get(i).textAnswer);
                resp.put("yesNoAnswer", (surveyResponse.responses.get(i).yesNoAnswer == null) ? null : surveyResponse.responses.get(i).yesNoAnswer);
                respArray.put(i, resp);
            }
            content.put("respondentId", asAFriendUserId);
            content.put("responses", respArray);
            content.put("surveyId", surveyResponse.surveyId);
            if (BuildConfig.DEBUG)
            Log.v("Utils ", content.toString());
            return content;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Affiche un écran de description d'un survey
     *
     * @param context the context of the parent view
     * @param surveyElement the survey whose description is to be shown
     * @param listSurveys the parent view
     */
    public static void packPopupWindow(final Context context, final ToontaDAO.SurveysListAnswer.SurveyElement surveyElement, View listSurveys) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupWindowLayout = inflater.inflate(R.layout.full_window_popup, null, true);
        final PopupWindow popupWindow = new PopupWindow(popupWindowLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.showAtLocation(listSurveys, Gravity.CENTER, 0, 0);

        String popupWindowContentText = ToontaConstants.NO_DESC_FOR_SURVEY;
        if (surveyElement.summary != null && !surveyElement.summary.trim().isEmpty() && !surveyElement.summary.equals("string")) {
            popupWindowContentText = surveyElement.summary;
        }

        ((TextView) popupWindowLayout.findViewById(R.id.survey_description)).setText(popupWindowContentText);

        AppCompatButton ok = (AppCompatButton) popupWindowLayout.findViewById(R.id.popup_ok_button);
        ok.setTransformationMethod(null);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ToontaQuestionActivity.class);
                intent.putExtra(ToontaConstants.QUESTION_TITLE, surveyElement.name);
                intent.putExtra(ToontaConstants.SURVEY_ID, surveyElement.surveyId);
                intent.putExtra(ToontaConstants.SURVEY_REWRD, surveyElement.reward);
                intent.putExtra(ToontaConstants.SURVEY_AUTHOR_ID, surveyElement.authorId);

                popupWindow.dismiss();
                context.startActivity(intent);
            }
        });

        AppCompatButton cancel = (AppCompatButton) popupWindowLayout.findViewById(R.id.popup_cancel_button);
        cancel.setTransformationMethod(null);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ImageView goUp = (ImageView) popupWindowLayout.findViewById(R.id.popup_window_go_up);
        goUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ImageView settings = (ImageView) popupWindowLayout.findViewById(R.id.popup_window_menu_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                // TODO Ajouter le menu des settings
            }
        });
    }

    /**
     * Retourne une liste de questionnairesactifs et non répondus
     *
     * @param surveyElementArrayList ensemble de questionnaires récupérés depuis le serveur
     * @return  liste de questionnaires non répondus
     */
    public static ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> getUnsweredSuryes(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
        ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> unAnsweredSurveys = new ArrayList<>();
        for (ToontaDAO.SurveysListAnswer.SurveyElement se : surveyElementArrayList) {
            if (se.active && !se.answered) {
                unAnsweredSurveys.add(se);
            }
        }
        return unAnsweredSurveys;
    }
}
