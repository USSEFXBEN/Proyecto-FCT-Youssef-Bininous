package com.example.fitlifeapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.model.Recordatorio;

import java.util.List;

/**
 * Adapter para mostrar la lista de recordatorios en un RecyclerView.
 * Se encarga de enlazar los datos del modelo Recordatorio
 * con el layout item_recordatorio.
 */
public class RecordatoriosAdapter
        extends RecyclerView.Adapter<RecordatoriosAdapter.ViewHolder> {

    /**
     * Interfaz que permite comunicar eventos del Adapter
     * al Fragment que lo utiliza.
     */
    public interface Listener {

        /**
         * Se llama cuando se activa o desactiva un recordatorio.
         */
        void onSwitchChanged(Recordatorio r, boolean activo);

        /**
         * Se llama cuando se solicita eliminar un recordatorio.
         */
        void onDelete(Recordatorio r);
    }

    private List<Recordatorio> lista;
    private Listener listener;

    /**
     * Constructor del adapter.
     */
    public RecordatoriosAdapter(
            List<Recordatorio> lista,
            Listener listener) {

        this.lista = lista;
        this.listener = listener;
    }

    /**
     * ViewHolder que mantiene las referencias a las vistas
     * de cada item del RecyclerView.
     */
    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView icono;
        TextView tvTitulo, tvSubtitulo, tvCategoria;
        Switch switchActivo;
        ImageButton btnEliminar;

        public ViewHolder(View v) {
            super(v);

            icono = v.findViewById(R.id.icono);
            tvTitulo = v.findViewById(R.id.tvTitulo);
            tvSubtitulo = v.findViewById(R.id.tvSubtitulo);
            tvCategoria = v.findViewById(R.id.tvCategoria);
            switchActivo = v.findViewById(R.id.switchActivo);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_recordatorio,
                        parent,
                        false
                );

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Recordatorio r = lista.get(position);

        // Mostrar datos del recordatorio
        holder.tvTitulo.setText(r.getTitulo());
        holder.tvSubtitulo.setText(
                r.getHora() + " · " + r.getFrecuencia()
        );
        holder.tvCategoria.setText(r.getCategoria());

        // Cambiar opacidad del icono según si está activo
        holder.icono.setAlpha(
                r.isActivo() ? 1f : 0.3f
        );

        // Estado del switch
        holder.switchActivo.setChecked(r.isActivo());
        holder.switchActivo.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    listener.onSwitchChanged(r, isChecked);
                    holder.icono.setAlpha(
                            isChecked ? 1f : 0.3f
                    );
                }
        );

        // Botón eliminar
        holder.btnEliminar.setOnClickListener(
                v -> listener.onDelete(r)
        );
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
