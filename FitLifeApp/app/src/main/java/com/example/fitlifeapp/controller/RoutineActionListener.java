package com.example.fitfeapp.controller;

import com.example.fitlifeapp.model.Rutina; // Asegúrate de que este path sea correcto

// Esta interfaz es lo que implementará tu Activity/Fragment
public interface RoutineActionListener {

    // Método para manejar el clic en el botón de borrar
    void onDeleteClick(Rutina rutina);

    // Método para manejar el clic en el botón de editar
    void onEditClick(Rutina rutina);

    // Opcional: si quieres manejar el clic en todo el elemento (item)
    // void onItemClick(Rutina rutina);
}