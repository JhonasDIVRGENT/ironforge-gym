package com.tpoo.upn.model;

/**
 * Clase abstracta con los datos comunes de Cliente y Usuario.
 * Persona no es una tabla de la base de datos, por eso no tiene id.
 */
public abstract class Persona {

    private String nombres;
    private String apellidos;

    public Persona(String nombres, String apellidos) {
        // HU-01 CA-02: si faltan los dos datos se informa con un solo mensaje.
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
        // La columna nombres del SQL admite como maximo 100 caracteres.
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
        // La columna apellidos del SQL admite como maximo 100 caracteres.
        if (apellidos.length() > 100) {
            throw new IllegalArgumentException("Los apellidos no pueden pasar de 100 caracteres");
        }
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
