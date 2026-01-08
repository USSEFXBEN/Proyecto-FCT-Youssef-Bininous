package com.example.fitlifeapp.notificacion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String titulo = intent.getStringExtra("titulo");
        String texto = intent.getStringExtra("texto");

        Log.d("FITLIFE_REMINDER", "⏰ Receiver activado");

        Toast.makeText(context, "⏰ " + titulo, Toast.LENGTH_LONG).show();

        NotificationUtils.mostrarNotificacion(
                context,
                titulo,
                texto
        );
    }
}
