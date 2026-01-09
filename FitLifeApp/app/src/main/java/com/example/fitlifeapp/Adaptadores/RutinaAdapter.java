package com.example.fitlifeapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.model.Rutina;

import java.util.List;
import java.util.Map;

/**
 * Adapter reutilizable para mostrar rutinas en distintos contextos.
 * Soporta dos modos:
 * - DASHBOARD: vista simple para marcar rutinas como completadas.
 * - MANAGEMENT: vista de gestión para editar o eliminar rutinas.
 */
public class RutinaAdapter
        extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {

    /**
     * Modos de funcionamiento del adapter.
     */
    public enum Mode {
        DASHBOARD,      // Vista simple con estado de rutina
        MANAGEMENT      // Vista de gestión con editar y eliminar
    }

    private final List<Rutina> listaRutinas;

    // Mapa de progreso diario (solo se usa en modo DASHBOARD)
    private final Map<String, String> progresoMap;

    private final Mode mode;
    private final RoutineItemListener listener;

    /**
     * Listener genérico para comunicar acciones al Fragment.
     */
    public interface RoutineItemListener {

        /**
         * Click sobre el item completo (Dashboard).
         */
        void onItemClick(Rutina rutina, int position);

        /**
         * Click en el botón editar (Management).
         */
        void onEditClick(Rutina rutina, int position);

        /**
         * Click en el botón eliminar (Management).
         */
        void onDeleteClick(Rutina rutina, int position);
    }

    /**
     * Constructor del adapter.
     */
    public RutinaAdapter(List<Rutina> listaRutinas,
                         Map<String, String> progresoMap,
                         Mode mode,
                         RoutineItemListener listener) {

        this.listaRutinas = listaRutinas;
        this.progresoMap = progresoMap;
        this.mode = mode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view;

        // Selección del layout según el modo
        if (mode == Mode.DASHBOARD) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(
                            R.layout.item_rutina,
                            parent,
                            false
                    );
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(
                            R.layout.item_routine_management,
                            parent,
                            false
                    );
        }

        return new RutinaViewHolder(view, mode);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RutinaViewHolder holder,
            int position) {

        Rutina rutina = listaRutinas.get(position);
        if (rutina == null) return;

        if (mode == Mode.DASHBOARD) {
            bindDashboardItem(holder, rutina, position);
        } else {
            bindManagementItem(holder, rutina, position);
        }
    }

    /**
     * Enlaza los datos en modo DASHBOARD.
     */
    private void bindDashboardItem(
            @NonNull RutinaViewHolder holder,
            Rutina rutina,
            int position) {

        if (holder.txtNombre != null) {
            holder.txtNombre.setText(
                    rutina.getNombre() != null
                            ? rutina.getNombre()
                            : ""
            );
        }

        if (holder.txtHora != null) {
            String hora = rutina.getHoraRecordatorio();
            holder.txtHora.setText(
                    (hora == null || hora.trim().isEmpty())
                            ? "Sin hora"
                            : hora
            );
        }

        if (holder.txtDescripcion != null) {
            String descripcion = rutina.getDescripcion();
            holder.txtDescripcion.setText(
                    (descripcion == null || descripcion.trim().isEmpty())
                            ? "Sin descripción"
                            : descripcion
            );
        }

        // Estado visual según progreso diario
        if (holder.imgEstado != null && progresoMap != null) {
            String estado = progresoMap.get(rutina.getId());
            holder.imgEstado.setImageResource(
                    "completado".equalsIgnoreCase(estado)
                            ? R.drawable.ic_radio_button_on
                            : R.drawable.ic_radio_button_off
            );
        }

        // Click sobre el item completo
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(rutina, adapterPos);
                }
            }
        });
    }

    /**
     * Enlaza los datos en modo MANAGEMENT.
     */
    private void bindManagementItem(
            @NonNull RutinaViewHolder holder,
            Rutina rutina,
            int position) {

        if (holder.tvRoutineName != null) {
            holder.tvRoutineName.setText(
                    rutina.getNombre() != null
                            ? rutina.getNombre()
                            : ""
            );
        }

        if (holder.tvRoutineTimeAndDays != null) {
            String hora = rutina.getHoraRecordatorio();
            holder.tvRoutineTimeAndDays.setText(
                    (hora == null || hora.trim().isEmpty())
                            ? "Sin hora"
                            : hora
            );
        }

        // Botón editar
        if (holder.btnEdit != null) {
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int adapterPos = holder.getAdapterPosition();
                    if (adapterPos != RecyclerView.NO_POSITION) {
                        listener.onEditClick(rutina, adapterPos);
                    }
                }
            });
        }

        // Botón eliminar
        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int adapterPos = holder.getAdapterPosition();
                    if (adapterPos != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(rutina, adapterPos);
                    }
                }
            });
        }

        // Click general del item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(rutina, adapterPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaRutinas != null ? listaRutinas.size() : 0;
    }

    /**
     * ViewHolder reutilizable para ambos layouts.
     */
    static class RutinaViewHolder
            extends RecyclerView.ViewHolder {

        // item_rutina (Dashboard)
        TextView txtNombre, txtHora, txtDescripcion;
        ImageView imgEstado;

        // item_routine_management (Gestión)
        TextView tvRoutineName, tvRoutineTimeAndDays;
        ImageButton btnEdit, btnDelete;

        RutinaViewHolder(
                @NonNull View itemView,
                Mode mode) {

            super(itemView);

            if (mode == Mode.DASHBOARD) {
                txtNombre =
                        itemView.findViewById(R.id.txtNombreRutina);
                txtHora =
                        itemView.findViewById(R.id.txtHoraRutina);
                txtDescripcion =
                        itemView.findViewById(R.id.txtDescripcionRutina);
                imgEstado =
                        itemView.findViewById(R.id.ivEstadoRutina);
            } else {
                tvRoutineName =
                        itemView.findViewById(R.id.tv_routine_name);
                tvRoutineTimeAndDays =
                        itemView.findViewById(
                                R.id.tv_routine_time_and_days
                        );
                btnEdit =
                        itemView.findViewById(
                                R.id.btn_edit_routine
                        );
                btnDelete =
                        itemView.findViewById(
                                R.id.btn_delete_routine
                        );
            }
        }
    }
}
