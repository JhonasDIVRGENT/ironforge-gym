package com.tpoo.upn.model;

// Datos comunes de Cliente y Usuario. No es una tabla de la base de datos.
public abstract class Persona {

    private String nombres;
    private String apellidos;

    public Persona(String nombres, String apellidos) {
        if ((nombres == null || nombres.isBlank()) && (apellidos == null || apellidos.isBlank())) {
            throw new IllegalArgumentException("Los nombres y apellidos son obligatorios");
        }
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
        if (nombres.length() > 100) {
            throw new IllegalArgumentException("Los nombres no pueden pasar de 100 caracteres");
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
        if (apellidos.length() > 100) {
            throw new IllegalArgumentException("Los apellidos no pueden pasar de 100 caracteres");
        }
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
