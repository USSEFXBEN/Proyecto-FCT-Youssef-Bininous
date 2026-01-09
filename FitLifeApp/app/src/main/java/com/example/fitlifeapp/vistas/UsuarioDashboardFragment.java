package com.example.fitlifeapp.vistas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.Adaptadores.RutinaAdapter;
import com.example.fitlifeapp.model.Progreso;
import com.example.fitlifeapp.model.Rutina;
import com.google.android.material.progressindicator.CircularProgressIndicator;
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

/**
 * Fragment principal del usuario.
 * Muestra un saludo, las rutinas del día actual
 * y el progreso diario en forma de porcentaje.
 */
public class UsuarioDashboardFragment extends Fragment {

    private static final String TAG = "UsuarioDashboard";

    private TextView tvSaludoUsuario, tvMensajeDia, tvProgresoTexto;
    private RecyclerView rvRutinas;
    private CircularProgressIndicator circularProgressDashboard;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final List<Rutina> listaRutinas = new ArrayList<>();
    private final Map<String, String> progresoMap = new HashMap<>();
    private RutinaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_dashboard, container, false);

        tvSaludoUsuario = view.findViewById(R.id.tvSaludoUsuario);
        tvMensajeDia = view.findViewById(R.id.tvMensajeDia);
        tvProgresoTexto = view.findViewById(R.id.tvProgresoTexto);
        rvRutinas = view.findViewById(R.id.rvRutinas);
        circularProgressDashboard =
                view.findViewById(R.id.circularProgressDashboard);

        rvRutinas.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        adapter = new RutinaAdapter(
                listaRutinas,
                progresoMap,
                RutinaAdapter.Mode.DASHBOARD,
                new RutinaAdapter.RoutineItemListener() {

                    @Override
                    public void onItemClick(Rutina rutina, int position) {
                        manejarClickRutina(rutina, position);
                    }

                    @Override
                    public void onEditClick(Rutina rutina, int position) {
                        // No se utiliza en el dashboard
                    }

                    @Override
                    public void onDeleteClick(Rutina rutina, int position) {
                        // No se utiliza en el dashboard
                    }
                }
        );

        rvRutinas.setAdapter(adapter);

        cargarDatosUsuario();

        return view;
    }

    /**
     * Devuelve la clave del día actual de la semana
     * usada en Firestore (lunes, martes, etc.).
     */
    private String getCurrentDayOfWeekKey() {

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

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

    /**
     * Carga los datos del usuario, sus rutinas del día
     * y posteriormente el progreso asociado.
     */
    private void cargarDatosUsuario() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.w(TAG, "Usuario no autenticado");
            Toast.makeText(
                    getContext(),
                    "Usuario no autenticado",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String uid = currentUser.getUid();
        String dayKey = getCurrentDayOfWeekKey();

        // 1. Obtener nombre del usuario
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        String nombre =
                                documentSnapshot.getString("nombre");
                        tvSaludoUsuario.setText(
                                "Hola, " + nombre
                        );
                    } else {
                        tvSaludoUsuario.setText("Hola, Usuario");
                    }

                    tvMensajeDia.setText(
                            "Rutinas programadas para " +
                                    dayKey.toUpperCase()
                    );

                    // 2. Obtener rutinas activas para el día actual
                    db.collection("routines")
                            .whereEqualTo("userId", uid)
                            .whereEqualTo(
                                    "diasActivos." + dayKey,
                                    true
                            )
                            .get()
                            .addOnSuccessListener(querySnapshots -> {

                                listaRutinas.clear();

                                for (QueryDocumentSnapshot doc : querySnapshots) {
                                    Rutina r =
                                            doc.toObject(Rutina.class);
                                    r.setId(doc.getId());
                                    listaRutinas.add(r);
                                }

                                cargarProgreso(uid);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(
                                        TAG,
                                        "Error al cargar rutinas",
                                        e
                                );
                                Toast.makeText(
                                        getContext(),
                                        "Error al cargar rutinas de hoy",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e ->
                        Log.e(
                                TAG,
                                "Error al cargar usuario",
                                e
                        )
                );
    }

    /**
     * Carga el progreso del día actual desde Firestore
     * y lo asocia a cada rutina.
     */
    private void cargarProgreso(String uid) {

        String fechaHoy =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        db.collection("progress")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(progressDocs -> {

                    progresoMap.clear();

                    for (QueryDocumentSnapshot pDoc : progressDocs) {
                        Progreso p =
                                pDoc.toObject(Progreso.class);

                        if (fechaHoy.equals(p.getFecha())) {
                            progresoMap.put(
                                    p.getRutinaId(),
                                    p.getEstado()
                            );
                        }
                    }

                    adapter.notifyDataSetChanged();
                    actualizarProgreso();
                })
                .addOnFailureListener(e ->
                        Log.e(
                                TAG,
                                "Error al cargar progreso",
                                e
                        )
                );
    }

    /**
     * Calcula y muestra el porcentaje de rutinas
     * completadas en el día actual.
     */
    private void actualizarProgreso() {

        if (listaRutinas.isEmpty()) {
            tvProgresoTexto.setText(
                    "No hay rutinas programadas para hoy"
            );
            circularProgressDashboard.setProgress(0, true);
            return;
        }

        int completadas = 0;

        for (Rutina r : listaRutinas) {
            if ("completado".equalsIgnoreCase(
                    progresoMap.get(r.getId()))) {
                completadas++;
            }
        }

        int total = listaRutinas.size();
        int porcentaje =
                (int) ((completadas / (float) total) * 100);

        tvProgresoTexto.setText(
                "Completadas " + completadas + " de " + total +
                        " (" + porcentaje + "%)"
        );

        circularProgressDashboard.setProgress(
                porcentaje,
                true
        );
    }

    /**
     * Gestiona el clic sobre una rutina,
     * alternando su estado entre completado y pendiente.
     */
    private void manejarClickRutina(Rutina rutina, int position) {

        if (rutina.getId() == null) return;

        String rutinaId = rutina.getId();
        String estadoActual = progresoMap.get(rutinaId);

        String nuevoEstado =
                "completado".equalsIgnoreCase(estadoActual)
                        ? "pendiente"
                        : "completado";

        progresoMap.put(rutinaId, nuevoEstado);
        adapter.notifyItemChanged(position);
        actualizarProgreso();

        guardarProgresoEnFirestore(rutinaId, nuevoEstado);
    }

    /**
     * Guarda o actualiza el progreso de una rutina
     * para el día actual en Firestore.
     */
    private void guardarProgresoEnFirestore(
            String rutinaId,
            String nuevoEstado) {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        String fechaHoy =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        String docId =
                uid + "_" + rutinaId + "_" + fechaHoy;

        Progreso progreso = new Progreso();
        progreso.setUserId(uid);
        progreso.setRutinaId(rutinaId);
        progreso.setFecha(fechaHoy);
        progreso.setEstado(nuevoEstado);

        db.collection("progress")
                .document(docId)
                .set(progreso)
                .addOnSuccessListener(aVoid ->
                        Log.d(
                                TAG,
                                "Progreso guardado correctamente"
                        )
                )
                .addOnFailureListener(e ->
                        Log.e(
                                TAG,
                                "Error al guardar progreso",
                                e
                        )
                );
    }
}
