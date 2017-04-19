package com.toonta.app.notifs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Selon l'intervalle donné, cette classe doit envoyer une requête au serveur pour
 * vérifier s'il y a de nouveaux questionnaires.
 *
 * Created by Marcellin RWEGO on 31/03/2017.
 */

public class ToontaAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Les requêtes vers le server seront ici
        Toast.makeText(context, "Toonta is running correctly", Toast.LENGTH_SHORT).show();
    }
}
