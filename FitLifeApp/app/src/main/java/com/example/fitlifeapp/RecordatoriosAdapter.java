package com.example.fitlifeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.model.Recordatorio;

import java.util.List;

public class RecordatoriosAdapter extends RecyclerView.Adapter<RecordatoriosAdapter.ViewHolder> {

    public interface Listener {
        void onSwitchChanged(Recordatorio r, boolean activo);
        void onDelete(Recordatorio r);
    }

    private List<Recordatorio> lista;
    private Listener listener;

    public RecordatoriosAdapter(List<Recordatorio> lista, Listener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recordatorio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Recordatorio r = lista.get(position);

        holder.tvTitulo.setText(r.getTitulo());
        holder.tvSubtitulo.setText(r.getHora() + "   ·   " + r.getFrecuencia());
        holder.tvCategoria.setText(r.getCategoria());

        holder.icono.setAlpha(r.isActivo() ? 1f : 0.3f);

        holder.switchActivo.setChecked(r.isActivo());
        holder.switchActivo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onSwitchChanged(r, isChecked);
            holder.icono.setAlpha(isChecked ? 1f : 0.3f);
        });

        holder.btnEliminar.setOnClickListener(v -> listener.onDelete(r));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
