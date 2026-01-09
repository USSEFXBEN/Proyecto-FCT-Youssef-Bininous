package com.example.fitlifeapp.model;

import java.util.Date;

/**
 * Modelo que representa un usuario de la aplicación.
 * Se utiliza para almacenar información básica del usuario
 * en la colección "users" de Firestore.
 */
public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private String rol;
    private Date createdAt;

    // Número total de rutinas creadas por el usuario
    private int totalRutinas;

    /**
     * Constructor vacío requerido por Firebase Firestore.
     */
    public Usuario() {
    }

    // Getters

    /**
     * Devuelve el identificador del usuario.
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve el nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el email del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Devuelve el rol del usuario (admin o usuario).
     */
    public String getRol() {
        return rol;
    }

    /**
     * Devuelve la fecha de creación de la cuenta.
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Devuelve el número total de rutinas del usuario.
     */
    public int getTotalRutinas() {
        return totalRutinas;
    }

    // Setters

    /**
     * Establece el identificador del usuario.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Establece el nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Establece el email del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Establece el rol del usuario.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Establece la fecha de creación de la cuenta.
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Establece el número total de rutinas del usuario.
     */
    public void setTotalRutinas(int totalRutinas) {
        this.totalRutinas = totalRutinas;
    }
}
