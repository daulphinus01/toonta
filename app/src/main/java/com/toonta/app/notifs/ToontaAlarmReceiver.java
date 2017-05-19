package com.toonta.app.notifs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.utils.Utils;

import java.util.ArrayList;

import static com.toonta.app.utils.ToontaConstants.NOTIFS_TAG;
import static com.toonta.app.utils.ToontaConstants.DEFAULT_NBR_SURVEYS;

/**
 * Selon l'intervalle donné, cette classe doit envoyer une requête au serveur pour
 * vérifier s'il y a de nouveaux questionnaires. En cas de nouveaux fonctionnaires,
 * une notification est affichée.
 *
 * Created by Marcellin RWEGO on 31/03/2017.
 */

public class ToontaAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        queryServer(context);
    }

    /**
     * Requête le server pour voir s'il y a des nouveaux questionnaires.
     * @param context le contexte utilisé
     */
    private void queryServer(final Context context) {
        new NewSurveysInteractor(context, new NewSurveysInteractor.NewSurveysViewUpdater() {
            @Override
            public void onNewSurveys(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList, boolean reset) {
                // Rien à faire
            }

            @Override
            public void onPopulateSurvies(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
                int surveysNbr = Utils.getUnsweredSuryes(surveyElementArrayList).size();
                int storedNbrSurveys = ToontaSharedPreferences.getSharedPreferencesSurveysNbr();
                // S'il y a des nouveaux questionnaires, on affiche une notification
                if (surveysNbr != DEFAULT_NBR_SURVEYS && surveysNbr > storedNbrSurveys) {
                    ToontaSharedPreferences.setSharedPreferencesSurveysNbr(surveysNbr);
                    createNotification(context);
                }
            }

            @Override
            public void onRefreshProgress() {
                // Rien à faire
            }

            @Override
            public void onRefreshDone() {
                // Rien à faire
            }

            @Override
            public void onFailure(String error) {
                // Rien à faire
            }
        }).fetchAllSurvies();
    }

    /**
     * Créée une notification à afficher
     * @param context contexte de la notification
     */
    private void createNotification(Context context) {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.toonta_logo_modifiee_en_ligne)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.message_notifications))
                .setAutoCancel(true);

        Intent homePageIntent = new Intent(context, HomeConnectedActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context)
                .addParentStack(HomeConnectedActivity.class)
                .addNextIntent(homePageIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notifBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notifiManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // TAG allows to update the notification later on.
        notifiManager.notify(NOTIFS_TAG, notifBuilder.build());
    }
}
