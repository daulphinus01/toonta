package com.toonta.app.notifs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.widget.Toast;

/**
 * Gestionnaire de l'activation / désactivation des notifs
 *
 * Created by Marcellin RWEGO on 31/03/2017.
 */

public class ToontaAlarmManager {

    PendingIntent pendingIntent;

    public static void enableNotifications(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;

        //manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(context, "Toonta Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public static void desableNotifications(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //manager.cancel(pendingIntent);
        Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }
}
