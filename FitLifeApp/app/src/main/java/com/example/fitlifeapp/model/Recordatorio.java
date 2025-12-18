package com.example.fitlifeapp.model;

public class Recordatorio {

    private String id;
    private String userId;
    private String titulo;
    private String hora;
    private String frecuencia;
    private String categoria;
    private String fechaCreado;
    private boolean activo;

    public Recordatorio() {
    }

    public Recordatorio(String id, String userId, String titulo, String hora, String frecuencia, String categoria, String fechaCreado, boolean activo) {
        this.id = id;
        this.userId = userId;
        this.titulo = titulo;
        this.hora = hora;
        this.frecuencia = frecuencia;
        this.categoria = categoria;
        this.fechaCreado = fechaCreado;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getHora() {
        return hora;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
