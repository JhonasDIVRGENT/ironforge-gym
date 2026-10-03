package com.tpoo.upn.model;

public class TipoMembresia {

    private int idTipo;
    private String nombre;
    private double precio;

    // Tipo nuevo, aun sin id.
    public TipoMembresia(String nombre, double precio) {
        setNombre(nombre);
        setPrecio(precio);
    }

    // Tipo leido de la base de datos.
    public TipoMembresia(int idTipo, String nombre, double precio) {
        this(nombre, precio);
        setIdTipo(idTipo);
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        if (idTipo < 0) {
            throw new IllegalArgumentException("El id del tipo de membresia no puede ser negativo");
        }
        this.idTipo = idTipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de membresia es obligatorio");
        }
        if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre del tipo no puede pasar de 50 caracteres");
        }
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (Double.isNaN(precio) || Double.isInfinite(precio)) {
            throw new IllegalArgumentException("El precio debe ser un numero valido");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }
}
