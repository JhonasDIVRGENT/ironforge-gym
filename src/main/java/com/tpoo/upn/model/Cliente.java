package com.tpoo.upn.model;

public class Cliente extends Persona {

    private int idCliente;
    private String dni;
    private String telefono;

    // Cliente nuevo, aun sin id.
    public Cliente(String dni, String nombres, String apellidos, String telefono) {
        super(nombres, apellidos);
        setDni(dni);
        setTelefono(telefono);
    }

    // Cliente leido de la base de datos.
    public Cliente(int idCliente, String dni, String nombres, String apellidos, String telefono) {
        this(dni, nombres, apellidos, telefono);
        setIdCliente(idCliente);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        if (idCliente < 0) {
            throw new IllegalArgumentException("El id del cliente no puede ser negativo");
        }
        this.idCliente = idCliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        // Exactamente 8 digitos.
        if (!dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 digitos");
        }
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    // El telefono es opcional (puede ser null).
    public void setTelefono(String telefono) {
        if (telefono != null && telefono.length() > 15) {
            throw new IllegalArgumentException("El telefono no puede pasar de 15 caracteres");
        }
        this.telefono = telefono;
    }
}
