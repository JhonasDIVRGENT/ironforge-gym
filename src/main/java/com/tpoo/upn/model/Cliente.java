package com.tpoo.upn.model;

/**
 * Cliente del gimnasio. Hereda nombres y apellidos de Persona.
 */
public class Cliente extends Persona {

    private int idCliente;
    private String dni;
    private String telefono;

    /** Constructor usado antes de insertar el cliente en la base de datos. */
    public Cliente(String dni, String nombres, String apellidos, String telefono) {
        super(nombres, apellidos);
        setDni(dni);
        setTelefono(telefono);
    }

    /** Constructor usado al recuperar el cliente desde la base de datos. */
    public Cliente(int idCliente, String dni, String nombres, String apellidos, String telefono) {
        this(dni, nombres, apellidos, telefono);
        setIdCliente(idCliente);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    /** El telefono es opcional, por eso puede quedar en null. */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
