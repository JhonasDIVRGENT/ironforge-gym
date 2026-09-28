package com.tpoo.upn.model;

/**
 * Clase abstracta con los datos comunes de Cliente y Usuario.
 * Persona no es una tabla de la base de datos, por eso no tiene id.
 */
public abstract class Persona {

    private String nombres;
    private String apellidos;

    public Persona(String nombres, String apellidos) {
        setNombres(nombres);
        setApellidos(apellidos);
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
