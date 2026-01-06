package com.example.fitlifeapp.model;

import java.util.Date;

public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private String rol;
    private Date createdAt;

    // 🔥 NUEVO
    private int totalRutinas;

    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getTotalRutinas() {
        return totalRutinas;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalRutinas(int totalRutinas) {
        this.totalRutinas = totalRutinas;
    }
}
