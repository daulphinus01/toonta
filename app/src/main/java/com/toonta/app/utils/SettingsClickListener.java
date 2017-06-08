package com.toonta.app.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.toonta.app.HomePageActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.model.UserMode;
import com.toonta.app.notifs.ToontaAlarmReceiver;

import java.util.ArrayList;

/**
 * Ecouteur chargé du menu des paramètres
 *
 * Created by Marcellin RWEGO on 14/03/2017.
 */

public class SettingsClickListener implements View.OnClickListener {
    private Context context;
    private MainBankDetailAdapter surveysAdapter;
    private ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> allSurveys;
    private ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> unansweredSurveys;

    public SettingsClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        v.setSelected(true);
        final int userMode = ToontaSharedPreferences.getUserMode();

        final MenuBuilder builder = new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.toonta_menu, builder);

        // Setting user mode title
        MenuItem menuItem = builder.findItem(R.id.toonta_user_mode);
        if (userMode == -1) {
            menuItem.setTitle(R.string.switch_to_surveyor_mode);
        } else {
            menuItem.setTitle(R.string.switch_to_user_mode);
        }

        // Notifications
        manageNotifications(builder);

        MenuPopupHelper menuHelper = new MenuPopupHelper(context, builder, v);
        menuHelper.setForceShowIcon(true);
        builder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.toonta_logout_menu:
                        ToontaSharedPreferences.logOut();
                        Intent intent = new Intent(context, HomePageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        return true;
                    case R.id.toonta_share_menu:
                        Utils.startShareActionIntent(context);
                        return true;
                    case R.id.toonta_user_mode:
                        updateViewWithUserMode();
                        updateToontaUserMode(userMode);
                        return true;
                    case R.id.toonta_notification_menu:
                        updateNotifications();
                        ToontaSharedPreferences.updateNotificationsState();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {}
        });

        menuHelper.show();
    }

    public void setSurveysAdapter(MainBankDetailAdapter surveysAdapter) {
        this.surveysAdapter = surveysAdapter;
    }

    public void setAllSurveys(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> allSurveys) {
        this.allSurveys = allSurveys;
    }

    public void setUnansweredSurveys(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> unansweredSurveys) {
        this.unansweredSurveys = unansweredSurveys;
    }

    /**
     * Met à jour le mode User/Surveyor
     *
     * @param userMode -1 correspond au mode User
     *                 et 1 au mode Surveyor
     */
    private void updateToontaUserMode(int userMode) {
        if (userMode == -1) {
            ToontaSharedPreferences.setToontaUserMode(UserMode.SURVEYOR);
        } else {
            ToontaSharedPreferences.setToontaUserMode(UserMode.USER);
        }
    }

    private void manageNotifications(MenuBuilder builder) {
        boolean stateNotif = ToontaSharedPreferences.getNotificationsState();
        if (stateNotif) {
            MenuItem notifItem = builder.findItem(R.id.toonta_notification_menu);
            if (notifItem != null) {
                notifItem.setTitle(R.string.disable_notifications);
                notifItem.setIcon(R.mipmap.notifications_disable);
            }
        }
    }

    private void updateNotifications() {
        boolean stateNotif = ToontaSharedPreferences.getNotificationsState();
        if (!stateNotif) {
            enableNotifications();
        } else {
            disableNotifications();
        }
    }

    /**
     * Active l'alarme gérant les notifications. Cette méthode active également
     * le boot receiver
     */
    private void enableNotifications() {
        Intent alarmIntent = new Intent(context, ToontaAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                context.getResources().getInteger(R.integer.ALARM_INTERVAL),
                pendingIntent);
    }

    /**
     * Désactive l'alarme gérant les notifications. Cette méthode désactive également
     * le boot receiver
     */
    private void disableNotifications() {
        Intent alarmIntent = new Intent(context, ToontaAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

    /**
     * Lorsque le mode User/Surveyor change, on doit mettre à jour
     * les éléments affichés
     */
    private void updateViewWithUserMode() {
        if (surveysAdapter != null
                && allSurveys != null
                && unansweredSurveys != null) {
            // -1 correspond au mode USER
            if (ToontaSharedPreferences.getUserMode() == -1) {
                surveysAdapter.addElements(allSurveys);
            } else {    // Mode surveyor
                surveysAdapter.addElements(unansweredSurveys);
            }
        }
    }
}
