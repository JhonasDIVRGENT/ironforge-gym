package com.tpoo.upn.app;

import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Demostracion de usuarios (RF-10, RF-13, RF-14, RF-16).
 * Necesita una cuenta ADMINISTRADOR activa y un username que todavia no exista.
 * Crea una cuenta de demostracion (RECEPCIONISTA), la desactiva y la vuelve a activar.
 * Solo cambia el estado de esa cuenta nueva; nunca el de la cuenta del administrador.
 */
public class PruebaUsuarios {

    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);

        System.out.println("=== Prueba de usuarios ===");
        System.out.println("Se creara una cuenta de demostracion nueva y se cambiara su estado.");

        try {
            String adminUsername = leer("Username del administrador: ");
            String adminClave = leerClave("Contrasena del administrador ");
            Usuario admin = usuarioService.iniciarSesion(adminUsername, adminClave);
            System.out.println("1. Sesion iniciada como " + admin.getRol());

            String demoUsername = leer("Username NUEVO para la cuenta de demostracion: ");
            String demoClave = leerClave("Contrasena para esa cuenta ");
            Usuario demo = new Usuario("Cuenta", "Demostracion", demoUsername, demoClave, Usuario.ROL_RECEPCIONISTA);
            usuarioService.crearUsuario(demo);
            System.out.println("2. Cuenta " + demoUsername + " creada como " + demo.getRol() + ", activa: " + demo.isActivo());

            usuarioService.cambiarEstadoUsuario(demoUsername, false);
            System.out.println("3. Cuenta " + demoUsername + " desactivada.");

            usuarioService.cerrarSesion();
            try {
                usuarioService.iniciarSesion(demoUsername, demoClave);
                System.out.println("4. FALLO: una cuenta inactiva inicio sesion.");
            } catch (IllegalArgumentException e) {
                System.out.println("4. Acceso rechazado como se esperaba: " + e.getMessage());
            }

            usuarioService.iniciarSesion(adminUsername, adminClave);
            usuarioService.cambiarEstadoUsuario(demoUsername, true);
            Usuario reactivada = usuarioService.iniciarSesion(demoUsername, demoClave);
            System.out.println("5. Cuenta reactivada: sesion iniciada como " + reactivada.getRol());

            // Un recepcionista no puede cambiar el estado de las cuentas (se intenta sobre la cuenta demo).
            try {
                usuarioService.cambiarEstadoUsuario(demoUsername, false);
                System.out.println("6. FALLO: un recepcionista cambio el estado de una cuenta.");
            } catch (IllegalStateException e) {
                System.out.println("6. Operacion sin permiso rechazada como se esperaba: " + e.getMessage());
            }

            usuarioService.cerrarSesion();
            System.out.println("7. Sesion cerrada. Hay sesion activa: " + sesion.haySesionActiva());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Demostracion detenida: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioService.cerrarSesion();
        }
    }

    private static String leer(String etiqueta) {
        System.out.print(etiqueta);
        return ENTRADA.nextLine().trim();
    }

    private static String leerClave(String etiqueta) {
        System.out.print(etiqueta + "(visible al escribir): ");
        return ENTRADA.nextLine();
    }
}
