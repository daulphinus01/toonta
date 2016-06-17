package com.toonta.app.activities.dashboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.toonta.app.R;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.activities.login.LoginActivity;
import com.toonta.app.activities.new_surveys.NewSurveysActivity;


public class DashboardActivity extends AppCompatActivity {

    private Button newSurveysButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        newSurveysButton = (Button) findViewById(R.id.dashboard_activity_button_new_surveys);
        newSurveysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewSurveysActivity.class));
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        logoutButton = (Button) findViewById(R.id.login_activity_button_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToontaSharedPreferences.logOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_action_bar, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.string_dashboard_activity_share_sentence));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.string_dashboard_activity_share_dialog_title)));
                return false;
            }
        });
        return true;
    }

}
