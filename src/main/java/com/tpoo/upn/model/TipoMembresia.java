package com.tpoo.upn.model;

/**
 * Tipo de membresia ofrecido por el gimnasio (mensual, trimestral, etc.).
 */
public class TipoMembresia {

    private int idTipo;
    private String nombre;
    private double precio;

    /**
     * Constructor usado antes de insertar el tipo en la base de datos.
     * Solo crea el objeto en memoria: no guarda nada en MySQL.
     * El idTipo queda en 0 porque MySQL todavia no le asigno un id.
     */
    public TipoMembresia(String nombre, double precio) {
        setNombre(nombre);
        setPrecio(precio);
    }

    /**
     * Constructor usado al recuperar el tipo desde la base de datos.
     * Tampoco consulta MySQL: solo recibe los datos que el DAO ya leyo.
     */
    public TipoMembresia(int idTipo, String nombre, double precio) {
        this(nombre, precio);
        setIdTipo(idTipo);
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        // 0 significa "aun sin id en la base de datos"; un id negativo no existe.
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
        // La columna nombre del SQL admite como maximo 50 caracteres.
        if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre del tipo no puede pasar de 50 caracteres");
        }
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        // Con double existen valores raros como NaN o infinito; MySQL no los puede guardar.
        if (Double.isNaN(precio) || Double.isInfinite(precio)) {
            throw new IllegalArgumentException("El precio debe ser un numero valido");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }
}
