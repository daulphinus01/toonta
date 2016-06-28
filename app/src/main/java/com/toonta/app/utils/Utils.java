package com.toonta.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
}
