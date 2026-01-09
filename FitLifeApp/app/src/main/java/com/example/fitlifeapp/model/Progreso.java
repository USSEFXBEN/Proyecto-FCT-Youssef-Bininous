package com.example.fitlifeapp.model;

/**
 * Modelo que representa el progreso de una rutina
 * en una fecha concreta para un usuario.
 * Se utiliza para almacenar el estado diario
 * (completado o pendiente) en Firestore.
 */
public class Progreso {

    private String userId;
    private String rutinaId;
    private String fecha;
    private String estado;

    /**
     * Constructor vacío requerido por Firebase Firestore
     * para poder convertir documentos en objetos.
     */
    public Progreso() {
    }

    // Getters

    /**
     * Devuelve el identificador del usuario.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Devuelve el identificador de la rutina.
     */
    public String getRutinaId() {
        return rutinaId;
    }

    /**
     * Devuelve la fecha del progreso (formato yyyy-MM-dd).
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Devuelve el estado de la rutina en esa fecha.
     * Ejemplo: "completado" o "pendiente".
     */
    public String getEstado() {
        return estado;
    }

    // Setters

    /**
     * Establece el identificador del usuario.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Establece el identificador de la rutina.
     */
    public void setRutinaId(String rutinaId) {
        this.rutinaId = rutinaId;
    }

    /**
     * Establece la fecha del progreso.
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Establece el estado de la rutina para esa fecha.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
