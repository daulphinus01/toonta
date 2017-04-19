package com.toonta.app;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.forms.ToontaLogin;
import com.toonta.app.notifs.ToontaAlarmReceiver;
import com.toonta.app.utils.MainBankDetailAdapter;
import com.toonta.app.utils.ProfileActivity;
import com.toonta.app.utils.SettingsClickListener;
import com.toonta.app.utils.Utils;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class HomeConnectedActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private MainBankDetailAdapter surveysAdapter;
    private ListView surviesListView;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home_connected);

        ToontaSharedPreferences.init(getApplicationContext());
        ToontaDAO.init(getApplicationContext());
        if (ToontaSharedPreferences.toontaSharedPreferences.userId == null || ToontaSharedPreferences.toontaSharedPreferences.requestToken == null){
            startActivity(new Intent(HomeConnectedActivity.this, HomePageActivity.class));
            finish();
        } else {

            // Actionbar
            setupActionBar();

            // TODO Notifications
            Intent alarmIntent = new Intent(HomeConnectedActivity.this, ToontaAlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(HomeConnectedActivity.this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    getResources().getInteger(R.integer.ALARM_INTERVAL),
                    pendingIntent);
            Toast.makeText(this, "Toonta Alarm Set", Toast.LENGTH_SHORT).show();

            // Settings
            ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
            toontaMenuButton.setOnClickListener(new SettingsClickListener(HomeConnectedActivity.this));

            Button bankButton = (Button) findViewById(R.id.bank_button);
            toontaSetOnClickListener(bankButton, BankDetailActivity.class);

            Button profileButton = (Button) findViewById(R.id.profile_button);
            toontaSetOnClickListener(profileButton, ProfileActivity.class);

            // Progress mechanisme when verifying credential
            progressDialog = new ProgressDialog(HomeConnectedActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.string_loading_survies_activity));

            surviesListView = (ListView) findViewById(R.id.list_surveys);

            surveysAdapter = new MainBankDetailAdapter(getBaseContext());
            assert surviesListView != null;
            surviesListView.setAdapter(surveysAdapter);

            surviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final ToontaDAO.SurveysListAnswer.SurveyElement surveyElement = surveysAdapter.getItem(position);

                    // PopupWindow affichant la description du survey
                    Utils.packPopupWindow(HomeConnectedActivity.this, surveyElement, findViewById(R.id.list_surveys));
                }
            });

            // Showing loading window
            progressDialog.show();
            NewSurveysInteractor newSurveysInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.NewSurveysViewUpdater() {
                @Override
                public void onNewSurveys(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList, boolean reset) {
                    // TODO Empty method
                }

                @Override
                public void onPopulateSurvies(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
                    // Dismissing loading window
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                    // Only unanswered surveys sont affichés
                    surveysAdapter.addElements(getUnsweredSuryes(surveyElementArrayList));
                }

                @Override
                public void onRefreshProgress() {
                    // TODO Empty method
                }

                @Override
                public void onRefreshDone() {
                    // TODO Empty method
                }

                @Override
                public void onFailure(String error) {
                    // Dismissing loading window
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (surviesListView.getChildCount() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
                    }
                    ToontaSharedPreferences.logOut();
                    startActivity(new Intent(HomeConnectedActivity.this, ToontaLogin.class));
                }
            });

            // Fetching survies
            newSurveysInteractor.fetchAllSurvies();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dismissing loading window
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar mActionBar = getSupportActionBar();
            assert mActionBar != null;
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }

    private void toontaSetOnClickListener(View button, final Class<?> cls) {
        if (button != null && cls != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), cls);

                    // TODO To be deleted
                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);
                    Toast.makeText(HomeConnectedActivity.this, "Alarm Canceled", Toast.LENGTH_SHORT).show();

                    startActivity(intent);
                }
            });
        }
    }

    private ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> getUnsweredSuryes(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
        ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> unAnsweredSurveys = new ArrayList<>();
        for (ToontaDAO.SurveysListAnswer.SurveyElement se : surveyElementArrayList) {
            if (!se.answered) {
                unAnsweredSurveys.add(se);
            }
        }
        return unAnsweredSurveys;
    }
}
