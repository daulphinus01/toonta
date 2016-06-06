package com.toonta.app;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.toonta.app.com.toonta.app.model.Survey;
import com.toonta.app.utils.SurveysAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeConnectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_connected);

        // Actionbar
        setupActionBar();

        ListView listView = (ListView) findViewById(R.id.list_surveys);

        List<Survey> surveys = generateSurveys();

        SurveysAdapter surveysAdapter = new SurveysAdapter(getBaseContext(), surveys);
        assert listView != null;
        listView.setAdapter(surveysAdapter);
    }

    @Override
    protected void onStop() {
        this.finish();
        super.onStop();
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

    private List<Survey> generateSurveys() {
        List<Survey> surveys = new ArrayList<>();
        surveys.add(new Survey("Length of television programs", ""));
        surveys.add(new Survey("Telephone Credit & Internet Data", ""));
        surveys.add(new Survey("Presidential elections for 2017", ""));
        surveys.add(new Survey("Length of television programs", ""));
        surveys.add(new Survey("Telephone Credit & Internet Data", ""));
        surveys.add(new Survey("Presidential elections for 2017", ""));
        surveys.add(new Survey("Length of television programs", ""));
        surveys.add(new Survey("Telephone Credit & Internet Data", ""));
        surveys.add(new Survey("Presidential elections for 2017", ""));
        return surveys;
    }

}
