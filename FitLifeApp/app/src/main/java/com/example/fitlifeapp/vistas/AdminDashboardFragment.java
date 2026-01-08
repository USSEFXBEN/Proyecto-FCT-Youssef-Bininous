package com.example.fitlifeapp.vistas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.Adaptadores.UsuariosAdapter;
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

        listaUsuarios = new ArrayList<>();

        usuariosAdapter = new UsuariosAdapter(
                listaUsuarios,
                this::mostrarOpcionesAdmin
        );

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

        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot ->
                        tvUsers.setText(snapshot.size() + "\nUsuarios")
                );

        db.collection("routines")
                .get()
                .addOnSuccessListener(snapshot ->
                        tvRoutines.setText(snapshot.size() + "\nRutinas")
                );

        db.collection("recordatorios")
                .get()
                .addOnSuccessListener(snapshot ->
                        tvReminders.setText(snapshot.size() + "\nRecordatorios")
                );
    }

    private void cargarUsuarios() {

        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {

                    listaUsuarios.clear();

                    snapshot.forEach(doc -> {
                        Usuario usuario = doc.toObject(Usuario.class);
                        usuario.setId(doc.getId());
                        listaUsuarios.add(usuario);
                    });

                    usuariosAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Usuarios cargados: " + listaUsuarios.size());
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error cargando usuarios", e)
                );
    }

    // 👑 OPCIONES ADMIN
    private void mostrarOpcionesAdmin(Usuario usuario) {

        String[] opciones = {
                usuario.getRol().equals("admin")
                        ? "Cambiar a usuario"
                        : "Cambiar a administrador",
                "Eliminar usuario"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle(usuario.getNombre())
                .setItems(opciones, (dialog, which) -> {

                    if (which == 0) {
                        cambiarRol(usuario);
                    } else {
                        confirmarEliminarUsuario(usuario);
                    }
                })
                .show();
    }

    // 🔁 CAMBIAR ROL
    private void cambiarRol(Usuario usuario) {

        String nuevoRol =
                usuario.getRol().equals("admin") ? "user" : "admin";

        db.collection("users")
                .document(usuario.getId())
                .update("rol", nuevoRol)
                .addOnSuccessListener(unused -> {
                    usuario.setRol(nuevoRol);
                    usuariosAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            getContext(),
                            "Rol actualizado a " + nuevoRol,
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // 🗑️ CONFIRMAR BORRADO
    private void confirmarEliminarUsuario(Usuario usuario) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar usuario")
                .setMessage("¿Eliminar definitivamente a " + usuario.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> eliminarUsuario(usuario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ❌ ELIMINAR USUARIO (Firestore)
    private void eliminarUsuario(Usuario usuario) {

        db.collection("users")
                .document(usuario.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    listaUsuarios.remove(usuario);
                    usuariosAdapter.notifyDataSetChanged();
                    Toast.makeText(
                            getContext(),
                            "Usuario eliminado",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void configurarLogout(View view) {
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Navigation.findNavController(view)
                    .navigate(R.id.loginFragment);
        });
    }
}
