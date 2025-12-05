package com.example.fitlifeapp.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.RutinaAdapter;
import com.example.fitlifeapp.model.Rutina;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutinesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RutinaAdapter adapter;
    private List<Rutina> listaRutinas;
    private FloatingActionButton fabAgregar;
    private TextView tvVacio;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rutinas, container, false);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        } else {
            Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Configurar vistas
        recyclerView = view.findViewById(R.id.routines_recycler_view);
        fabAgregar = view.findViewById(R.id.fab_add_routine);
        // tvVacio = view.findViewById(R.id.tvListaVacia); // opcional

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaRutinas = new ArrayList<>();

        // Configurar Adapter correctamente
        adapter = new RutinaAdapter(listaRutinas, new HashMap<>(), (rutina, position) -> eliminarRutina(rutina));
        recyclerView.setAdapter(adapter);

        // Botón agregar rutina
        fabAgregar.setOnClickListener(v -> mostrarDialogoCrear());

        // Cargar rutinas iniciales
        cargarRutinas();

        return view;
    }

    // --- DIALOGO PARA CREAR RUTINA ---
    private void mostrarDialogoCrear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nueva Rutina");

        final EditText input = new EditText(getContext());
        input.setHint("Nombre de la rutina (Ej: Pecho y Tríceps)");
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = input.getText().toString().trim();
            if (!nombre.isEmpty()) {
                guardarEnFirebase(nombre);
            } else {
                Toast.makeText(getContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // --- GUARDAR NUEVA RUTINA EN FIRESTORE ---
    private void guardarEnFirebase(String nombre) {
        // Crear objeto Rutina con constructor que acepte nombre y userId
        Rutina nuevaRutina = new Rutina(nombre, userId);

        db.collection("routines")
                .add(nuevaRutina)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Rutina guardada", Toast.LENGTH_SHORT).show();
                    cargarRutinas();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // --- CARGAR RUTINAS DESDE FIRESTORE ---
    private void cargarRutinas() {
        db.collection("routines")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaRutinas.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Rutina r = doc.toObject(Rutina.class);
                        r.setId(doc.getId()); // Guardar ID del documento
                        listaRutinas.add(r);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar rutinas", Toast.LENGTH_SHORT).show());
    }

    // --- ELIMINAR RUTINA ---
    private void eliminarRutina(Rutina rutina) {
        if (rutina.getId() == null) return;

        db.collection("routines").document(rutina.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Rutina eliminada", Toast.LENGTH_SHORT).show();
                    listaRutinas.remove(rutina);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar rutina", Toast.LENGTH_SHORT).show());
    }
}
