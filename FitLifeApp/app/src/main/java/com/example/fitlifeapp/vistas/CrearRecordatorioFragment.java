package com.example.fitlifeapp.vistas;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.notificacion.ReminderScheduler;
import com.example.fitlifeapp.model.Recordatorio;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

/**
 * Fragment encargado de la creación de nuevos recordatorios.
 * Permite al usuario introducir los datos básicos y guarda
 * el recordatorio en Firestore, además de programar la notificación.
 */
public class CrearRecordatorioFragment extends Fragment {

    private static final String TAG = "CrearRecordatorio";

    // Vistas
    private TextView tvHora, tvFrecuencia;
    private LinearLayout layoutHora, layoutFrecuencia;
    private TextView etTitulo, etCategoria;
    private MaterialButton btnGuardar;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflamos el layout del fragment
        View view = inflater.inflate(
                R.layout.fragment_crear_recordatorio, container, false);

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Campos de texto
        etTitulo = view.findViewById(R.id.etTitulo);
        etCategoria = view.findViewById(R.id.etCategoria);

        // Selección de hora
        tvHora = view.findViewById(R.id.tvHora);
        layoutHora = view.findViewById(R.id.layoutHora);

        // Selección de frecuencia
        tvFrecuencia = view.findViewById(R.id.tvFrecuencia);
        layoutFrecuencia = view.findViewById(R.id.layoutFrecuencia);

        // Botón guardar
        btnGuardar = view.findViewById(R.id.btnGuardar);

        // Configuración de listeners
        configurarHora();
        configurarFrecuencia();
        configurarGuardar();

        return view;
    }

    /**
     * Configura el selector de hora usando un TimePickerDialog.
     * La hora seleccionada se muestra en formato HH:mm.
     */
    private void configurarHora() {
        layoutHora.setOnClickListener(v -> {

            TimePickerDialog dialog = new TimePickerDialog(
                    getContext(),
                    (timePicker, hour, minute) -> {

                        String horaFormateada =
                                String.format("%02d:%02d", hour, minute);
                        tvHora.setText(horaFormateada);

                        Log.d(TAG, "Hora seleccionada: " + horaFormateada);
                    },
                    12,
                    0,
                    true
            );

            dialog.show();
        });
    }

    /**
     * Muestra un diálogo con las distintas frecuencias posibles
     * para el recordatorio.
     */
    private void configurarFrecuencia() {

        String[] opciones = {
                "Diario",
                "Cada 2 horas",
                "Cada 4 horas",
                "Semanal",
                "Mensual"
        };

        layoutFrecuencia.setOnClickListener(v -> {

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Seleccionar frecuencia")
                    .setItems(opciones, (dialog, which) -> {
                        tvFrecuencia.setText(opciones[which]);
                        Log.d(TAG, "Frecuencia seleccionada: " + opciones[which]);
                    })
                    .show();
        });
    }

    /**
     * Valida los datos introducidos por el usuario,
     * guarda el recordatorio en Firestore y programa
     * la notificación correspondiente.
     */
    private void configurarGuardar() {

        btnGuardar.setOnClickListener(v -> {

            String titulo = etTitulo.getText().toString().trim();
            String horaTexto = tvHora.getText().toString().trim();
            String frecuencia = tvFrecuencia.getText().toString().trim();
            String categoria = etCategoria.getText().toString().trim();

            // Validación básica de campos obligatorios
            if (titulo.isEmpty() || horaTexto.isEmpty()) {
                Toast.makeText(
                        getContext(),
                        "Completa todos los campos",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            String uid = mAuth.getUid();
            if (uid == null) return;

            // ID del documento en Firestore
            String id = db.collection("recordatorios").document().getId();
            String fechaCreado = LocalDate.now().toString();

            // Creación del objeto Recordatorio
            Recordatorio r = new Recordatorio(
                    id,
                    uid,
                    titulo,
                    horaTexto,
                    frecuencia,
                    categoria,
                    fechaCreado,
                    true
            );

            Log.d(TAG, "Guardando recordatorio en Firestore");

            db.collection("recordatorios")
                    .document(id)
                    .set(r)
                    .addOnSuccessListener(unused -> {

                        // Programación de la notificación asociada
                        ReminderScheduler.programarRecordatorio(
                                requireContext(),
                                r
                        );

                        // Volver a la lista de recordatorios
                        Navigation.findNavController(v)
                                .navigate(
                                        R.id.action_crearRecordatorioFragment_to_nav_reminders
                                );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    getContext(),
                                    "Error al guardar: " + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show()
                    );
        });
    }
}
