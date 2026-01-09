package com.example.fitlifeapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlifeapp.R;
import com.example.fitlifeapp.model.Usuario;

import java.util.List;

/**
 * Adapter encargado de mostrar la lista de usuarios
 * en el panel de administración.
 */
public class UsuariosAdapter
        extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    /**
     * Listener para gestionar acciones prolongadas
     * sobre un usuario (opciones de administrador).
     */
    public interface OnUsuarioLongClickListener {
        void onLongClick(Usuario usuario);
    }

    private List<Usuario> listaUsuarios;
    private OnUsuarioLongClickListener listener;

    /**
     * Constructor del adapter.
     */
    public UsuariosAdapter(List<Usuario> listaUsuarios,
                           OnUsuarioLongClickListener listener) {

        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_usuario_admin,
                        parent,
                        false
                );

        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull UsuarioViewHolder holder,
            int position) {

        Usuario usuario = listaUsuarios.get(position);

        // Mostrar datos básicos del usuario
        holder.tvNombre.setText(usuario.getNombre());
        holder.tvEmail.setText(usuario.getEmail());

        int total = usuario.getTotalRutinas();
        holder.tvRutinas.setText(
                total + (total == 1 ? " rutina" : " rutinas")
        );

        // Long click para mostrar opciones de administrador
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(usuario);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null
                ? listaUsuarios.size()
                : 0;
    }

    /**
     * ViewHolder del item de usuario en el panel admin.
     */
    static class UsuarioViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNombre, tvEmail, tvRutinas;

        public UsuarioViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvNombre =
                    itemView.findViewById(R.id.tvNombreUsuario);
            tvEmail =
                    itemView.findViewById(R.id.tvEmailUsuario);
            tvRutinas =
                    itemView.findViewById(R.id.tvInfoRutinas);
        }
    }
}
