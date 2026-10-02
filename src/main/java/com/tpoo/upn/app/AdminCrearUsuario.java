package com.tpoo.upn.app;

import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

/**
 * Caso: el administrador crea una cuenta nueva del personal (RF-13).
 * Necesita una cuenta ADMINISTRADOR activa y un username que todavia no exista.
 * Guarda una cuenta nueva, que queda activa.
 */
public class AdminCrearUsuario {

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioController usuarioController = new UsuarioController(new UsuarioService(sesion));

        System.out.println("=== IronForge Gym - Crear usuario ===");

        try {
            usuarioController.iniciarSesion(Consola.leer("Username del administrador: "),
                    Consola.leerClave("Contrasena"));
            System.out.println();

            String nombres = Consola.leer("Nombres: ");
            String apellidos = Consola.leer("Apellidos: ");
            String username = Consola.leer("Username nuevo: ");
            String clave = Consola.leerClave("Contrasena de la cuenta nueva");

            System.out.println("Rol de la cuenta:");
            System.out.println("1. Recepcionista");
            System.out.println("2. Administrador");
            System.out.println("0. Cancelar");
            int opcion = Consola.leerOpcion(2);
            if (opcion == 0) {
                System.out.println("No se creo ninguna cuenta.");
                return;
            }
            String rol = opcion == 1 ? Usuario.ROL_RECEPCIONISTA : Usuario.ROL_ADMINISTRADOR;

            // El constructor de Usuario valida los datos; el servicio comprueba permiso y username unico.
            Usuario nuevo = new Usuario(nombres, apellidos, username, clave, rol);
            usuarioController.crearUsuario(nuevo);
            System.out.println("CUENTA CREADA: " + nuevo.getUsername() + " (" + nuevo.getNombreCompleto()
                    + ") como " + nuevo.getRol() + ", activa: " + nuevo.isActivo());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operacion no realizada: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioController.cerrarSesion();
            System.out.println("Sesion cerrada.");
        }
    }
}
