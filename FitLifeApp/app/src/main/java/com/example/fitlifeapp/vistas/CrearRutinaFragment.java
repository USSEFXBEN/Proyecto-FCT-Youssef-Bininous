package com.example.fitlifeapp.vistas;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.model.Recordatorio;
import com.example.fitlifeapp.model.Rutina;
import com.example.fitlifeapp.notificacion.ReminderScheduler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CrearRutinaFragment extends Fragment {

    private static final String TAG = "FITLIFE_TRACE";

    private EditText etNombre, etDescripcion;
    private TextView tvHoraSeleccionada;
    private Button btnSeleccionarHora, btnCancelar, btnGuardar;

    private CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves, cbViernes, cbSabado, cbDomingo;

    private FirebaseFirestore db;
    private String userId;

    private String horaSeleccionada = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "══════════════════════════════════════");
        Log.d(TAG, "🟢 onCreateView INICIO");

        View view = inflater.inflate(R.layout.fragment_crear_rutina, container, false);

        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "📦 FirebaseFirestore inicializado");

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e(TAG, "❌ Usuario no logueado");
            Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return view;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "👤 userId = " + userId);

        etNombre = view.findViewById(R.id.etNombreRutina);
        etDescripcion = view.findViewById(R.id.etDescripcionRutina);
        tvHoraSeleccionada = view.findViewById(R.id.tvHoraSeleccionada);
        btnSeleccionarHora = view.findViewById(R.id.btnSeleccionarHora);
        btnCancelar = view.findViewById(R.id.btnCancelarCrear);
        btnGuardar = view.findViewById(R.id.btnGuardarRutina);

        cbLunes = view.findViewById(R.id.cbLunes);
        cbMartes = view.findViewById(R.id.cbMartes);
        cbMiercoles = view.findViewById(R.id.cbMiercoles);
        cbJueves = view.findViewById(R.id.cbJueves);
        cbViernes = view.findViewById(R.id.cbViernes);
        cbSabado = view.findViewById(R.id.cbSabado);
        cbDomingo = view.findViewById(R.id.cbDomingo);

        Log.d(TAG, "🧩 Views enlazadas");

        btnSeleccionarHora.setOnClickListener(v -> {
            Log.d(TAG, "🕒 Click en seleccionar hora");
            mostrarTimePicker();
        });

        btnGuardar.setOnClickListener(v -> {
            Log.d(TAG, "💾 CLICK BOTÓN GUARDAR");
            guardarRutina();
        });

        btnCancelar.setOnClickListener(v -> {
            Log.d(TAG, "↩️ Click cancelar");
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .popBackStack();
        });

        Log.d(TAG, "🟢 onCreateView FIN");
        Log.d(TAG, "══════════════════════════════════════");

        return view;
    }

    private void mostrarTimePicker() {
        Log.d(TAG, "🕒 mostrarTimePicker()");

        Calendar c = Calendar.getInstance();

        new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                    tvHoraSeleccionada.setText(horaSeleccionada);
                    Log.d(TAG, "⏰ Hora seleccionada = " + horaSeleccionada);
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void guardarRutina() {

        Log.d(TAG, "══════════════════════════════════════");
        Log.d(TAG, "💾 guardarRutina() INICIO");

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        Log.d(TAG, "📌 nombre = " + nombre);
        Log.d(TAG, "📌 descripcion = " + descripcion);
        Log.d(TAG, "📌 horaSeleccionada = " + horaSeleccionada);

        if (TextUtils.isEmpty(nombre)) {
            Log.e(TAG, "❌ Nombre vacío");
            Toast.makeText(getContext(), "Introduce un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Boolean> diasActivos = new HashMap<>();
        diasActivos.put("lunes", cbLunes.isChecked());
        diasActivos.put("martes", cbMartes.isChecked());
        diasActivos.put("miercoles", cbMiercoles.isChecked());
        diasActivos.put("jueves", cbJueves.isChecked());
        diasActivos.put("viernes", cbViernes.isChecked());
        diasActivos.put("sabado", cbSabado.isChecked());
        diasActivos.put("domingo", cbDomingo.isChecked());


        Log.d(TAG, "📅 Días activos = " + diasActivos);

        String rutinaId = db.collection("routines").document().getId();
        Log.d(TAG, "🆔 rutinaId = " + rutinaId);

        Rutina rutina = new Rutina(
                rutinaId,
                userId,
                nombre,
                descripcion,
                horaSeleccionada,
                diasActivos
        );

        Log.d(TAG, "📤 Guardando rutina en Firestore");

        db.collection("routines")
                .document(rutinaId)
                .set(rutina)
                .addOnSuccessListener(unused -> {

                    Log.d(TAG, "✅ Rutina guardada");

                    if (!TextUtils.isEmpty(horaSeleccionada)) {

                        Log.d(TAG, "🔔 Creando recordatorio");

                        String recordatorioId =
                                db.collection("recordatorios").document().getId();

                        Log.d(TAG, "🆔 recordatorioId = " + recordatorioId);

                        Recordatorio recordatorio = new Recordatorio(
                                recordatorioId,
                                userId,
                                "Rutina: " + nombre,
                                horaSeleccionada,
                                "Diario",
                                "Rutina",
                                LocalDate.now().toString(),
                                true
                        );

                        Log.d(TAG, "📤 Guardando recordatorio en Firestore");

                        db.collection("recordatorios")
                                .document(recordatorioId)
                                .set(recordatorio)
                                .addOnSuccessListener(r -> {

                                    Log.d(TAG, "✅ Recordatorio guardado");
                                    Log.d(TAG, "🚀 Programando alarma");

                                    if (getContext() != null) {
                                        ReminderScheduler.programarRecordatorio(
                                                getContext().getApplicationContext(),
                                                recordatorio
                                        );
                                        Log.d(TAG, "⏰ Alarma programada correctamente");
                                    } else {
                                        Log.e(TAG, "❌ Contexto nulo, alarma NO programada");
                                    }

                                    Log.d(TAG, "↩️ Navegando atrás");
                                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                            .popBackStack();
                                })
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "❌ Error guardando recordatorio", e)
                                );
                    } else {
                        Log.d(TAG, "ℹ️ Rutina sin hora, no se programa notificación");
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                .popBackStack();
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "❌ Error guardando rutina", e)
                );
    }
}
