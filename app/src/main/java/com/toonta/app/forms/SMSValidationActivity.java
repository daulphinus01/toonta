package com.toonta.app.forms;

import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.model.Responses;
import com.toonta.app.utils.ToontaConstants;
import com.toonta.app.utils.ToontaUserInterceptor;
import com.toonta.app.utils.Utils;

public class SMSValidationActivity extends AppCompatActivity {

    private ToontaUserInterceptor toontaUserInterceptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsvalidation);

        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(SMSValidationActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(SMSValidationActivity.this);
            }
        });

        // Getting bundles
        final String titleQuestionScreen = getIntent().getStringExtra(ToontaConstants.QUESTION_TITLE);
        // TODO Quand le code renvoye par SMS est valide, on renvoie la/les reponse(s)
        Responses responsesToBeSent= getIntent().getParcelableExtra(ToontaConstants.SURVEY_RESPONSES_TO_BE_SENT);

        // Screen title
        TextView screenTitle = (TextView) findViewById(R.id.toonta_validate_sms_form_screen_title);
        assert screenTitle != null;
        screenTitle.setText(titleQuestionScreen);

        toontaUserInterceptor = new ToontaUserInterceptor(SMSValidationActivity.this, new ToontaUserInterceptor.ToontaUserViewUpdater() {
            @Override
            public void onToontaUserGet(ToontaUser toontaUser) {
                // User existant et donc on peut utiliser son ID
            }

            @Override
            public void onToontaUserUpdate(String error) {
                // Rien a mettre la dedans
            }

            @Override
            public void onFailure(String error) {
                if (error.equals("Authentication error")){
                    // User inconnu, on va creer un nouveau user

                } else {
                    // Probleme de connexion

                }
            }
        });

        // TODO Call toontaUserInterceptor.fetchToontaUserByPhoneNbr("User's phone nbr");
    }

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
