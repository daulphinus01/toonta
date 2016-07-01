package com.toonta.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.toonta.app.HomePageActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.model.Bank;

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

    public static boolean bothPwdHaveToBeTheSame(String pwd, String pwdConfrm) {
        return pwd.equals(pwdConfrm);
    }

    public static ActionMode.Callback initActionModeCallBack(final Context context) {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.toonta_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.toonta_logout_menu:
                        ToontaSharedPreferences.logOut();
                        mode.finish(); // Action picked, so close the CAB
                        context.startActivity(new Intent(context, HomePageActivity.class));
                        ((Activity)context).finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // actionModeCallBack = null;
            }
        };
    }

    public static void startShareActionIntent(Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Try Toonta for Smartphone. Download it here : https://www.heebari.com");
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
                        rg.addView(yesRB, buttonGroupLayoutParams);
                        yesRB.setId(context.getResources().getInteger(R.integer.YES_RADIO_BUTTON_ID));

                        RadioButton noRB = new RadioButton(context);
                        noRB.setText(R.string.toonta_radio_button_no);
                        rg.addView(noRB, buttonGroupLayoutParams);
                        noRB.setId(context.getResources().getInteger(R.integer.NO_RADIO_BUTTON_ID));

                        returnedLayout[i].addView(rg);
                        break;

                    case "BASIC":
                        EditText editText = new EditText(context);
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                        editText.setMinLines(3);
                        editText.setTag(ToontaConstants.TOONTA_BASIC_TAG + questionResponse.get(i).id);
                        System.out.println(ToontaConstants.TOONTA_BASIC_TAG + questionResponse.get(i).id);
                        editText.setLayoutParams(layoutParams);

                        returnedLayout[i].addView(editText);
                        break;

                    case "MULTIPLE_CHOICE":
                        CheckBox[] tabCheckBoxes = new CheckBox[questionResponse.get(i).choices.size()];
                        for (int k = 0; k < questionResponse.get(i).choices.size(); k++) {
                            LinearLayout.LayoutParams boxLayoutParams =
                                    new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            tabCheckBoxes[k] = new CheckBox(context);
                            tabCheckBoxes[k].setText(questionResponse.get(i).choices.get(k).value);
                            tabCheckBoxes[k].setId(questionResponse.get(i).choices.get(k).id.hashCode());
                            tabCheckBoxes[k].setTag(ToontaConstants.TOONTA_MULTIPLE_CHOICE_TAG + questionResponse.get(i).choices.get(k).id);
                            tabCheckBoxes[k].setLayoutParams(boxLayoutParams);
                            returnedLayout[i].addView(tabCheckBoxes[k]);
                        }
                        break;
                }
            }
        }
        return returnedLayout;
    }
}
