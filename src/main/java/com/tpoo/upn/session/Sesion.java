package com.tpoo.upn.session;

import com.tpoo.upn.model.Usuario;

// Guarda el usuario autenticado. No consulta la base ni revisa contrasenas.
public class Sesion {

    private Usuario usuarioActual;

    public void iniciar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("No se puede iniciar sesion sin un usuario");
        }
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario esta inactivo y no puede iniciar sesion");
        }
        this.usuarioActual = usuario;
    }

    public void cerrar() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public boolean esAdministrador() {
        return usuarioActual != null && usuarioActual.esAdministrador();
    }

    public boolean esRecepcionista() {
        return usuarioActual != null && usuarioActual.esRecepcionista();
    }
}
