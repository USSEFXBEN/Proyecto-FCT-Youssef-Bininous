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

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {

    public enum Mode {
        DASHBOARD,      // Usa item_rutina (lista simple con icono de estado)
        MANAGEMENT      // Usa item_routine_management (nombre + hora + días + lápiz + borrar)
    }

    private final List<Rutina> listaRutinas;
    private final Map<String, String> progresoMap; // solo se usa en DASHBOARD
    private final Mode mode;
    private final RoutineItemListener listener;

    // Listener genérico para ambos modos
    public interface RoutineItemListener {
        void onItemClick(Rutina rutina, int position);    // click en todo el item (Dashboard)
        void onEditClick(Rutina rutina, int position);    // botón lápiz (Management)
        void onDeleteClick(Rutina rutina, int position);  // botón borrar (Management)
    }

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
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (mode == Mode.DASHBOARD) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rutina, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_routine_management, parent, false);
        }
        return new RutinaViewHolder(view, mode);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        Rutina rutina = listaRutinas.get(position);
        if (rutina == null) return;

        if (mode == Mode.DASHBOARD) {
            bindDashboardItem(holder, rutina, position);
        } else {
            bindManagementItem(holder, rutina, position);
        }
    }

    private void bindDashboardItem(@NonNull RutinaViewHolder holder, Rutina rutina, int position) {
        // Campos de item_rutina.xml
        if (holder.txtNombre != null) {
            holder.txtNombre.setText(
                    rutina.getNombre() != null ? rutina.getNombre() : ""
            );
        }

        if (holder.txtHora != null) {
            String hora = rutina.getHoraRecordatorio();
            if (hora == null || hora.trim().isEmpty()) {
                hora = "Sin hora";
            }
            holder.txtHora.setText(hora);
        }

        if (holder.txtDescripcion != null) {
            String descripcion = rutina.getDescripcion();
            if (descripcion == null || descripcion.trim().isEmpty()) {
                descripcion = "Sin descripción";
            }
            holder.txtDescripcion.setText(descripcion);
        }

        if (holder.imgEstado != null && progresoMap != null) {
            String estado = progresoMap.get(rutina.getId());
            if ("completado".equalsIgnoreCase(estado)) {
                holder.imgEstado.setImageResource(R.drawable.ic_radio_button_on);
            } else {
                holder.imgEstado.setImageResource(R.drawable.ic_radio_button_off);
            }
        }

        // Click en todo el item (para Dashboard → marcar/desmarcar rutina)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(rutina, adapterPos);
                }
            }
        });
    }

    private void bindManagementItem(@NonNull RutinaViewHolder holder, Rutina rutina, int position) {
        // Campos de item_routine_management.xml
        if (holder.tvRoutineName != null) {
            holder.tvRoutineName.setText(
                    rutina.getNombre() != null ? rutina.getNombre() : ""
            );
        }

        if (holder.tvRoutineTimeAndDays != null) {
            String hora = rutina.getHoraRecordatorio();
            if (hora == null || hora.trim().isEmpty()) {
                hora = "Sin hora";
            }

            // Si tienes un campo diasActivos en Rutina, aquí lo puedes formatear bonito.
            // De momento, solo mostramos la hora:
            String texto = hora;
            holder.tvRoutineTimeAndDays.setText(texto);
        }

        // Botón Editar (lápiz)
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

        // Botón Borrar
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

        // Si quieres que clicar en la tarjeta haga algo:
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

    static class RutinaViewHolder extends RecyclerView.ViewHolder {

        // item_rutina (Dashboard)
        TextView txtNombre, txtHora, txtDescripcion;
        ImageView imgEstado;

        // item_routine_management (Gestión)
        TextView tvRoutineName, tvRoutineTimeAndDays;
        ImageButton btnEdit, btnDelete;

        RutinaViewHolder(@NonNull View itemView, Mode mode) {
            super(itemView);

            if (mode == Mode.DASHBOARD) {
                txtNombre = itemView.findViewById(R.id.txtNombreRutina);
                txtHora = itemView.findViewById(R.id.txtHoraRutina);
                txtDescripcion = itemView.findViewById(R.id.txtDescripcionRutina);
                imgEstado = itemView.findViewById(R.id.ivEstadoRutina);
            } else {
                tvRoutineName = itemView.findViewById(R.id.tv_routine_name);
                tvRoutineTimeAndDays = itemView.findViewById(R.id.tv_routine_time_and_days);
                btnEdit = itemView.findViewById(R.id.btn_edit_routine);
                btnDelete = itemView.findViewById(R.id.btn_delete_routine);
            }
        }
    }
}
