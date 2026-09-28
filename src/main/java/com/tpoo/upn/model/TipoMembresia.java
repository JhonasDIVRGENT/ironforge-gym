package com.tpoo.upn.model;

/**
 * Tipo de membresia ofrecido por el gimnasio (mensual, trimestral, etc.).
 */
public class TipoMembresia {

    private int idTipo;
    private String nombre;
    private double precio;

    /** Constructor usado antes de insertar el tipo en la base de datos. */
    public TipoMembresia(String nombre, double precio) {
        setNombre(nombre);
        setPrecio(precio);
    }

    /** Constructor usado al recuperar el tipo desde la base de datos. */
    public TipoMembresia(int idTipo, String nombre, double precio) {
        this(nombre, precio);
        setIdTipo(idTipo);
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de membresia es obligatorio");
        }
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }
}
