package com.toonta.app.activities.new_surveys;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;

import java.util.ArrayList;


public class NewSurveysActivity extends AppCompatActivity {

    private static int QUANTUM_LOAD = 10;

    private ListView newSurveysListView;
    private SurveysListAdapter surveysListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private NewSurveysInteractor newSurveysInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_suveys);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newSurveysListView = (ListView) findViewById(R.id.new_surveys_activity_list_view);
        surveysListAdapter = new SurveysListAdapter(getBaseContext());
        newSurveysListView.setAdapter(surveysListAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.new_surveys_activity_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newSurveysInteractor.submitRefreshList();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);

        newSurveysInteractor = new NewSurveysInteractor(getApplicationContext(), new NewSurveysInteractor.NewSurveysViewUpdater() {
            @Override
            public void onNewSurveys(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList, boolean reset) {
                if (reset) {
                    surveysListAdapter.resetWithList(surveyElementArrayList);
                } else {
                    surveysListAdapter.addElements(surveyElementArrayList);
                }
            }

            @Override
            public void onPopulateSurvies(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {

            }

            @Override
            public void onRefreshProgress() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onRefreshDone() {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String error) {
                newSurveysListView.setEmptyView(findViewById(R.id.new_surveys_activity_empty));
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });

        newSurveysListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
                if ((firstVisibleItem % QUANTUM_LOAD == 0) && (firstVisibleItem / QUANTUM_LOAD % 2 != 0)) {
                    newSurveysInteractor.submitLoadPage(firstVisibleItem / QUANTUM_LOAD % 2 + 1);
                }
            }
        });
        newSurveysInteractor.submitRefreshList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }
}
