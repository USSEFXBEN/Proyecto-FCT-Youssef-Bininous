package com.example.fitlifeapp.notificacion;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fitlifeapp.model.Recordatorio;

import java.util.Calendar;

/**
 * Clase encargada de programar y cancelar recordatorios
 * utilizando AlarmManager.
 */
public class ReminderScheduler {

    private static final String TAG = "ReminderScheduler";

    /**
     * Programa un recordatorio según la hora y frecuencia indicadas.
     */
    public static void programarRecordatorio(
            Context context,
            Recordatorio r) {

        Log.d(
                TAG,
                "Programando recordatorio: "
                        + r.getHora()
                        + " | Frecuencia: "
                        + r.getFrecuencia()
        );

        // Parsear la hora (formato HH:mm)
        String[] partes = r.getHora().split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);

        // Si la hora ya ha pasado hoy, se programa para el día siguiente
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Intent que recibirá el BroadcastReceiver
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("titulo", r.getTitulo());
        intent.putExtra("texto", "Es hora de tu recordatorio");

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        r.getId().hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        AlarmManager alarmManager =
                (AlarmManager)
                        context.getSystemService(Context.ALARM_SERVICE);

        // Calcular intervalo según la frecuencia seleccionada
        long intervalo =
                obtenerIntervaloFrecuencia(r.getFrecuencia());

        if (intervalo > 0) {
            // Recordatorios repetitivos
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    intervalo,
                    pendingIntent
            );

            Log.d(
                    TAG,
                    "Recordatorio repetitivo programado. Intervalo: "
                            + intervalo
            );

        } else {
            // Recordatorio único
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

            Log.d(TAG, "Recordatorio único programado");
        }

        Log.d(TAG, "Alarma registrada correctamente");
    }

    /**
     * Devuelve el intervalo en milisegundos según la frecuencia seleccionada.
     * Si devuelve 0, se trata como una alarma única.
     */
    private static long obtenerIntervaloFrecuencia(
            String frecuencia) {

        if (frecuencia == null) return 0;

        switch (frecuencia) {

            case "Diario":
                return AlarmManager.INTERVAL_DAY;

            case "Cada 2 horas":
                return AlarmManager.INTERVAL_HOUR * 2;

            case "Cada 4 horas":
                return AlarmManager.INTERVAL_HOUR * 4;

            case "Semanal":
                return AlarmManager.INTERVAL_DAY * 7;

            case "Mensual":
            default:
                // Simplificación aceptada para FCT
                return 0;
        }
    }

    /**
     * Cancela un recordatorio previamente programado.
     */
    public static void cancelarRecordatorio(
            Context context,
            Recordatorio r) {

        Intent intent =
                new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        r.getId().hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        AlarmManager alarmManager =
                (AlarmManager)
                        context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Recordatorio cancelado");
        }
    }
}
