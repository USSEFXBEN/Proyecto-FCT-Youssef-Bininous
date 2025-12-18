package com.example.fitlifeapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.RecordatoriosAdapter;
import com.example.fitlifeapp.controller.ReminderScheduler;
import com.example.fitlifeapp.model.Recordatorio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecordatoriosFragment extends Fragment {

    private RecyclerView rvRecordatorios;
    private FloatingActionButton fabAdd;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final List<Recordatorio> lista = new ArrayList<>();
    private RecordatoriosAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recordatorios, container, false);

        rvRecordatorios = view.findViewById(R.id.rvRecordatorios);
        fabAdd = view.findViewById(R.id.fabAddRecordatorio);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rvRecordatorios.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RecordatoriosAdapter(lista, new RecordatoriosAdapter.Listener() {

            // 🔁 ACTIVAR / DESACTIVAR RECORDATORIO
            @Override
            public void onSwitchChanged(Recordatorio r, boolean activo) {

                db.collection("recordatorios")
                        .document(r.getId())
                        .update("activo", activo)
                        .addOnSuccessListener(unused -> {

                            r.setActivo(activo);

                            if (activo) {
                                // 🔔 Programar notificación
                                ReminderScheduler.programarRecordatorio(requireContext(), r);
                                Toast.makeText(getContext(), "Recordatorio activado", Toast.LENGTH_SHORT).show();
                            } else {
                                // ❌ Cancelar notificación
                                ReminderScheduler.cancelarRecordatorio(requireContext(), r);
                                Toast.makeText(getContext(), "Recordatorio desactivado", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                        );
            }

            // 🗑 ELIMINAR RECORDATORIO
            @Override
            public void onDelete(Recordatorio r) {

                // Cancelamos alarma antes de borrar
                ReminderScheduler.cancelarRecordatorio(requireContext(), r);

                db.collection("recordatorios")
                        .document(r.getId())
                        .delete()
                        .addOnSuccessListener(unused -> {
                            lista.remove(r);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Recordatorio eliminado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                        );
            }
        });

        rvRecordatorios.setAdapter(adapter);

        cargarRecordatorios();

        fabAdd.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_nav_reminders_to_crearRecordatorio)
        );

        return view;
    }

    // 📥 CARGAR RECORDATORIOS DEL USUARIO
    private void cargarRecordatorios() {

        db.collection("recordatorios")
                .whereEqualTo("userId", mAuth.getUid())
                .get()
                .addOnSuccessListener(q -> {

                    lista.clear();

                    for (var doc : q) {
                        Recordatorio r = doc.toObject(Recordatorio.class);
                        r.setId(doc.getId());
                        lista.add(r);

                        // 🔁 Reprogramar si está activo (por si vuelve al fragment)
                        if (r.isActivo()) {
                            ReminderScheduler.programarRecordatorio(requireContext(), r);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar recordatorios", Toast.LENGTH_SHORT).show()
                );
    }
}
