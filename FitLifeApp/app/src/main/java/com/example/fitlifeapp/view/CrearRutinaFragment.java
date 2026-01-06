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
import androidx.navigation.Navigation;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.model.Rutina;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CrearRutinaFragment extends Fragment {

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

        View view = inflater.inflate(R.layout.fragment_crear_rutina, container, false);

        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return view;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        btnSeleccionarHora.setOnClickListener(v -> mostrarTimePicker());
        btnGuardar.setOnClickListener(v -> guardarRutina());
        btnCancelar.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .popBackStack());

        return view;
    }

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

    private void guardarRutina() {

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Boolean> diasActivos = new HashMap<>();
        if (cbLunes.isChecked()) diasActivos.put("lunes", true);
        if (cbMartes.isChecked()) diasActivos.put("martes", true);
        if (cbMiercoles.isChecked()) diasActivos.put("miercoles", true);
        if (cbJueves.isChecked()) diasActivos.put("jueves", true);
        if (cbViernes.isChecked()) diasActivos.put("viernes", true);
        if (cbSabado.isChecked()) diasActivos.put("sabado", true);
        if (cbDomingo.isChecked()) diasActivos.put("domingo", true);

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

                    // 🔥 INCREMENTAR CONTADOR
                    db.collection("users")
                            .document(userId)
                            .update("totalRutinas", FieldValue.increment(1));

                    Toast.makeText(getContext(), "Rutina creada", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(R.id.action_crearRutinaFragment_to_nav_routines);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Error al crear rutina",
                                Toast.LENGTH_SHORT).show());
    }
}
