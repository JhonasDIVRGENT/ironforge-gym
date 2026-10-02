package com.tpoo.upn.controller;

import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.UsuarioService;
import java.sql.SQLException;

/**
 * Recibe las solicitudes de la presentacion sobre sesion y cuentas del personal
 * y las delega en UsuarioService, que comprueba credenciales y permisos.
 */
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        if (usuarioService == null) {
            throw new IllegalArgumentException("El servicio de usuarios es obligatorio");
        }
        this.usuarioService = usuarioService;
    }

    public Usuario iniciarSesion(String username, String password) throws SQLException {
        return usuarioService.iniciarSesion(username, password);
    }

    public void cerrarSesion() {
        usuarioService.cerrarSesion();
    }

    public boolean crearUsuario(Usuario usuario) throws SQLException {
        return usuarioService.crearUsuario(usuario);
    }

    public boolean cambiarEstadoUsuario(String username, boolean activo) throws SQLException {
        return usuarioService.cambiarEstadoUsuario(username, activo);
    }
}
