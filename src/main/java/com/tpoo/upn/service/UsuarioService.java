package com.tpoo.upn.service;

import com.tpoo.upn.dao.UsuarioDAO;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Sesion sesion;

    public UsuarioService(Sesion sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesion es obligatoria");
        }
        this.sesion = sesion;
    }

    public Usuario iniciarSesion(String username, String password) throws SQLException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contrasena es obligatoria");
        }

        // Si este intento falla, no debe quedar activa la sesion anterior.
        sesion.cerrar();

        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        // Mismo mensaje en ambos casos para no revelar que usernames existen.
        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Usuario o contrasena incorrectos");
        }
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("La cuenta esta inactiva");
        }

        sesion.iniciar(usuario);
        return usuario;
    }

    public void cerrarSesion() {
        sesion.cerrar();
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

    public boolean cambiarEstadoUsuario(String username, boolean activo) throws SQLException {
        exigirAdministrador();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        usuario.setActivo(activo);
        return usuarioDAO.actualizar(usuario);
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
