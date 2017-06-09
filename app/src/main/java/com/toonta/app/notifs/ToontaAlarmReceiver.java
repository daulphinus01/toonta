package com.toonta.app.notifs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.toonta.app.BuildConfig;
import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.utils.Utils;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import static com.toonta.app.utils.ToontaConstants.DEFAULT_NBR_SURVEYS;
import static com.toonta.app.utils.ToontaConstants.NOTIFS_TAG;

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
        if (ToontaSharedPreferences.toontaSharedPreferences == null) {
            ToontaSharedPreferences.init(context);
        }
        if (ToontaSharedPreferences.toontaSharedPreferences != null
                && ToontaSharedPreferences.toontaSharedPreferences.userId != null
                && ToontaSharedPreferences.toontaSharedPreferences.requestToken != null) {
            queryServer(context,
                    ToontaSharedPreferences.toontaSharedPreferences.userId,
                    ToontaSharedPreferences.toontaSharedPreferences.requestToken);
        }
    }

    /**
     * Requête le server pour voir s'il y a des nouveaux questionnaires.
     * @param context le contexte utilisé
     */
    private void queryServer(final Context context, final String userId, final String userToken) {
        Volley.newRequestQueue(context).add(new JsonArrayRequest(Request.Method.GET,
                "http://92.222.90.138:8080/toonta-api/survey/list?user=" + userId,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (BuildConfig.DEBUG) {
                            Log.v("ALMRCVER Surveys list", response.toString());
                        }
                        int surveysNbr = Utils.getUnsweredSuryes(ToontaDAO.parseSurveyList(response).surveyElements).size();
                        int storedNbrSurveys = ToontaSharedPreferences.getSharedPreferencesSurveysNbr();
                        // S'il y a des nouveaux questionnaires, on affiche une notification
                        if (surveysNbr != DEFAULT_NBR_SURVEYS && surveysNbr > storedNbrSurveys) {
                            ToontaSharedPreferences.setSharedPreferencesSurveysNbr(surveysNbr);
                            createNotification(context);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (BuildConfig.DEBUG) {
                            Log.e("ALMRCVER Surveys list", error.toString());
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("userId", userId);
                params.put("userToken", userToken);

                return params;
            }
        });
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
