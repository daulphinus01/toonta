package com.toonta.app.utils;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 *
 * Created by Marcellin RWEGO on 25/03/2017.
 * @version 1.0.10
 * @see android.content.BroadcastReceiver
 */

public class BootReceiver extends BroadcastReceiver {

    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "android.intent.action.BOOT_COMPLETED":
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    static void enableNotifications(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    static void disableNotifications(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
