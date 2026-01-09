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

/**
 * Fragment que actúa como panel de control del administrador.
 * Permite ver estadísticas generales, listar usuarios
 * y realizar acciones administrativas sobre ellos.
 */
public class AdminDashboardFragment extends Fragment {

    private static final String TAG = "AdminDashboard";

    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Vistas
    private TextView tvWelcome, tvUsers, tvRoutines, tvReminders;
    private RecyclerView rvUsers;
    private Button btnLogout;

    // RecyclerView
    private UsuariosAdapter usuariosAdapter;
    private List<Usuario> listaUsuarios;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // Inflamos el layout del dashboard de administrador
        View view = inflater.inflate(
                R.layout.fragment_dashboard_admin, container, false);

        // Inicialización de Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a vistas
        tvWelcome = view.findViewById(R.id.tvAdminWelcome);
        tvUsers = view.findViewById(R.id.tvTotalUsers);
        tvRoutines = view.findViewById(R.id.tvTotalRoutines);
        tvReminders = view.findViewById(R.id.tvTotalReminders);
        btnLogout = view.findViewById(R.id.btnAdminLogout);
        rvUsers = view.findViewById(R.id.rvUsers);

        // Lista de usuarios
        listaUsuarios = new ArrayList<>();

        // Adapter con callback para opciones de administrador
        usuariosAdapter = new UsuariosAdapter(
                listaUsuarios,
                this::mostrarOpcionesAdmin
        );

        // Configuración del RecyclerView
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(usuariosAdapter);

        // Carga de datos
        cargarAdmin();
        cargarEstadisticas();
        cargarUsuarios();
        configurarLogout(view);

        return view;
    }

    /**
     * Carga la información del administrador logueado
     * para mostrar un mensaje de bienvenida.
     */
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

    /**
     * Obtiene estadísticas generales de la aplicación:
     * número de usuarios, rutinas y recordatorios.
     */
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

    /**
     * Carga todos los usuarios desde Firestore
     * y los muestra en el RecyclerView.
     */
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

    /**
     * Muestra un diálogo con opciones administrativas
     * para el usuario seleccionado.
     */
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

    /**
     * Cambia el rol del usuario entre "admin" y "user".
     */
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

    /**
     * Muestra un diálogo de confirmación antes
     * de eliminar definitivamente un usuario.
     */
    private void confirmarEliminarUsuario(Usuario usuario) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar usuario")
                .setMessage("¿Eliminar definitivamente a " + usuario.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> eliminarUsuario(usuario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina el usuario de la colección "users" en Firestore
     * y lo quita de la lista local.
     */
    private void eliminarUsuario(Usuario usuario) {

        String uid = usuario.getId();

        // 1️⃣ Borrar rutinas
        db.collection("routines")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(rutinas -> {
                    for (var doc : rutinas) {
                        doc.getReference().delete();
                    }
                });

        // 2️⃣ Borrar recordatorios
        db.collection("recordatorios")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(recordatorios -> {
                    for (var doc : recordatorios) {
                        doc.getReference().delete();
                    }
                });

        // 3️⃣ Borrar progreso
        db.collection("progress")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(progress -> {
                    for (var doc : progress) {
                        doc.getReference().delete();
                    }
                });

        // 4️⃣ Borrar usuario
        db.collection("users")
                .document(uid)
                .delete()
                .addOnSuccessListener(unused -> {

                    listaUsuarios.remove(usuario);
                    usuariosAdapter.notifyDataSetChanged();

                    Toast.makeText(
                            getContext(),
                            "Usuario eliminado completamente",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    /**
     * Cierra la sesión del administrador y navega
     * de vuelta a la pantalla de login.
     */
    private void configurarLogout(View view) {
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Navigation.findNavController(view)
                    .navigate(R.id.loginFragment);
        });
    }
}
