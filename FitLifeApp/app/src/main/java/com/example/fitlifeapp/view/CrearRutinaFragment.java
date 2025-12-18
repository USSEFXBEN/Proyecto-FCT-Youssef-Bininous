package com.example.fitlifeapp.view;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.model.Rutina;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CrearRutinaFragment extends Fragment {

    private EditText etNombre, etDescripcion;
    private TextView tvHoraSeleccionada;
    private Button btnSeleccionarHora, btnCancelar, btnGuardar;

    private CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves, cbViernes, cbSabado, cbDomingo;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private String horaSeleccionada = ""; // formato "HH:mm"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crear_rutina, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return view;
        }
        userId = mAuth.getCurrentUser().getUid();

        // === VISTAS ===
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

        // Seguridad extra: si algo es null, mejor avisar que crashear
        if (etNombre == null || etDescripcion == null) {
            Toast.makeText(getContext(),
                    "Error de vistas: revisa que el layout tenga etNombreRutina y etDescripcionRutina",
                    Toast.LENGTH_LONG).show();
        }

        // === LISTENERS ===
        btnSeleccionarHora.setOnClickListener(v -> mostrarTimePicker());

        btnGuardar.setOnClickListener(v -> guardarRutina());

        btnCancelar.setOnClickListener(v -> {
            NavController navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.popBackStack(); // vuelve atrás a la lista de rutinas
        });

        return view;
    }

    private void mostrarTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                    tvHoraSeleccionada.setText(horaSeleccionada);
                },
                hora,
                minuto,
                true
        );

        timePickerDialog.show();
    }

    private void guardarRutina() {
        // Por si acaso siguen siendo null, evitamos crash
        if (etNombre == null || etDescripcion == null) {
            Toast.makeText(getContext(),
                    "No se pudieron cargar los campos de texto (revisa el layout)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mapa de días activos
        Map<String, Boolean> diasActivos = new HashMap<>();
        if (cbLunes.isChecked()) diasActivos.put("lunes", true);
        if (cbMartes.isChecked()) diasActivos.put("martes", true);
        if (cbMiercoles.isChecked()) diasActivos.put("miercoles", true);
        if (cbJueves.isChecked()) diasActivos.put("jueves", true);
        if (cbViernes.isChecked()) diasActivos.put("viernes", true);
        if (cbSabado.isChecked()) diasActivos.put("sabado", true);
        if (cbDomingo.isChecked()) diasActivos.put("domingo", true);

        // Crear objeto Rutina (id = null, Firestore lo genera)
        Rutina rutina = new Rutina(
                null,
                userId,
                nombre,
                descripcion,
                horaSeleccionada,
                diasActivos
        );

        db.collection("routines")
                .add(rutina)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(getContext(), "Rutina creada", Toast.LENGTH_SHORT).show();
                    NavController navController =
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_crearRutinaFragment_to_nav_routines);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Error al crear rutina: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}
