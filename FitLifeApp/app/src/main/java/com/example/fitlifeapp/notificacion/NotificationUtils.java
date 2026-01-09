package com.example.fitlifeapp.notificacion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.fitlifeapp.R;

/**
 * Clase utilitaria encargada de mostrar notificaciones
 * dentro de la aplicación FitLife.
 * Centraliza la creación del canal y el envío de la notificación.
 */
public class NotificationUtils {

    private static final String CHANNEL_ID = "fitlife_channel";
    private static final String TAG = "NotificationUtils";

    /**
     * Muestra una notificación con el título y texto indicados.
     * En Android 8.0 o superior se crea previamente el canal
     * de notificaciones si no existe.
     */
    public static void mostrarNotificacion(
            Context context,
            String titulo,
            String texto) {

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear canal de notificaciones en Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Recordatorios FitLife",
                            NotificationManager.IMPORTANCE_HIGH
                    );
            manager.createNotificationChannel(channel);
        }

        // Construcción de la notificación
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(titulo)
                        .setContentText(texto)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        // Mostrar la notificación con un ID único
        manager.notify(
                (int) System.currentTimeMillis(),
                builder.build()
        );

        Log.d(TAG, "Notificación mostrada correctamente");
    }
}
