package com.example.fitlifeapp.vistas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fitlifeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment encargado del registro de nuevos usuarios.
 * Crea el usuario en Firebase Authentication y guarda
 * sus datos básicos en Firestore.
 */
public class RegistrarFragment extends Fragment {

    // Campos de entrada
    private EditText etNombre, etEmail, etPass, etPassConfirm;

    // Botones y enlaces
    private Button btnRegistrar;
    private TextView tvIrALogin;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Navegación
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.registrar_fragment, container, false);

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Controlador de navegación
        navController =
                Navigation.findNavController(
                        requireActivity(),
                        R.id.nav_host_fragment
                );

        // Enlace de vistas
        etNombre = view.findViewById(R.id.etNombreRegistro);
        etEmail = view.findViewById(R.id.etEmailRegistro);
        etPass = view.findViewById(R.id.etPassRegistro);
        etPassConfirm = view.findViewById(R.id.etPassConfirmRegistro);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        tvIrALogin = view.findViewById(R.id.tvIrALogin);

        // Volver a la pantalla de login
        tvIrALogin.setOnClickListener(v ->
                navController.navigate(
                        R.id.action_registrarFragment_to_loginFragment
                )
        );

        // Botón de registro
        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        return view;
    }

    /**
     * Valida los datos introducidos y registra el usuario.
     * Primero se crea en Firebase Authentication y después
     * se guarda su información en Firestore.
     */
    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        String confirmPassword =
                etPassConfirm.getText().toString().trim();

        // Validación básica de campos
        if (nombre.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            Toast.makeText(
                    getContext(),
                    "Rellena todos los campos",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Comprobación de contraseñas
        if (!password.equals(confirmPassword)) {
            Toast.makeText(
                    getContext(),
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Crear usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(
                                    getContext(),
                                    "Error inesperado",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        String uid = user.getUid();

                        // Datos del usuario en Firestore
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("nombre", nombre);
                        userData.put("email", email);
                        userData.put("rol", "usuario"); // rol por defecto
                        userData.put(
                                "createdAt",
                                FieldValue.serverTimestamp()
                        );

                        db.collection("users")
                                .document(uid)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(
                                            getContext(),
                                            "Registro exitoso",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    navController.navigate(
                                            R.id.action_registrarFragment_to_dashboardFragment
                                    );
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                getContext(),
                                                "Error al guardar en la base de datos",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                );

                    } else {
                        Toast.makeText(
                                getContext(),
                                "Error en el registro",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
