package com.example.fitlifeapp.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.fitlifeapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Obtener la referencia a la BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 2. Obtener el NavController del NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // 3. Conectar la BottomNavigationView con el NavController
            // Esto funciona porque los IDs de los destinos en nav_graph.xml (nav_home, nav_routines)
            // coinciden con los IDs del menú (bottom_nav_menu.xml).
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            // 4. Lógica para ocultar/mostrar la BottomNavigationView en pantallas específicas
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();

                // IDs de los destinos donde la barra debe estar OCULTA (Login/Registro)
                if (destId == R.id.loginFragment || destId == R.id.registrarFragment) {
                    bottomNavigationView.setVisibility(View.GONE);
                }
                // En cualquier otro destino (Dashboard, Rutinas, etc.), la barra es VISIBLE
                else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}