package com.toonta.app.notifs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.toonta.app.R;
import com.toonta.app.ToontaSharedPreferences;

/**
 * Cette classe écoute le rédemarrage du téléphone. Si les notifications étaient activées
 * pour l'appli toonta, cette classe les réactivera.
 *
 * Created by Marcellin RWEGO on 31/03/2017.
 */

public class ToontaBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                // Récupération de l'état des notification dans les préférences
                ToontaSharedPreferences.init(context);
                boolean isNotifEnabled = ToontaSharedPreferences.isLoggedIn()
                        && !ToontaSharedPreferences.getNotificationsState();

                // Si utilisateur connecté et notifications activées, il faut relancer l'alarme
                if (isNotifEnabled) {
                    Intent alarmIntent = new Intent(context, ToontaAlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                            context.getResources().getInteger(R.integer.ALARM_INTERVAL), pendingIntent);
                }
                break;
        }
    }
}
