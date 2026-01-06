package com.example.fitlifeapp.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fitlifeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilFragment extends Fragment {

    private FirebaseAuth auth;

    private TextView tvName, tvEmail, btnLogout;
    private Button btnChangePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        Switch switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        if (user != null) {
            tvEmail.setText(user.getEmail());
            tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Usuario");
        }

        // Estado inicial del dark mode
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        switchDarkMode.setChecked(currentMode == AppCompatDelegate.MODE_NIGHT_YES);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        btnChangePassword.setOnClickListener(v -> mostrarDialogCambiarPassword());

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Navigation.findNavController(view)
                    .navigate(R.id.loginFragment);
        });

        return view;
    }

    private void mostrarDialogCambiarPassword() {

        EditText etPassword = new EditText(getContext());
        etPassword.setHint("Nueva contraseña");

        new AlertDialog.Builder(getContext())
                .setTitle("Cambiar contraseña")
                .setView(etPassword)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String newPassword = etPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                        Toast.makeText(
                                getContext(),
                                "La contraseña debe tener al menos 6 caracteres",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) return;

                    user.updatePassword(newPassword)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(
                                            getContext(),
                                            "Contraseña actualizada",
                                            Toast.LENGTH_SHORT
                                    ).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            getContext(),
                                            "Error: vuelve a iniciar sesión",
                                            Toast.LENGTH_SHORT
                                    ).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
