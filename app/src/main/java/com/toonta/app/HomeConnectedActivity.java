package com.toonta.app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
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
import android.widget.ListView;

import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.utils.ProfileActivity;
import com.toonta.app.utils.SurveysAdapter;
import com.toonta.app.utils.ToontaConstants;
import com.toonta.app.utils.ToontaQuestionActivity;

import java.util.ArrayList;

public class HomeConnectedActivity extends AppCompatActivity {

    private NewSurveysInteractor newSurveysInteractor;
    private ProgressDialog progressDialog;
    private SurveysAdapter surveysAdapter;
    private ListView surviesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_connected);

        // Actionbar
        setupActionBar();

        Button bankButton = (Button) findViewById(R.id.bank_button);
        toontaSetOnClickListener(bankButton, BankDetailActivity.class);

        Button profileButton = (Button) findViewById(R.id.profile_button);
        toontaSetOnClickListener(profileButton, ProfileActivity.class);

        // Progress mechanisme when verifying credential
        progressDialog = new ProgressDialog(HomeConnectedActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.string_loading_survies_activity));

        surviesListView = (ListView) findViewById(R.id.list_surveys);

        surveysAdapter = new SurveysAdapter(getBaseContext());
        assert surviesListView != null;
        surviesListView.setAdapter(surveysAdapter);

        surviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ToontaQuestionActivity.class);
                // View titleView = ((RelativeLayout) view).getChildAt(0);
                // String titleText = ((TextView)titleView).getText().toString();
                ToontaDAO.SurveysListAnswer.SurveyElement surveyElement = surveysAdapter.getItem(position);
                intent.putExtra(ToontaConstants.QUESTION_TITLE, surveyElement.name);
                intent.putExtra(ToontaConstants.SURVEY_ID, surveyElement.surveyId);
                intent.putExtra(ToontaConstants.SURVEY_REWRD, surveyElement.reward);

                startActivity(intent);
            }
        });

        // Showing loading window
        progressDialog.show();
        newSurveysInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.NewSurveysViewUpdater() {
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
                // newSurveysListView.setEmptyView(findViewById(R.id.new_surveys_activity_empty));
                if (surviesListView.getChildCount() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // Fetching survies
        newSurveysInteractor.fetchAllSurvies();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            /*ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }*/




            ActionBar mActionBar = getSupportActionBar();
            assert mActionBar != null;
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
            /*TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
            mTitleTextView.setText("My Own Title");*/

            /*ImageButton imageButton = (ImageButton) mCustomView
                    .findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Refresh Clicked!",
                            Toast.LENGTH_LONG).show();
                }
            });*/

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
