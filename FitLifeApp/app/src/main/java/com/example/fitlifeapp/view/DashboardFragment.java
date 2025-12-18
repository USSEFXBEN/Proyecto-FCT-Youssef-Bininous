package com.example.fitlifeapp.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.RutinaAdapter;
import com.example.fitlifeapp.model.Progreso;
import com.example.fitlifeapp.model.Rutina;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
// imports
import com.google.android.material.progressindicator.CircularProgressIndicator;


// campos



public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";

    private TextView tvSaludoUsuario, tvMensajeDia, tvProgresoTexto;
    private ImageView ivProgresoCircular;
    private RecyclerView rvRutinas;
    private ImageButton btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Rutina> listaRutinas = new ArrayList<>();
    // Mapea el ID de Rutina al Estado (e.g. "ID_rutina" -> "completado")
    private Map<String, String> progresoMap = new HashMap<>();
    private RutinaAdapter adapter;
    private ProgressBar progressBarDashboard;

    private CircularProgressIndicator circularProgressDashboard;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvSaludoUsuario = view.findViewById(R.id.tvSaludoUsuario);
        tvMensajeDia = view.findViewById(R.id.tvMensajeDia);
        tvProgresoTexto = view.findViewById(R.id.tvProgresoTexto);

        rvRutinas = view.findViewById(R.id.rvRutinas);
        btnLogout = view.findViewById(R.id.btnLogout);

        circularProgressDashboard = view.findViewById(R.id.circularProgressDashboard);

        rvRutinas.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Adapter en modo DASHBOARD
        adapter = new RutinaAdapter(
                listaRutinas,
                progresoMap,
                RutinaAdapter.Mode.DASHBOARD,
                new RutinaAdapter.RoutineItemListener() {
                    @Override
                    public void onItemClick(Rutina rutina, int position) {
                        // Click en la rutina -> marcar / desmarcar completado
                        manejarClickRutina(rutina, position);
                    }

                    @Override
                    public void onEditClick(Rutina rutina, int position) {
                        // No se usa en Dashboard
                    }

                    @Override
                    public void onDeleteClick(Rutina rutina, int position) {
                        // No se usa en Dashboard
                    }
                }
        );
        rvRutinas.setAdapter(adapter);

        btnLogout.setOnClickListener(v -> cerrarSesion());

        cargarDatosUsuario();

        return view;
    }

    private void cerrarSesion() {
        mAuth.signOut();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == R.id.nav_home) {
            navController.navigate(R.id.action_dashboardFragment_to_loginFragment);
        }
    }

    private String getCurrentDayOfWeekKey() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Claves deben coincidir con 'diasActivos' en Firestore
        switch (dayOfWeek) {
            case Calendar.MONDAY: return "lunes";
            case Calendar.TUESDAY: return "martes";
            case Calendar.WEDNESDAY: return "miercoles";
            case Calendar.THURSDAY: return "jueves";
            case Calendar.FRIDAY: return "viernes";
            case Calendar.SATURDAY: return "sabado";
            case Calendar.SUNDAY: return "domingo";
            default: return "";
        }
    }

    private void cargarDatosUsuario() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            cerrarSesion();
            return;
        }

        String uid = currentUser.getUid();
        Log.d(TAG, "UID real autenticado: " + uid);
        String dayKey = getCurrentDayOfWeekKey();

        // 1️⃣ Cargar nombre del usuario (saludo)
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        tvSaludoUsuario.setText("Hola, " + nombre + " 👋");
                    } else {
                        tvSaludoUsuario.setText("Hola, Usuario 👋");
                    }
                    tvMensajeDia.setText("¡Hagamos de hoy un día increíble! (" + dayKey.toUpperCase() + ")");

                    // 2️⃣ Cargar Rutinas de Hoy (usuario + día activo)
                    db.collection("routines")
                            .whereEqualTo("userId", uid)
                            .whereEqualTo("diasActivos." + dayKey, true)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                listaRutinas.clear();
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    Rutina r = doc.toObject(Rutina.class);
                                    // Importante: guardamos el ID del documento
                                    r.setId(doc.getId());
                                    listaRutinas.add(r);
                                }

                                // 3️⃣ Cargar Progreso del día
                                cargarProgreso(uid);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al cargar rutinas: " + e.getMessage());
                                Toast.makeText(getContext(), "Error al cargar rutinas de hoy.", Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener usuario: " + e.getMessage());
                });
    }

    private void cargarProgreso(String uid) {
        // Fecha de hoy para filtrar solo el progreso de hoy
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        db.collection("progress")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(progressDocs -> {
                    progresoMap.clear();
                    for (QueryDocumentSnapshot pDoc : progressDocs) {
                        Progreso p = pDoc.toObject(Progreso.class);
                        // Solo mapeamos el progreso que coincide con la fecha de hoy
                        if (p.getRutinaId() != null &&
                                p.getEstado() != null &&
                                fechaHoy.equals(p.getFecha())) {
                            progresoMap.put(p.getRutinaId(), p.getEstado());
                        }
                    }
                    // 4️⃣ Notificar al adapter y actualizar la vista
                    adapter.notifyDataSetChanged();
                    actualizarProgreso();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar progreso: " + e.getMessage());
                });
    }

    private void actualizarProgreso() {
        if (listaRutinas.isEmpty()) {
            tvProgresoTexto.setText("¡No hay rutinas programadas para hoy!");
            circularProgressDashboard.setProgress(0, true);
            return;
        }

        int completadas = 0;
        for (Rutina r : listaRutinas) {
            String estado = progresoMap.get(r.getId());
            if ("completado".equalsIgnoreCase(estado)) {
                completadas++;
            }
        }

        int total = listaRutinas.size();
        int porcentaje = (int) ((completadas / (float) total) * 100);

        tvProgresoTexto.setText(
                "¡Has completado " + completadas + "/" + total + " rutinas de hoy! (" + porcentaje + "%)"
        );

        circularProgressDashboard.setProgress(porcentaje, true); // true = animado
    }




    private void manejarClickRutina(Rutina rutina, int position) {
        if (rutina.getId() == null) return;

        String rutinaId = rutina.getId();
        String estadoActual = progresoMap.get(rutinaId);
        String nuevoEstado;

        // Si es null o "pendiente", cambia a "completado". Si es "completado", cambia a "pendiente".
        if ("completado".equalsIgnoreCase(estadoActual)) {
            nuevoEstado = "pendiente";
        } else {
            nuevoEstado = "completado";
        }

        // 1. Actualizar el estado en el mapa local
        progresoMap.put(rutinaId, nuevoEstado);

        // 2. Notificar al adapter y actualizar el progreso general
        adapter.notifyItemChanged(position);
        actualizarProgreso();

        // 3. Guardar en Firestore
        guardarProgresoEnFirestore(rutinaId, nuevoEstado);
    }

    private void guardarProgresoEnFirestore(String rutinaId, String nuevoEstado) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Clave única para el documento de progreso: userId_rutinaId_fecha
        String docId = uid + "_" + rutinaId + "_" + fechaHoy;

        Progreso progreso = new Progreso();
        progreso.setUserId(uid);
        progreso.setRutinaId(rutinaId);
        progreso.setFecha(fechaHoy);
        progreso.setEstado(nuevoEstado);

        db.collection("progress")
                .document(docId)
                .set(progreso)
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "Progreso actualizado correctamente en Firestore"))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error al guardar progreso: " + e.getMessage()));
    }
}
