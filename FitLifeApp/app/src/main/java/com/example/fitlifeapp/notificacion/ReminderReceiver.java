package com.example.fitlifeapp.notificacion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * BroadcastReceiver encargado de recibir las alarmas
 * programadas para los recordatorios.
 * Cuando se activa, muestra una notificación al usuario.
 */
public class ReminderReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Obtener datos enviados desde el PendingIntent
        String titulo = intent.getStringExtra("titulo");
        String texto = intent.getStringExtra("texto");

        Log.d(TAG, "Receiver activado");

        // Mensaje informativo opcional
        Toast.makeText(
                context,
                titulo,
                Toast.LENGTH_LONG
        ).show();

        // Mostrar notificación
        NotificationUtils.mostrarNotificacion(
                context,
                titulo,
                texto
        );
    }
}
