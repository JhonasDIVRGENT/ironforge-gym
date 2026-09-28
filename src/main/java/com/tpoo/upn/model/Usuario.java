package com.tpoo.upn.model;

/**
 * Usuario del personal que opera el sistema.
 * Hereda nombres y apellidos de Persona.
 */
public class Usuario extends Persona {

    public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";
    public static final String ROL_RECEPCIONISTA = "RECEPCIONISTA";

    private int idUsuario;
    private String username;
    private String password;
    private String rol;
    private boolean activo;

    /** Constructor usado antes de insertar el usuario en la base de datos. */
    public Usuario(String nombres, String apellidos, String username, String password, String rol) {
        super(nombres, apellidos);
        setUsername(username);
        setPassword(password);
        setRol(rol);
        setActivo(true);
    }

    /** Constructor usado al recuperar el usuario desde la base de datos. */
    public Usuario(int idUsuario, String nombres, String apellidos, String username,
            String password, String rol, boolean activo) {
        this(nombres, apellidos, username, password, rol);
        setIdUsuario(idUsuario);
        setActivo(activo);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contrasena es obligatoria");
        }
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        String rolMayusculas = rol.trim().toUpperCase();
        if (!rolMayusculas.equals(ROL_ADMINISTRADOR) && !rolMayusculas.equals(ROL_RECEPCIONISTA)) {
            throw new IllegalArgumentException("El rol debe ser ADMINISTRADOR o RECEPCIONISTA");
        }
        this.rol = rolMayusculas;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean esAdministrador() {
        return rol.equals(ROL_ADMINISTRADOR);
    }

    public boolean esRecepcionista() {
        return rol.equals(ROL_RECEPCIONISTA);
    }
}
