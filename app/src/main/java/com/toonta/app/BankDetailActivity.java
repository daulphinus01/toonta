package com.toonta.app;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.toonta.app.activities.new_surveys.NewSurveysInteractor;
import com.toonta.app.utils.BankDetailAdapter;
import com.toonta.app.utils.SettingsClickListener;
import com.toonta.app.utils.Utils;

public class BankDetailActivity extends AppCompatActivity {

    // Total toons
    TextView rightLabel;

    private ListView surviesListView;
    // private ProgressDialog progressDialog;
    private BankDetailAdapter bankDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);

        // Actionbar
        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new SettingsClickListener(BankDetailActivity.this));

        // Total toons
        rightLabel = (TextView) findViewById(R.id.right_label);

        // Label "Solde"
        TextView leftLabel = (TextView) findViewById(R.id.left_label);

        surviesListView = (ListView) findViewById(R.id.bank_items);

        assert leftLabel != null;
        leftLabel.setText(R.string.bank_solde);
        leftLabel.setTransformationMethod(null);

        bankDetailAdapter = new BankDetailAdapter(getBaseContext());
        assert surviesListView != null;
        surviesListView.setAdapter(bankDetailAdapter);

        // Showing loading window
        NewSurveysInteractor newSurveysInteractor = new NewSurveysInteractor(BankDetailActivity.this, new NewSurveysInteractor.CompaniesUpdater() {
            @Override
            public void onSuccess(ToontaDAO.SurveysListAnswer surveyElementArrayList) {
                // Dismissing loading window
                /*if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/

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
                /*if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();*/

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
        NavUtils.navigateUpFromSameTask(BankDetailActivity.this);
    }
}
