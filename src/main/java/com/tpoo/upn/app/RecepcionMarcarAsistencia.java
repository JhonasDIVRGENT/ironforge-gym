package com.tpoo.upn.app;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.IngresoController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/**
 * Caso: la recepcionista inicia sesion y marca la asistencia de un cliente
 * (RF-10, RF-04, RF-06, RF-07, RF-08).
 * Necesita una cuenta RECEPCIONISTA activa y clientes con alguna membresia
 * (por ejemplo, los de database/Insert.sql).
 * Solo guarda un ingreso, y unicamente si se confirma y el servicio lo autoriza.
 */
public class RecepcionMarcarAsistencia {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        // Una sola Sesion compartida por los servicios; cada controlador recibe su servicio.
        Sesion sesion = new Sesion();
        UsuarioController usuarioController = new UsuarioController(new UsuarioService(sesion));
        ClienteController clienteController = new ClienteController(new ClienteService(sesion));
        MembresiaController membresiaController = new MembresiaController(new MembresiaService(sesion));
        IngresoController ingresoController = new IngresoController(new IngresoService(sesion));

        System.out.println("=== IronForge Gym - Marcar asistencia ===");

        try {
            Usuario usuario = usuarioController.iniciarSesion(
                    Consola.leer("Username del recepcionista: "), Consola.leerClave("Contrasena"));
            System.out.println("Sesion iniciada: " + usuario.getNombreCompleto() + " (" + usuario.getRol() + ")");
            System.out.println();

            Cliente cliente = Consola.elegirCliente(clienteController.listarClientes());
            if (cliente == null) {
                return;
            }

            System.out.println();
            System.out.println("Cliente: " + cliente.getNombreCompleto() + " - DNI " + cliente.getDni());
            System.out.println("Membresias:");
            Consola.mostrarMembresias(membresiaController.consultarVigencia(cliente.getDni()));

            if (!Consola.confirmar("Registrar el ingreso de " + cliente.getNombreCompleto() + "?")) {
                System.out.println("No se registro ningun ingreso.");
                return;
            }
            registrarIngreso(ingresoController, cliente);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operacion no realizada: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioController.cerrarSesion();
            System.out.println("Sesion cerrada.");
        }
    }

    /** El servicio decide si el cliente puede ingresar; aqui solo se muestra el resultado. */
    private static void registrarIngreso(IngresoController ingresoController, Cliente cliente)
            throws SQLException {
        try {
            Ingreso ingreso = ingresoController.registrarIngreso(cliente.getDni());
            System.out.println("INGRESO REGISTRADO");
            System.out.println("   Cliente: " + ingreso.getCliente().getNombreCompleto());
            System.out.println("   Fecha y hora: " + ingreso.getFechaHora().format(FORMATO_FECHA_HORA));
            System.out.println("   Membresia usada: " + ingreso.getMembresia().getTipo().getNombre()
                    + " (vence " + ingreso.getMembresia().getFechaFin() + ")");
            System.out.println("   Registrado por: " + ingreso.getUsuario().getUsername());
        } catch (IllegalArgumentException e) {
            System.out.println("INGRESO RECHAZADO: " + e.getMessage() + ". No se guardo ningun registro.");
        }
    }
}
