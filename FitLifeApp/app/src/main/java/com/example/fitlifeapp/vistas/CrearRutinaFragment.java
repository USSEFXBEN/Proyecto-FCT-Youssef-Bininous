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

/**
 * Fragment encargado de crear una nueva rutina.
 * Permite introducir nombre, descripción, hora y días activos.
 * Si se selecciona una hora, también se crea un recordatorio asociado.
 */
public class CrearRutinaFragment extends Fragment {

    private static final String TAG = "CrearRutinaFragment";

    // Campos de texto
    private EditText etNombre, etDescripcion;
    private TextView tvHoraSeleccionada;

    // Botones
    private Button btnSeleccionarHora, btnCancelar, btnGuardar;

    // CheckBox de días
    private CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves,
            cbViernes, cbSabado, cbDomingo;

    // Firebase
    private FirebaseFirestore db;
    private String userId;

    // Hora seleccionada en formato HH:mm
    private String horaSeleccionada = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crear_rutina, container, false);

        db = FirebaseFirestore.getInstance();

        // Comprobamos que el usuario esté logueado
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return view;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Enlace de vistas
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

        // Listener para seleccionar hora
        btnSeleccionarHora.setOnClickListener(v -> mostrarTimePicker());

        // Listener para guardar rutina
        btnGuardar.setOnClickListener(v -> guardarRutina());

        // Listener para cancelar y volver atrás
        btnCancelar.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .popBackStack()
        );

        return view;
    }

    /**
     * Muestra un TimePickerDialog para seleccionar la hora
     * del recordatorio de la rutina.
     */
    private void mostrarTimePicker() {

        Calendar c = Calendar.getInstance();

        new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                    tvHoraSeleccionada.setText(horaSeleccionada);
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        ).show();
    }

    /**
     * Valida los datos introducidos y guarda la rutina en Firestore.
     * Si se ha seleccionado una hora, también se crea un recordatorio.
     */
    private void guardarRutina() {

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        // Validación básica
        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(getContext(), "Introduce un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mapa de días activos
        Map<String, Boolean> diasActivos = new HashMap<>();
        diasActivos.put("lunes", cbLunes.isChecked());
        diasActivos.put("martes", cbMartes.isChecked());
        diasActivos.put("miercoles", cbMiercoles.isChecked());
        diasActivos.put("jueves", cbJueves.isChecked());
        diasActivos.put("viernes", cbViernes.isChecked());
        diasActivos.put("sabado", cbSabado.isChecked());
        diasActivos.put("domingo", cbDomingo.isChecked());

        // ID de la rutina
        String rutinaId = db.collection("routines").document().getId();

        Rutina rutina = new Rutina(
                rutinaId,
                userId,
                nombre,
                descripcion,
                horaSeleccionada,
                diasActivos
        );

        db.collection("routines")
                .document(rutinaId)
                .set(rutina)
                .addOnSuccessListener(unused -> {

                    // Si hay hora, se crea también un recordatorio
                    if (!TextUtils.isEmpty(horaSeleccionada)) {

                        String recordatorioId =
                                db.collection("recordatorios").document().getId();

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

                        db.collection("recordatorios")
                                .document(recordatorioId)
                                .set(recordatorio)
                                .addOnSuccessListener(r -> {

                                    if (getContext() != null) {
                                        ReminderScheduler.programarRecordatorio(
                                                getContext().getApplicationContext(),
                                                recordatorio
                                        );
                                    }

                                    Navigation.findNavController(
                                            requireActivity(),
                                            R.id.nav_host_fragment
                                    ).popBackStack();
                                })
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error guardando recordatorio", e)
                                );
                    } else {
                        // Rutina sin hora, solo se guarda la rutina
                        Navigation.findNavController(
                                requireActivity(),
                                R.id.nav_host_fragment
                        ).popBackStack();
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error guardando rutina", e)
                );
    }
}
