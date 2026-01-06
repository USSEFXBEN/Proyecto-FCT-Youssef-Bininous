package com.example.fitlifeapp.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.UsuariosAdapter;
import com.example.fitlifeapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFragment extends Fragment {

    private static final String TAG = "AdminDashboard";

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView tvWelcome, tvUsers, tvRoutines, tvReminders;
    private RecyclerView rvUsers;
    private Button btnLogout;

    private UsuariosAdapter usuariosAdapter;
    private List<Usuario> listaUsuarios;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_dashboard_admin, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvWelcome = view.findViewById(R.id.tvAdminWelcome);
        tvUsers = view.findViewById(R.id.tvTotalUsers);
        tvRoutines = view.findViewById(R.id.tvTotalRoutines);
        tvReminders = view.findViewById(R.id.tvTotalReminders);
        btnLogout = view.findViewById(R.id.btnAdminLogout);
        rvUsers = view.findViewById(R.id.rvUsers);

        // RecyclerView
        listaUsuarios = new ArrayList<>();
        usuariosAdapter = new UsuariosAdapter(listaUsuarios);

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(usuariosAdapter);

        cargarAdmin();
        cargarEstadisticas();
        cargarUsuarios();
        configurarLogout(view);

        return view;
    }

    private void cargarAdmin() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nombre = doc.getString("nombre");
                        tvWelcome.setText("Bienvenido de nuevo, " + nombre);
                    }
                });
    }

    private void cargarEstadisticas() {

        // 👤 TOTAL USUARIOS
        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    int totalUsers = snapshot.size();
                    tvUsers.setText(totalUsers + "\nUsuarios");
                });

        // 📋 TOTAL RUTINAS (🔥 ESTO ES LO QUE FALTABA)
        db.collection("routines")
                .get()
                .addOnSuccessListener(snapshot -> {
                    int totalRoutines = snapshot.size();
                    tvRoutines.setText(totalRoutines + "\nRutinas");
                });

        // ⏰ TOTAL RECORDATORIOS
        db.collection("recordatorios")
                .get()
                .addOnSuccessListener(snapshot -> {
                    int totalReminders = snapshot.size();
                    tvReminders.setText(totalReminders + "\nRecordatorios");
                });
    }


    private void cargarUsuarios() {

        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {

                    listaUsuarios.clear();

                    snapshot.forEach(doc -> {
                        Usuario usuario = doc.toObject(Usuario.class);
                        usuario.setId(doc.getId()); // UID real
                        listaUsuarios.add(usuario);
                    });

                    usuariosAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Usuarios cargados: " + listaUsuarios.size());
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error cargando usuarios", e)
                );
    }

    private void configurarLogout(View view) {
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Navigation.findNavController(view)
                    .navigate(R.id.loginFragment);
        });
    }
}
