package com.tpoo.upn.controller;

import com.tpoo.upn.dao.UsuarioDAO;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

/**
 * Reglas de negocio de usuarios: autenticacion , creacion 
 * y activacion o desactivacion de cuentas .
 */
public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Sesion sesion;

    public UsuarioController(Sesion sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesion es obligatoria");
        }
        this.sesion = sesion;
    }

    /** Unica operacion que puede ejecutarse sin sesion previa. */
    public Usuario iniciarSesion(String username, String password) throws SQLException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contrasena es obligatoria");
        }

        // Se cierra primero: si este intento falla, no debe quedar activa la sesion anterior.
        sesion.cerrar();

        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        // Mismo mensaje para usuario inexistente y contrasena incorrecta:
        // asi no se revela cuales usernames existen.
        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Usuario o contrasena incorrectos");
        }
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("La cuenta esta inactiva");
        }

        sesion.iniciar(usuario);
        return usuario;
    }

    public boolean crearUsuario(Usuario usuario) throws SQLException {
        exigirAdministrador();
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (usuarioDAO.existeUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El username ya esta registrado");
        }
        usuario.setActivo(true);
        return usuarioDAO.insertar(usuario);
    }

    public boolean cambiarEstadoUsuario(Usuario usuario, boolean activo) throws SQLException {
        exigirAdministrador();
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        Usuario guardado = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (guardado == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        if (guardado.getIdUsuario() != usuario.getIdUsuario()) {
            throw new IllegalArgumentException("El usuario no corresponde al registro guardado");
        }

        guardado.setActivo(activo);
        boolean actualizado = usuarioDAO.actualizar(guardado);
        if (actualizado) {
            usuario.setActivo(activo);
        }
        return actualizado;
    }

    private void exigirAdministrador() {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        if (!sesion.esAdministrador()) {
            throw new IllegalStateException("Solo el administrador puede realizar esta operacion");
        }
    }
}
