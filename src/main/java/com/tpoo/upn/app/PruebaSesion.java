package com.tpoo.upn.app;

import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.session.Sesion;

// Prueba Sesion en memoria, sin MySQL.
public class PruebaSesion {

    public static void main(String[] args) {
        Sesion sesion = new Sesion();

        System.out.println("1. Sesion recien creada");
        System.out.println("   haySesionActiva = " + sesion.haySesionActiva());
        System.out.println("   getUsuarioActual = " + sesion.getUsuarioActual());
        System.out.println("   esAdministrador = " + sesion.esAdministrador());
        System.out.println("   esRecepcionista = " + sesion.esRecepcionista());

        Usuario admin = new Usuario("Carlos", "Ramirez", "admin01", "clave", Usuario.ROL_ADMINISTRADOR);
        sesion.iniciar(admin);
        System.out.println("2. Sesion con administrador activo");
        System.out.println("   haySesionActiva = " + sesion.haySesionActiva());
        System.out.println("   usuario = " + sesion.getUsuarioActual().getNombreCompleto());
        System.out.println("   esAdministrador = " + sesion.esAdministrador());
        System.out.println("   esRecepcionista = " + sesion.esRecepcionista());

        Usuario recepcionista = new Usuario("Lucia", "Torres", "recep01", "clave", Usuario.ROL_RECEPCIONISTA);
        sesion.iniciar(recepcionista);
        System.out.println("3. Sesion con recepcionista activo");
        System.out.println("   usuario = " + sesion.getUsuarioActual().getNombreCompleto());
        System.out.println("   esAdministrador = " + sesion.esAdministrador());
        System.out.println("   esRecepcionista = " + sesion.esRecepcionista());

        sesion.cerrar();
        System.out.println("4. Despues de cerrar");
        System.out.println("   haySesionActiva = " + sesion.haySesionActiva());
        System.out.println("   getUsuarioActual = " + sesion.getUsuarioActual());

        sesion.cerrar();
        System.out.println("5. cerrar() otra vez sin sesion: no falla");

        System.out.println("6. iniciar(null)");
        try {
            sesion.iniciar(null);
            System.out.println("   ERROR: deberia haber rechazado el null");
        } catch (IllegalArgumentException e) {
            System.out.println("   rechazado: " + e.getMessage());
        }

        Usuario inactivo = new Usuario("Mario", "Lopez", "recep02", "clave", Usuario.ROL_RECEPCIONISTA);
        inactivo.setActivo(false);
        System.out.println("7. iniciar(usuario inactivo)");
        try {
            sesion.iniciar(inactivo);
            System.out.println("   ERROR: deberia haber rechazado al inactivo");
        } catch (IllegalArgumentException e) {
            System.out.println("   rechazado: " + e.getMessage());
        }

        System.out.println("8. Tras los rechazos la sesion sigue vacia");
        System.out.println("   haySesionActiva = " + sesion.haySesionActiva());
        System.out.println("   esAdministrador = " + sesion.esAdministrador()
                + " | esRecepcionista = " + sesion.esRecepcionista() + " (sin NullPointerException)");
    }
}
