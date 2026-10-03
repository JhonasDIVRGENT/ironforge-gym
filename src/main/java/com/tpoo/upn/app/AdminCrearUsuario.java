package com.tpoo.upn.app;

import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

// Administrador: crea una cuenta nueva (queda activa). Guarda 1 cuenta.
public class AdminCrearUsuario {

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);

        System.out.println("=== IronForge Gym - Crear usuario ===");

        try {
            usuarioService.iniciarSesion(Consola.leer("Username del administrador: "),
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

            Usuario nuevo = new Usuario(nombres, apellidos, username, clave, rol);
            usuarioService.crearUsuario(nuevo);
            System.out.println("CUENTA CREADA: " + nuevo.getUsername() + " (" + nuevo.getNombreCompleto()
                    + ") como " + nuevo.getRol() + ", activa: " + nuevo.isActivo());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operacion no realizada: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioService.cerrarSesion();
            System.out.println("Sesion cerrada.");
        }
    }
}
