package com.example.fitlifeapp.vistas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitlifeapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProgresoSemanalFragment extends Fragment {

    private BarChart barChart;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_progreso_semanal, container, false);

        barChart = view.findViewById(R.id.barChartWeekly);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        configurarGrafico();
        cargarDatos();

        return view;
    }

    // ---------------- CONFIGURACIÓN VISUAL ----------------

    private void configurarGrafico() {
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.setDrawGridBackground(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setFitBars(true);

        // EJE X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int) value) {
                    case 0: return "Mon";
                    case 1: return "Tue";
                    case 2: return "Wed";
                    case 3: return "Thu";
                    case 4: return "Fri";
                    case 5: return "Sat";
                    case 6: return "Sun";
                    default: return "";
                }
            }
        });

        // EJE Y IZQUIERDO
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(true);

        // EJE Y DERECHO DESACTIVADO
        barChart.getAxisRight().setEnabled(false);
    }

    // ---------------- CARGA DE DATOS ----------------

    private void cargarDatos() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        Map<Integer, Integer> conteo = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            conteo.put(i, 0);
        }

        db.collection("progress")
                .whereEqualTo("userId", uid)
                .whereEqualTo("estado", "completado")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String fecha = doc.getString("fecha");
                        Integer dia = obtenerIndiceDia(fecha);
                        if (dia != null) {
                            conteo.put(dia, conteo.get(dia) + 1);
                        }
                    }
                    pintarGrafico(conteo);
                });
    }

    private Integer obtenerIndiceDia(String fecha) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(fecha));
            int day = cal.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.MONDAY: return 0;
                case Calendar.TUESDAY: return 1;
                case Calendar.WEDNESDAY: return 2;
                case Calendar.THURSDAY: return 3;
                case Calendar.FRIDAY: return 4;
                case Calendar.SATURDAY: return 5;
                case Calendar.SUNDAY: return 6;
            }
        } catch (Exception ignored) {}
        return null;
    }

    // ---------------- PINTADO ----------------

    private void pintarGrafico(Map<Integer, Integer> conteo) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, conteo.get(i)));
        }

        BarDataSet dataSet =
                new BarDataSet(entries, "Rutinas completadas");

        dataSet.setColor(Color.parseColor("#4CD97B"));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.DKGRAY);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        barChart.setData(data);
        barChart.invalidate();
        barChart.animateY(800);
    }
}
