package com.example.fitlifeapp.view;

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
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private NavController navController;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        if (usuarioActual != null) {
            comprobarRolYRedirigir(usuarioActual.getUid());
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvRegister = view.findViewById(R.id.tvRegister);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);

        tvRegister.setOnClickListener(
                v -> navController.navigate(R.id.action_loginFragment_to_registrarFragment));

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Función próximamente disponible",
                        Toast.LENGTH_SHORT).show());

        btnLogin.setOnClickListener(v -> iniciarSesion());

        return view;
    }

    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(),
                    "Rellena todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            comprobarRolYRedirigir(user.getUid());
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "Error en el inicio de sesión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Comprueba el rol del usuario en Firestore y redirige
     */
    private void comprobarRolYRedirigir(String uid) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(getContext(),
                                "Usuario sin datos",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String rol = doc.getString("rol");

                    if ("admin".equalsIgnoreCase(rol)) {
                        navController.navigate(R.id.nav_admin);
                    } else {
                        navController.navigate(R.id.nav_home);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Error al verificar el rol",
                                Toast.LENGTH_SHORT).show());
    }
}
