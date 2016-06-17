package com.toonta.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.toonta.app.ToontaDAO;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.activities.dashboard.DashboardActivity;
import com.toonta.app.activities.login.LoginActivity;

import io.fabric.sdk.android.Fabric;

public class LoadingActivity extends AppCompatActivity {

    public static String NEED_FIRST_USE_SLIDER = "NEED_FIRST_USE_SLIDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        ToontaSharedPreferences.init(getApplicationContext());
        ToontaDAO.init(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ToontaSharedPreferences.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            if (!ToontaSharedPreferences.isFistUseSliderValidated()) {
                intent.putExtra(NEED_FIRST_USE_SLIDER, true);
            }
            startActivity(intent);
        } else {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        }
        finish();
    }
}
