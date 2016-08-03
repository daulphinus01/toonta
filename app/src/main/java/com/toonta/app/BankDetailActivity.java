package com.toonta.app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.toonta.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BankDetailActivity extends AppCompatActivity {

    // Total toons
    TextView rightLabel;

    private ListView surviesListView;
    private ProgressDialog progressDialog;
    private BankDetailAdapter bankDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);

        // Actionbar
        setupActionBar();

        // Button up
        ImageView upButton = (ImageView) findViewById(R.id.toonta_bank_detail_up_button);
        assert upButton != null;
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(BankDetailActivity.this);
            }
        });


        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(BankDetailActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(BankDetailActivity.this);
            }
        });

        // Total toons
        rightLabel = (TextView) findViewById(R.id.right_label);

        // Label "Solde"
        TextView leftLabel = (TextView) findViewById(R.id.left_label);

        // Progress mechanisme when verifying credential
        progressDialog = new ProgressDialog(BankDetailActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.string_loading_survies_activity));

        surviesListView = (ListView) findViewById(R.id.bank_items);

        assert leftLabel != null;
        leftLabel.setText(R.string.bank_solde);
        leftLabel.setTransformationMethod(null);

        bankDetailAdapter = new BankDetailAdapter(getBaseContext());
        assert surviesListView != null;
        surviesListView.setAdapter(bankDetailAdapter);

        // TODO Set correctely the listener
        surviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), BankDetailQstActivity.class);

                ToontaDAO.SurveysListAnswer.SurveyElement surveyElement = bankDetailAdapter.getItem(position);
                intent.putExtra(ToontaConstants.QUESTION_TITLE, surveyElement.name);
                intent.putExtra(ToontaConstants.SURVEY_ID, surveyElement.surveyId);
                intent.putExtra(ToontaConstants.SURVEY_REWRD, surveyElement.reward);
                intent.putExtra(ToontaConstants.SURVEY_AUTHOR_ID, surveyElement.authorId);

                startActivity(intent);
            }
        });

        // Showing loading window
        progressDialog.show();
        NewSurveysInteractor newSurveysInteractor = new NewSurveysInteractor(BankDetailActivity.this, new NewSurveysInteractor.CompaniesUpdater() {
            @Override
            public void onSuccess(ToontaDAO.SurveysListAnswer surveyElementArrayList) {
                // Dismissing loading window
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                assert rightLabel != null;
                rightLabel.setText(Utils.computeBanksTotalToons(surveyElementArrayList.surveyElements));
                rightLabel.setTransformationMethod(null);

                if (surveyElementArrayList.surveyElements.size() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "No companies to show", Snackbar.LENGTH_LONG).show();
                } else {
                    bankDetailAdapter.addElements(surveyElementArrayList.surveyElements);
                }
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
        newSurveysInteractor.getCompanies();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar mActionBar = getSupportActionBar();
            assert mActionBar != null;
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }
}
