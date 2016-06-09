package com.toonta.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toonta.app.model.Survey;
import com.toonta.app.utils.ProfileActivit;
import com.toonta.app.utils.SurveysAdapter;
import com.toonta.app.utils.ToontaConstants;
import com.toonta.app.utils.ToontaQuestionActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeConnectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_connected);

        // Actionbar
        setupActionBar();

        Button bankButton = (Button) findViewById(R.id.bank_button);
        toontaSetOnClickListener(bankButton, BankDetailActivity.class);

        Button profileButton = (Button) findViewById(R.id.profile_button);
        toontaSetOnClickListener(profileButton, ProfileActivit.class);

        ListView listView = (ListView) findViewById(R.id.list_surveys);

        List<Survey> surveys = generateSurveys();

        SurveysAdapter surveysAdapter = new SurveysAdapter(getBaseContext(), surveys);
        assert listView != null;
        listView.setAdapter(surveysAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ToontaQuestionActivity.class);
                View titleView = ((RelativeLayout) view).getChildAt(0);
                String titleText = ((TextView)titleView).getText().toString();
                intent.putExtra(ToontaConstants.QUESTION_TITLE, titleText);
                startActivity(intent);
            }
        });
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
