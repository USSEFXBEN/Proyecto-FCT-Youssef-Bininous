package com.example.fitlifeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.example.fitlifeapp.Rutina;

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {

    private List<Rutina> listaRutinas;
    private OnRutinaClickListener listener;

    // Constructor
    public RutinaAdapter(List<Rutina> listaRutinas, OnRutinaClickListener listener) {
        this.listaRutinas = listaRutinas;
        this.listener = listener;
    }

    // Interfaz para clicks
    public interface OnRutinaClickListener {
        void onRutinaClick(Rutina rutina);
    }

    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rutina, parent, false);
        return new RutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        Rutina rutina = listaRutinas.get(position);

        if (rutina != null) {
            holder.txtNombre.setText(rutina.getNombre());
            holder.txtHora.setText(rutina.getHora());
            holder.txtDescripcion.setText(rutina.getDescripcion());

            if (rutina.isCompletado()) {
                holder.imgEstado.setImageResource(R.drawable.ic_radio_button_on);
            } else {
                holder.imgEstado.setImageResource(R.drawable.ic_radio_button_off);
            }

            // Click listener
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRutinaClick(rutina);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaRutinas != null ? listaRutinas.size() : 0;
    }

    // ViewHolder
    public static class RutinaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtHora, txtDescripcion;
        ImageView imgEstado;

        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreRutina);
            txtHora = itemView.findViewById(R.id.txtHoraRutina);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionRutina);
//            imgEstado = itemView.findViewById(R.id.imgEstadoRutina);
        }
    }
}
