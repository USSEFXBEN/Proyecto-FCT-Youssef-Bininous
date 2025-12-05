package com.example.fitlifeapp.model;

import java.util.HashMap;
import java.util.Map;

public class Rutina {

    private String id;  // ID del documento en Firestore
    private String userId; // ID del usuario dueño de la rutina
    private String nombre;
    private String descripcion;
    private String horaRecordatorio;
    private Map<String, Boolean> diasActivos;

    // Constructor vacío requerido por Firestore
    public Rutina() {}

    // Constructor completo (útil si quieres crear rutinas con todos los datos)
    public Rutina(String id, String userId, String nombre, String descripcion,
                  String horaRecordatorio, Map<String, Boolean> diasActivos) {
        this.id = id;
        this.userId = userId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.horaRecordatorio = horaRecordatorio;
        this.diasActivos = diasActivos != null ? diasActivos : new HashMap<>();
    }

    // Constructor simple para usar en RoutinesFragment (nombre + userId)
    public Rutina(String nombre, String userId) {
        this.nombre = nombre;
        this.userId = userId;
        this.descripcion = "";
        this.horaRecordatorio = "";
        this.diasActivos = new HashMap<>();
    }

    // -------------------
    // GETTERS Y SETTERS
    // -------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getHoraRecordatorio() { return horaRecordatorio; }
    public void setHoraRecordatorio(String horaRecordatorio) { this.horaRecordatorio = horaRecordatorio; }

    public Map<String, Boolean> getDiasActivos() { return diasActivos; }
    public void setDiasActivos(Map<String, Boolean> diasActivos) {
        this.diasActivos = diasActivos != null ? diasActivos : new HashMap<>();
    }
}
