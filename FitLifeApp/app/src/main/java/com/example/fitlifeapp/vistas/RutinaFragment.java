package com.example.fitlifeapp.vistas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.Adaptadores.RutinaAdapter;
import com.example.fitlifeapp.model.Recordatorio;
import com.example.fitlifeapp.model.Rutina;
import com.example.fitlifeapp.notificacion.ReminderScheduler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class RutinaFragment extends Fragment {

    private RecyclerView recyclerView;
    private RutinaAdapter adapter;
    private List<Rutina> listaRutinas;
    private FloatingActionButton fabAgregar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rutinas, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        } else {
            Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return view;
        }

        recyclerView = view.findViewById(R.id.routines_recycler_view);
        fabAgregar = view.findViewById(R.id.fab_add_routine);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaRutinas = new ArrayList<>();

        adapter = new RutinaAdapter(
                listaRutinas,
                null,
                RutinaAdapter.Mode.MANAGEMENT,
                new RutinaAdapter.RoutineItemListener() {

                    @Override
                    public void onItemClick(Rutina rutina, int position) {
                        abrirEditarRutina(rutina);
                    }

                    @Override
                    public void onEditClick(Rutina rutina, int position) {
                        abrirEditarRutina(rutina);
                    }

                    @Override
                    public void onDeleteClick(Rutina rutina, int position) {
                        eliminarRutina(rutina);
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        fabAgregar.setOnClickListener(v -> abrirCrearRutina());

        cargarRutinas();

        return view;
    }

    private void abrirCrearRutina() {
        NavController navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_nav_routines_to_crearRutinaFragment);
    }

    private void abrirEditarRutina(Rutina rutina) {
        NavController navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        Bundle bundle = new Bundle();
        bundle.putString("rutinaId", rutina.getId());

        navController.navigate(
                R.id.action_nav_routines_to_editarRutinaFragment,
                bundle
        );
    }

    private void cargarRutinas() {
        db.collection("routines")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaRutinas.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Rutina r = doc.toObject(Rutina.class);
                        r.setId(doc.getId());
                        listaRutinas.add(r);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Error al cargar rutinas",
                                Toast.LENGTH_SHORT).show());
    }

    // 🗑️ ELIMINAR RUTINA + PROGRESO + RECORDATORIOS
    private void eliminarRutina(Rutina rutina) {

        if (rutina.getId() == null) return;

        String tituloRecordatorio = "Rutina: " + rutina.getNombre();

        // 1️⃣ Buscar recordatorios asociados a esta rutina
        db.collection("recordatorios")
                .whereEqualTo("userId", userId)
                .whereEqualTo("titulo", tituloRecordatorio)
                .get()
                .addOnSuccessListener(recordatoriosSnapshot -> {

                    // 2️⃣ Buscar progreso
                    db.collection("progress")
                            .whereEqualTo("rutinaId", rutina.getId())
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener(progressSnapshots -> {

                                WriteBatch batch = db.batch();

                                // 🔔 Cancelar y borrar recordatorios
                                for (QueryDocumentSnapshot doc : recordatoriosSnapshot) {
                                    Recordatorio r = doc.toObject(Recordatorio.class);
                                    r.setId(doc.getId());

                                    ReminderScheduler.cancelarRecordatorio(
                                            requireContext().getApplicationContext(),
                                            r
                                    );

                                    batch.delete(doc.getReference());
                                }

                                // 🗑️ Borrar progreso
                                for (QueryDocumentSnapshot doc : progressSnapshots) {
                                    batch.delete(doc.getReference());
                                }

                                // 🗑️ Borrar rutina
                                batch.delete(
                                        db.collection("routines").document(rutina.getId())
                                );

                                // 3️⃣ Ejecutar borrado
                                batch.commit()
                                        .addOnSuccessListener(unused -> {

                                            db.collection("users")
                                                    .document(userId)
                                                    .update(
                                                            "totalRutinas",
                                                            FieldValue.increment(-1)
                                                    );

                                            listaRutinas.remove(rutina);
                                            adapter.notifyDataSetChanged();

                                            Toast.makeText(
                                                    getContext(),
                                                    "Rutina y recordatorios eliminados",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(
                                                        getContext(),
                                                        "Error al eliminar datos",
                                                        Toast.LENGTH_SHORT
                                                ).show());
                            });
                });
    }
}
