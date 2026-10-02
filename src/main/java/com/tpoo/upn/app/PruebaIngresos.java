package com.tpoo.upn.app;

import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Demostracion de ingresos (RF-06, RF-07, RF-08).
 * Necesita una cuenta RECEPCIONISTA activa y dos clientes existentes:
 * uno SIN membresia vigente (vencida, aun no vigente o sin membresias) y
 * otro CON membresia vigente, por ejemplo el usado en PruebaMembresias.
 * Solo el segundo deja un ingreso nuevo en la base.
 */
public class PruebaIngresos {

    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        ClienteService clienteService = new ClienteService(sesion);
        IngresoService ingresoService = new IngresoService(sesion);

        System.out.println("=== Prueba de ingresos ===");

        try {
            Usuario usuario = usuarioService.iniciarSesion(leer("Username del recepcionista: "), leerClave());

            // Se comprueba que el cliente exista para que el rechazo solo pueda venir de la membresia.
            String dniSinVigente = leer("DNI de un cliente SIN membresia vigente: ");
            if (clienteService.buscarCliente(dniSinVigente) == null) {
                System.out.println("Cliente no encontrado; indique un cliente registrado.");
                return;
            }
            try {
                ingresoService.registrarIngreso(dniSinVigente);
                System.out.println("1. FALLO: se registro un ingreso sin membresia vigente.");
            } catch (IllegalArgumentException e) {
                System.out.println("1. Ingreso rechazado como se esperaba: " + e.getMessage());
            }

            String dniConVigente = leer("DNI de un cliente CON membresia vigente: ");
            LocalDateTime antes = LocalDateTime.now();
            Ingreso ingreso = ingresoService.registrarIngreso(dniConVigente);
            LocalDateTime despues = LocalDateTime.now();
            System.out.println("2. Ingreso autorizado con id " + ingreso.getIdIngreso() + " para "
                    + ingreso.getCliente().getNombreCompleto() + ", membresia "
                    + ingreso.getMembresia().getIdMembresia() + " (vence " + ingreso.getMembresia().getFechaFin() + ")");

            // El servicio debe haber tomado el usuario de la Sesion y la hora del reloj del sistema.
            boolean mismoUsuario = ingreso.getUsuario().getUsername().equals(usuario.getUsername());
            boolean horaDelSistema = !ingreso.getFechaHora().isBefore(antes) && !ingreso.getFechaHora().isAfter(despues);
            System.out.println("3. Registrado por " + ingreso.getUsuario().getUsername()
                    + (mismoUsuario ? " (usuario de la sesion)" : " FALLO: no es el usuario de la sesion"));
            System.out.println("   Fecha y hora " + ingreso.getFechaHora().withNano(0)
                    + (horaDelSistema ? " (reloj del sistema)" : " FALLO: no coincide con el reloj"));
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

    private static String leerClave() {
        System.out.print("Contrasena (visible al escribir): ");
        return ENTRADA.nextLine();
    }
}
