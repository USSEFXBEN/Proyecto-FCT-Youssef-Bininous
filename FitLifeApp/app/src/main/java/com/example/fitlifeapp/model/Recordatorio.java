package com.example.fitlifeapp.model;

/**
 * Modelo que representa un recordatorio dentro de la aplicación.
 * Se utiliza tanto para almacenar la información en Firestore
 * como para programar notificaciones mediante AlarmManager.
 */
public class Recordatorio {

    private String id;
    private String userId;
    private String titulo;
    private String hora;
    private String frecuencia;
    private String categoria;
    private String fechaCreado;
    private boolean activo;

    /**
     * Constructor vacío requerido por Firebase Firestore.
     */
    public Recordatorio() {
    }

    /**
     * Constructor completo utilizado al crear un nuevo recordatorio.
     */
    public Recordatorio(String id,
                        String userId,
                        String titulo,
                        String hora,
                        String frecuencia,
                        String categoria,
                        String fechaCreado,
                        boolean activo) {

        this.id = id;
        this.userId = userId;
        this.titulo = titulo;
        this.hora = hora;
        this.frecuencia = frecuencia;
        this.categoria = categoria;
        this.fechaCreado = fechaCreado;
        this.activo = activo;
    }

    // Getters

    /**
     * Devuelve el identificador del recordatorio.
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve el identificador del usuario propietario.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Devuelve el título del recordatorio.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Devuelve la hora del recordatorio (formato HH:mm).
     */
    public String getHora() {
        return hora;
    }

    /**
     * Devuelve la frecuencia del recordatorio
     * (Diario, Semanal, Cada X horas, etc.).
     */
    public String getFrecuencia() {
        return frecuencia;
    }

    /**
     * Devuelve la categoría asociada al recordatorio.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Indica si el recordatorio está activo.
     */
    public boolean isActivo() {
        return activo;
    }

    // Setters

    /**
     * Establece el identificador del recordatorio.
     * Se usa normalmente tras obtener el ID del documento en Firestore.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Activa o desactiva el recordatorio.
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
