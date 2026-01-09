package com.example.fitlifeapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Modelo que representa una rutina del usuario.
 * Una rutina puede tener una hora de recordatorio
 * y varios días de la semana activos.
 */
public class Rutina {

    // Identificador del documento en Firestore
    private String id;

    // Identificador del usuario propietario de la rutina
    private String userId;

    // Nombre y descripción de la rutina
    private String nombre;
    private String descripcion;

    // Hora asociada al recordatorio (formato HH:mm)
    private String horaRecordatorio;

    // Días de la semana en los que la rutina está activa
    // Ejemplo: lunes=true, martes=false, etc.
    private Map<String, Boolean> diasActivos;

    /**
     * Constructor vacío requerido por Firebase Firestore
     * para convertir documentos en objetos Java.
     */
    public Rutina() {
    }

    /**
     * Constructor completo utilizado al crear o recuperar
     * una rutina con todos sus datos.
     */
    public Rutina(String id,
                  String userId,
                  String nombre,
                  String descripcion,
                  String horaRecordatorio,
                  Map<String, Boolean> diasActivos) {

        this.id = id;
        this.userId = userId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.horaRecordatorio = horaRecordatorio;
        this.diasActivos =
                diasActivos != null ? diasActivos : new HashMap<>();
    }

    /**
     * Constructor simplificado utilizado en algunos casos
     * donde solo es necesario el nombre y el usuario.
     */
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

    /**
     * Devuelve el ID de la rutina.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el ID de la rutina.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Devuelve el ID del usuario propietario.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Establece el ID del usuario propietario.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Devuelve el nombre de la rutina.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la rutina.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la descripción de la rutina.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la rutina.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve la hora del recordatorio asociada a la rutina.
     */
    public String getHoraRecordatorio() {
        return horaRecordatorio;
    }

    /**
     * Establece la hora del recordatorio.
     */
    public void setHoraRecordatorio(String horaRecordatorio) {
        this.horaRecordatorio = horaRecordatorio;
    }

    /**
     * Devuelve el mapa de días activos de la rutina.
     */
    public Map<String, Boolean> getDiasActivos() {
        return diasActivos;
    }

    /**
     * Establece los días activos de la rutina.
     * Si el mapa es nulo, se inicializa vacío.
     */
    public void setDiasActivos(Map<String, Boolean> diasActivos) {
        this.diasActivos =
                diasActivos != null ? diasActivos : new HashMap<>();
    }
}
