package com.example.fitlifeapp.model;

public class Progreso {

    private String userId;
    private String rutinaId;
    private String fecha;
    private String estado;

    // Constructor vacío (requerido por Firestore)
    public Progreso() {}

    // Getters
    public String getUserId() { return userId; }
    public String getRutinaId() { return rutinaId; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setRutinaId(String rutinaId) { this.rutinaId = rutinaId; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setEstado(String estado) { this.estado = estado; }
}
