package com.toonta.app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.forms.ToontaLogin;
import com.toonta.app.utils.MainBankDetailAdapter;
import com.toonta.app.utils.ProfileActivity;
import com.toonta.app.utils.ToontaConstants;
import com.toonta.app.utils.ToontaQuestionActivity;
import com.toonta.app.utils.Utils;

import java.util.ArrayList;

public class HomeConnectedActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private MainBankDetailAdapter surveysAdapter;
    private ListView surviesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            toontaMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActionMode(Utils.initActionModeCallBack(HomeConnectedActivity.this));
                    v.setSelected(true);
                }
            });

            // Share
            ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
            toontaShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.startShareActionIntent(HomeConnectedActivity.this);
                }
            });

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

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View popupWindowLayout = inflater.inflate(R.layout.full_window_popup, null, true);
                    final PopupWindow popupWindow = new PopupWindow(popupWindowLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(findViewById(R.id.list_surveys), Gravity.CENTER, 0, 0);

                    String popupWindowContentText = "No description available for this survey";
                    if (surveyElement.summary != null && !surveyElement.summary.trim().isEmpty() && !surveyElement.summary.equals("string")) {
                        popupWindowContentText = surveyElement.summary;
                    }

                    ((TextView) popupWindowLayout.findViewById(R.id.survey_description)).setText(popupWindowContentText);

                    AppCompatButton ok = (AppCompatButton) popupWindowLayout.findViewById(R.id.popup_ok_button);
                    ok.setTransformationMethod(null);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), ToontaQuestionActivity.class);
                            intent.putExtra(ToontaConstants.QUESTION_TITLE, surveyElement.name);
                            intent.putExtra(ToontaConstants.SURVEY_ID, surveyElement.surveyId);
                            intent.putExtra(ToontaConstants.SURVEY_REWRD, surveyElement.reward);
                            intent.putExtra(ToontaConstants.SURVEY_AUTHOR_ID, surveyElement.authorId);

                            startActivity(intent);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    popupWindow.dismiss();
                                }
                            }, 3000);
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

                    ImageView share = (ImageView) popupWindowLayout.findViewById(R.id.popup_window_toonta_share);
                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.startShareActionIntent(HomeConnectedActivity.this);
                        }
                    });

                    ImageView settings = (ImageView) popupWindowLayout.findViewById(R.id.popup_window_menu_settings);
                    settings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActionMode(Utils.initActionModeCallBack(HomeConnectedActivity.this));
                            v.setSelected(true);
                        }
                    });
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

                    surveysAdapter.addElements(surveyElementArrayList);
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
                    startActivity(intent);
                }
            });
        }
    }
}
