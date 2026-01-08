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

public class RegistrarFragment extends Fragment {

    private EditText etNombre, etEmail, etPass, etPassConfirm;
    private Button btnRegistrar;
    private TextView tvIrALogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registrar_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        etNombre = view.findViewById(R.id.etNombreRegistro);
        etEmail = view.findViewById(R.id.etEmailRegistro);
        etPass = view.findViewById(R.id.etPassRegistro);
        etPassConfirm = view.findViewById(R.id.etPassConfirmRegistro);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        tvIrALogin = view.findViewById(R.id.tvIrALogin);

        tvIrALogin.setOnClickListener(v -> navController.navigate(R.id.action_registrarFragment_to_loginFragment));
        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        return view;
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        String confirmPassword = etPassConfirm.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario en Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(getContext(), "Error inesperado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = user.getUid();

                        // ========== GUARDAR USUARIO EN FIRESTORE ==========
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("nombre", nombre);
                        userData.put("email", email);
                        userData.put("rol", "usuario"); // rol por defecto
                        userData.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("users")
                                .document(uid)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Registro exitoso ✔", Toast.LENGTH_SHORT).show();
                                    navController.navigate(R.id.action_registrarFragment_to_dashboardFragment);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error al guardar en Base de Datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(getContext(), "Error en el registro: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
