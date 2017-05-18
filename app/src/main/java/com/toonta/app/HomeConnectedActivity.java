package com.toonta.app;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.forms.ToontaLogin;
import com.toonta.app.utils.MainBankDetailAdapter;
import com.toonta.app.utils.ProfileActivity;
import com.toonta.app.utils.SettingsClickListener;
import com.toonta.app.utils.Utils;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

import static com.toonta.app.utils.ToontaConstants.DEFAULT_NBR_SURVEYS;
import static com.toonta.app.utils.ToontaConstants.NOTIFS_TAG;
import static com.toonta.app.utils.Utils.getUnsweredSuryes;


public class HomeConnectedActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private MainBankDetailAdapter surveysAdapter;
    private ListView surviesListView;
    private NewSurveysInteractor newSurveysInteractor;
    private ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> allSurveys;
    private ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> unansweredSurveys;

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

            // Settings
            ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
            final SettingsClickListener settingsClickListener = new SettingsClickListener(HomeConnectedActivity.this);
            toontaMenuButton.setOnClickListener(settingsClickListener);

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
            settingsClickListener.setSurveysAdapter(surveysAdapter);

            surviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final ToontaDAO.SurveysListAnswer.SurveyElement surveyElement = surveysAdapter.getItem(position);

                    // PopupWindow affichant la description du survey
                    Utils.packPopupWindow(HomeConnectedActivity.this, surveyElement, findViewById(R.id.list_surveys));
                }
            });

            surviesListView.setOnScrollListener(scrollListener);

            // Showing loading window
            progressDialog.show();
            newSurveysInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.NewSurveysViewUpdater() {
                @Override
                public void onNewSurveys(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList, boolean reset) {
                    // TODO Empty method
                }

                @Override
                public void onPopulateSurvies(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
                    // Surveys total
                    allSurveys = surveyElementArrayList;
                    // Surveys non répondus
                    unansweredSurveys = getUnsweredSuryes(surveyElementArrayList);

                    // MAJ du listener
                    settingsClickListener.setAllSurveys(allSurveys);
                    settingsClickListener.setUnansweredSurveys(unansweredSurveys);

                    // On met le nombre de surveys non répondus dans les préférebces
                    ToontaSharedPreferences.setSharedPreferencesSurveysNbr(unansweredSurveys.size());

                    // En mode user, on affiche les surveys non répondus. Sinon on affiche tous les surveys
                    // -1 correspond au mode USER
                    if (ToontaSharedPreferences.getUserMode() == -1) {
                        surveysAdapter.addElements(unansweredSurveys);
                    } else {
                        surveysAdapter.addElements(allSurveys);
                    }

                    // Dismissing loading window
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
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

            View mCustomView = mInflater.inflate(R.layout.custom_actionbar_ecran_accueil, null);
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
                    startActivity(intent);
                }
            });
        }
    }

    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            boolean hasTouchedBottom = (firstVisibleItem + visibleItemCount) == totalItemCount;
            if (hasTouchedBottom && allSurveys != null) {
                int surveysNbr = Utils.getUnsweredSuryes(allSurveys).size();
                int storedNbrSurveys = ToontaSharedPreferences.getSharedPreferencesSurveysNbr();
                // S'il y a des nouveaux questionnaires, on met à jour la page entière
                if (surveysNbr != DEFAULT_NBR_SURVEYS && surveysNbr != storedNbrSurveys) {
                    newSurveysInteractor.fetchAllSurvies();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(NOTIFS_TAG);
                }
            }
        }
    };
}
