package com.example.fitlifeapp.view;

import android.annotation.SuppressLint;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditarRutinaFragment extends Fragment {

    private EditText etNombre, etDescripcion;

    private TextView tvHoraSeleccionada;
    private Button btnSeleccionarHora, btnCancelar, btnGuardarCambios, btnEliminar;

    private CheckBox cbLunes, cbMartes, cbMiercoles, cbJueves, cbViernes, cbSabado, cbDomingo;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String rutinaId;
    private String userId;
    private String horaSeleccionada = "";

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_editar_rutina, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return view;
        }
        userId = mAuth.getCurrentUser().getUid();

        if (getArguments() != null) {
            rutinaId = getArguments().getString("rutinaId");
        }

        if (rutinaId == null) {
            Toast.makeText(getContext(), "Error: rutina no especificada", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Views
        etNombre = view.findViewById(R.id.etNombreRutinaEdit);
        etDescripcion = view.findViewById(R.id.etDescripcionRutinaEdit);
        tvHoraSeleccionada = view.findViewById(R.id.tvHoraSeleccionadaEdit);
        btnSeleccionarHora = view.findViewById(R.id.btnSeleccionarHoraEdit);
        btnCancelar = view.findViewById(R.id.btnCancelarEditar);
        btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios);
        btnEliminar = view.findViewById(R.id.btnEliminarRutina);

        cbLunes = view.findViewById(R.id.cbLunesEdit);
        cbMartes = view.findViewById(R.id.cbMartesEdit);
        cbMiercoles = view.findViewById(R.id.cbMiercolesEdit);
        cbJueves = view.findViewById(R.id.cbJuevesEdit);
        cbViernes = view.findViewById(R.id.cbViernesEdit);
        cbSabado = view.findViewById(R.id.cbSabadoEdit);
        cbDomingo = view.findViewById(R.id.cbDomingoEdit);

        btnSeleccionarHora.setOnClickListener(v -> mostrarTimePicker());
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
        btnCancelar.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack());
        btnEliminar.setOnClickListener(v -> eliminarRutina());

        // Cargar datos actuales de la rutina
        cargarRutinaDesdeFirestore();

        return view;
    }

    private void mostrarTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                    tvHoraSeleccionada.setText(horaSeleccionada);
                }, hora, minuto, true);

        timePickerDialog.show();
    }

    private void cargarRutinaDesdeFirestore() {
        db.collection("routines").document(rutinaId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(getContext(), "Rutina no encontrada", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Rutina r = doc.toObject(Rutina.class);
                    if (r == null) return;

                    // Rellenar campos
                    if (r.getNombre() != null) {
                        etNombre.setText(r.getNombre());
                    }
                    if (r.getDescripcion() != null) {
                        etDescripcion.setText(r.getDescripcion());
                    }

                    horaSeleccionada = r.getHoraRecordatorio() != null ? r.getHoraRecordatorio() : "";
                    if (horaSeleccionada.isEmpty()) {
                        tvHoraSeleccionada.setText("Sin hora");
                    } else {
                        tvHoraSeleccionada.setText(horaSeleccionada);
                    }

                    Map<String, Boolean> dias = r.getDiasActivos();
                    if (dias != null) {
                        cbLunes.setChecked(Boolean.TRUE.equals(dias.get("lunes")));
                        cbMartes.setChecked(Boolean.TRUE.equals(dias.get("martes")));
                        cbMiercoles.setChecked(Boolean.TRUE.equals(dias.get("miercoles")));
                        cbJueves.setChecked(Boolean.TRUE.equals(dias.get("jueves")));
                        cbViernes.setChecked(Boolean.TRUE.equals(dias.get("viernes")));
                        cbSabado.setChecked(Boolean.TRUE.equals(dias.get("sabado")));
                        cbDomingo.setChecked(Boolean.TRUE.equals(dias.get("domingo")));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar rutina: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void guardarCambios() {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String descripcion = etDescripcion.getText() != null ? etDescripcion.getText().toString().trim() : "";

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

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("nombre", nombre);
        updateData.put("descripcion", descripcion);
        updateData.put("horaRecordatorio", horaSeleccionada);
        updateData.put("diasActivos", diasActivos);
        updateData.put("userId", userId); // por seguridad

        db.collection("routines").document(rutinaId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Rutina actualizada", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_editarRutinaFragment_to_nav_routines);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al actualizar rutina: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void eliminarRutina() {
        db.collection("routines").document(rutinaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Rutina eliminada", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_editarRutinaFragment_to_nav_routines);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al eliminar rutina: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
