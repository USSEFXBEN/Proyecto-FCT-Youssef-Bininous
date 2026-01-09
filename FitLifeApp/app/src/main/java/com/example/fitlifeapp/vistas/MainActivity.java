package com.example.fitlifeapp.vistas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.fitlifeapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Actividad principal de la aplicación.
 * Contiene el NavHostFragment y el BottomNavigationView.
 * También se encarga de solicitar el permiso de notificaciones
 * en versiones recientes de Android.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurarNavegacion();
        solicitarPermisoNotificaciones();
    }

    /**
     * Configura la navegación con Navigation Component
     * y controla la visibilidad del BottomNavigationView
     * según el fragment actual.
     */
    private void configurarNavegacion() {

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            Log.e(TAG, "NavHostFragment no encontrado");
            return;
        }

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Control de visibilidad del menú inferior
        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {

                    int id = destination.getId();

                    if (id == R.id.loginFragment
                            || id == R.id.registrarFragment
                            || id == R.id.nav_admin) {

                        bottomNavigationView.setVisibility(View.GONE);

                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    /**
     * Solicita el permiso POST_NOTIFICATIONS en Android 13 o superior.
     * En versiones anteriores no es necesario.
     */
    private void solicitarPermisoNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );

            } else {
                Log.d(TAG, "Permiso de notificaciones ya concedido");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "Permiso de notificaciones concedido");

            } else {
                Log.w(TAG, "Permiso de notificaciones denegado");
            }
        }
    }
}
