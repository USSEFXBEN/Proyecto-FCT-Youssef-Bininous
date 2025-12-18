package com.example.fitlifeapp.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fitlifeapp.model.Recordatorio;

import java.util.Calendar;

public class ReminderScheduler {

    public static void programarRecordatorio(Context context, Recordatorio r) {

        Log.d("FITLIFE_SCHEDULER", "📆 Programando recordatorio: " + r.getHora());

        String[] partes = r.getHora().split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);

        // Si la hora ya pasó, programamos para mañana
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("titulo", r.getTitulo());
        intent.putExtra("texto", "⏰ Es hora de tu recordatorio");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                r.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // ⚠️ IMPORTANTE: USAMOS set(), NO setExact()
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Log.d("FITLIFE_SCHEDULER", "✅ Alarma registrada correctamente");
    }

    public static void cancelarRecordatorio(Context context, Recordatorio r) {

        Intent intent = new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                r.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

        Log.d("FITLIFE_SCHEDULER", "❌ Recordatorio cancelado");
    }
}
