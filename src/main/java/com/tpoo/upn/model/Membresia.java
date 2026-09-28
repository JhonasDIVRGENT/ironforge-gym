package com.tpoo.upn.model;

import java.time.LocalDate;

/**
 * Membresia de un cliente durante un periodo determinado.
 * El estado no se guarda: se calcula comparando una fecha con el periodo.
 */
public class Membresia {

    public static final String AUN_NO_VIGENTE = "AUN_NO_VIGENTE";
    public static final String VIGENTE = "VIGENTE";
    public static final String VENCIDA = "VENCIDA";

    private int idMembresia;
    private Cliente cliente;
    private TipoMembresia tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    /** Constructor usado antes de insertar la membresia en la base de datos. */
    public Membresia(Cliente cliente, TipoMembresia tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de la membresia son obligatorias");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser menor a la fecha de inicio");
        }
        setCliente(cliente);
        setTipo(tipo);
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    /** Constructor usado al recuperar la membresia desde la base de datos. */
    public Membresia(int idMembresia, Cliente cliente, TipoMembresia tipo,
            LocalDate fechaInicio, LocalDate fechaFin) {
        this(cliente, tipo, fechaInicio, fechaFin);
        setIdMembresia(idMembresia);
    }

    public int getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("La membresia debe pertenecer a un cliente");
        }
        this.cliente = cliente;
    }

    public TipoMembresia getTipo() {
        return tipo;
    }

    public void setTipo(TipoMembresia tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("La membresia debe tener un tipo");
        }
        this.tipo = tipo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser menor a la fecha de inicio");
        }
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        if (fechaFin == null) {
            throw new IllegalArgumentException("La fecha de fin es obligatoria");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser menor a la fecha de inicio");
        }
        this.fechaFin = fechaFin;
    }

    /** La membresia esta vigente si la fecha esta dentro del periodo, incluyendo los extremos. */
    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    /** Devuelve AUN_NO_VIGENTE, VIGENTE o VENCIDA segun la fecha consultada. */
    public String obtenerEstado(LocalDate fecha) {
        if (fecha.isBefore(fechaInicio)) {
            return AUN_NO_VIGENTE;
        }
        if (fecha.isAfter(fechaFin)) {
            return VENCIDA;
        }
        return VIGENTE;
    }
}
