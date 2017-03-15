package com.toonta.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;

import com.toonta.app.HomeConnectedActivity;
import com.toonta.app.HomePageActivity;
import com.toonta.app.R;
import com.toonta.app.ToontaSharedPreferences;

/**
 * Created by Marcellin RWEGO on 14/03/2017.
 */

public class SettingsClickListener implements View.OnClickListener {
    private Context context;

    public SettingsClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        v.setSelected(true);

        MenuBuilder builder = new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.toonta_menu, builder);
        MenuPopupHelper menuHelper = new MenuPopupHelper(context, builder, v);
        menuHelper.setForceShowIcon(true);
        builder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.toonta_logout_menu:
                        ToontaSharedPreferences.logOut();
                        Intent intent = new Intent(context, HomePageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        return true;
                    case R.id.toonta_share_menu:
                        Utils.startShareActionIntent(context);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {}
        });

        menuHelper.show();
    }
}
