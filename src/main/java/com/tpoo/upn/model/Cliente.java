package com.tpoo.upn.model;

/**
 * Cliente del gimnasio. Hereda nombres y apellidos de Persona.
 */
public class Cliente extends Persona {

    private int idCliente;
    private String dni;
    private String telefono;

    /**
     * Constructor usado antes de insertar el cliente en la base de datos.
     * Solo crea el objeto en memoria: no guarda nada en MySQL.
     * El idCliente queda en 0 porque MySQL todavia no le asigno un id.
     */
    public Cliente(String dni, String nombres, String apellidos, String telefono) {
        super(nombres, apellidos);
        setDni(dni);
        setTelefono(telefono);
    }

    /**
     * Constructor usado al recuperar el cliente desde la base de datos.
     * Tampoco consulta MySQL: solo recibe los datos que el DAO ya leyo.
     */
    public Cliente(int idCliente, String dni, String nombres, String apellidos, String telefono) {
        this(dni, nombres, apellidos, telefono);
        setIdCliente(idCliente);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        // 0 significa "aun sin id en la base de datos"; un id negativo no existe.
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
        // "\\d{8}" significa: exactamente 8 digitos (\\d = un digito, {8} = ocho veces).
        // matches exige que TODO el texto coincida, asi que "1234567a" o "123456789" fallan.
        if (!dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 digitos");
        }
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    /** El telefono es opcional, por eso puede quedar en null. */
    public void setTelefono(String telefono) {
        
        if (telefono != null && telefono.length() > 15) {
            throw new IllegalArgumentException("El telefono no puede pasar de 15 caracteres");
        }
        this.telefono = telefono;
    }
}
