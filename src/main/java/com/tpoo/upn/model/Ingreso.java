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

    /** Constructor usado antes de insertar el ingreso en la base de datos. */
    public Ingreso(Cliente cliente, Membresia membresia, Usuario usuario, LocalDateTime fechaHora) {
        setCliente(cliente);
        setMembresia(membresia);
        setUsuario(usuario);
        setFechaHora(fechaHora);
    }

    /** Constructor usado al recuperar el ingreso desde la base de datos. */
    public Ingreso(int idIngreso, Cliente cliente, Membresia membresia, Usuario usuario,
            LocalDateTime fechaHora) {
        this(cliente, membresia, usuario, fechaHora);
        setIdIngreso(idIngreso);
    }

    public int getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(int idIngreso) {
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
