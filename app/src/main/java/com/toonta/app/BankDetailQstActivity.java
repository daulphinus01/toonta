package com.toonta.app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.model.Bank;
import com.toonta.app.utils.BankDetailAdapter;
import com.toonta.app.utils.ToontaConstants;
import com.toonta.app.utils.ToontaQuestionActivity;
import com.toonta.app.utils.ToontaQuestionNoAnswerActivity;
import com.toonta.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BankDetailQstActivity extends AppCompatActivity {

    // Total toons
    TextView rightLabel;

    // Total toons
    TextView leftLabel;

    // Survey id
    private String surveyId;
    // Company name
    private String companyName;

    private ListView surviesListView;
    private ProgressDialog progressDialog;
    private BankDetailAdapter bankDetailAdapter;

    // ListeView's interceptor
    private NewSurveysInteractor newSurveysInteractor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail_qst);
        // SurveyId
        surveyId = getIntent().getStringExtra(ToontaConstants.SURVEY_ID);
        // Company name
        companyName = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);

        // Actionbar
        setupActionBar();

        surviesListView = (ListView) findViewById(R.id.bank_items_qst);

        // Company name
        rightLabel = (TextView) findViewById(R.id.right_label_qst);
        assert rightLabel != null;
        rightLabel.setText(companyName);
        rightLabel.setTransformationMethod(null);


        // Total toons
        final TextView leftLabel = (TextView) findViewById(R.id.left_label_qst);

        // Progress mechanisme when verifying credential
        progressDialog = new ProgressDialog(BankDetailQstActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.string_loading_survies_activity));

        bankDetailAdapter = new BankDetailAdapter(getBaseContext());
        assert surviesListView != null;
        surviesListView.setAdapter(bankDetailAdapter);

        // TODO Set correctely the listener
        surviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToontaDAO.SurveysListAnswer.SurveyElement surveyElement = bankDetailAdapter.getItem(position);
                Intent intent = new Intent(getBaseContext(), ToontaQuestionNoAnswerActivity.class);
                intent.putExtra(ToontaConstants.QUESTION_TITLE, surveyElement.name);
                intent.putExtra(ToontaConstants.SURVEY_ID, surveyElement.surveyId);

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
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                assert leftLabel != null;
                leftLabel.setText(Utils.computeBanksTotalToons(surveyElementArrayList));
                leftLabel.setTransformationMethod(null);

                if (surveyElementArrayList.size() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "No survies to show", Snackbar.LENGTH_LONG).show();
                } else {
                    bankDetailAdapter.addElements(surveyElementArrayList);
                }
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
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }
}
