package com.example.fitlifeapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;

public class RutinaViewHolder extends RecyclerView.ViewHolder {

    public TextView txtNombre;
    public TextView txtDescripcion;
    public TextView txtDias;
    public TextView txtHora;

    public RutinaViewHolder(View itemView) {
        super(itemView);

        txtNombre = itemView.findViewById(R.id.txtNombreRutina);
        txtDescripcion = itemView.findViewById(R.id.txtDescripcionRutina);
//        txtDias = itemView.findViewById(R.id.txtDiasActivos);
        txtHora = itemView.findViewById(R.id.txtHoraRutina);
    }
}
