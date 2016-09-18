package com.toonta.app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
        // Company name
        companyName = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);
        // Author id
        final String authorId = getIntent().getStringExtra(ToontaConstants.SURVEY_AUTHOR_ID);

        // Actionbar
        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(BankDetailQstActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(BankDetailQstActivity.this);
            }
        });

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

                if (surveyElementArrayList.size() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.toonta_No_surveys_to_show, Snackbar.LENGTH_LONG).show();
                } else {
                    ToontaDAO.SurveysListAnswer tmp = new ToontaDAO.SurveysListAnswer();
                    tmp.surveyElements = surveyElementArrayList;
                    ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementsByAuthor =
                            getSurviesByAuthorId(authorId, Utils.getAnsweredSurvies(tmp));
                    if (surveyElementsByAuthor.size() < 1) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.toonta_No_surveys_to_show, Snackbar.LENGTH_LONG).show();
                    } else {
                        assert leftLabel != null;
                        leftLabel.setText(Utils.computeBanksTotalToons(surveyElementsByAuthor));
                        leftLabel.setTransformationMethod(null);
                        bankDetailAdapter.addElements(surveyElementsByAuthor);
                    }
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
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);

            View mCustomView = getLayoutInflater().inflate(R.layout.custom_actionbar_with_up_button, null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            mActionBar.setCustomView(mCustomView, layoutParams);
            Toolbar parent = (Toolbar) mCustomView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    public void goUp(View view) {
        NavUtils.navigateUpFromSameTask(BankDetailQstActivity.this);
    }

    private ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> getSurviesByAuthorId(String authorId, ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
        ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElements = new ArrayList<>();
        for (ToontaDAO.SurveysListAnswer.SurveyElement surveyElement : surveyElementArrayList) {
            if (surveyElement.authorId.equals(authorId)) {
                surveyElements.add(surveyElement);
            }
        }
        return surveyElements;
    }

}
