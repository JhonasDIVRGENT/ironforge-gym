package com.tpoo.upn.model;

import java.time.LocalDateTime;

/**
 * Registro del ingreso de un cliente al gimnasio.
 * Conoce al cliente, la membresia que autorizo el acceso
 * y el usuario del personal que registro el ingreso.
 */
public class Ingreso {

    private int idIngreso;
    private Cliente cliente;
    private Membresia membresia;
    private Usuario usuario;
    private LocalDateTime fechaHora;

    /**
     * Constructor usado antes de insertar el ingreso en la base de datos.
     * Solo crea el objeto en memoria: no guarda nada en MySQL.
     * El idIngreso queda en 0 porque MySQL todavia no le asigno un id.
     * Recibe la fechaHora, lo que permite reconstruir tambien ingresos historicos.
     */
    public Ingreso(Cliente cliente, Membresia membresia, Usuario usuario, LocalDateTime fechaHora) {
        setCliente(cliente);
        setMembresia(membresia);
        setUsuario(usuario);
        setFechaHora(fechaHora);
    }

    /**
     * Constructor usado al recuperar el ingreso desde la base de datos.
     * Tampoco consulta MySQL: solo recibe los datos que el DAO ya leyo.
     */
    public Ingreso(int idIngreso, Cliente cliente, Membresia membresia, Usuario usuario,
            LocalDateTime fechaHora) {
        this(cliente, membresia, usuario, fechaHora);
        setIdIngreso(idIngreso);
    }

    public int getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(int idIngreso) {
        // 0 significa "aun sin id en la base de datos"; un id negativo no existe.
        if (idIngreso < 0) {
            throw new IllegalArgumentException("El id del ingreso no puede ser negativo");
        }
        this.idIngreso = idIngreso;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El ingreso debe pertenecer a un cliente");
        }
        this.cliente = cliente;
    }

    public Membresia getMembresia() {
        return membresia;
    }

    public void setMembresia(Membresia membresia) {
        if (membresia == null) {
            throw new IllegalArgumentException("El ingreso debe estar autorizado por una membresia");
        }
        this.membresia = membresia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El ingreso debe ser registrado por un usuario");
        }
        this.usuario = usuario;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora del ingreso son obligatorias");
        }
        this.fechaHora = fechaHora;
    }
}
