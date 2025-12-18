package com.example.fitlifeapp.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.fitlifeapp.R;

public class NotificationHelper {

    public static final String CHANNEL_ID = "fitlife_reminders";

    public static void mostrarNotificacion(Context context, String titulo, String texto) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Canal (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios FitLife",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Recordatorios de rutinas y hábitos");
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
