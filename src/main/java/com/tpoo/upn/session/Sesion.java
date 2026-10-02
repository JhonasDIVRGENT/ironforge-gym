package com.tpoo.upn.session;

import com.tpoo.upn.model.Usuario;

/**
 * Representa al usuario autenticado en la aplicacion.
 * No consulta la base de datos ni comprueba contrasenas: eso lo hace UsuarioService
 * antes de llamar a iniciar(). Una misma instancia se comparte entre los cuatro servicios.
 */
public class Sesion {

    private Usuario usuarioActual;

    public void iniciar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("No se puede iniciar sesion sin un usuario");
        }
        // Solo se revisa el objeto recibido; si el usuario se desactiva despues en la
        // base de datos, Sesion no se entera.
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario esta inactivo y no puede iniciar sesion");
        }
        this.usuarioActual = usuario;
    }

    /** Cierra la sesion. Se puede llamar aunque no hubiera ninguna abierta. */
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
        
        if (usuarioActual == null) {
            return false;
        }
        return usuarioActual.esAdministrador();
    }

    public boolean esRecepcionista() {
        if (usuarioActual == null) {
            return false;
        }
        return usuarioActual.esRecepcionista();
    }
}
