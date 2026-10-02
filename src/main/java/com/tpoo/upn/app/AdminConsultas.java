package com.tpoo.upn.app;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.IngresoController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Caso: el administrador consulta las membresias proximas a vencer (RF-11)
 * o el historial de ingresos de un cliente (RF-09).
 * Necesita una cuenta ADMINISTRADOR activa. Solo lee datos.
 */
public class AdminConsultas {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioController usuarioController = new UsuarioController(new UsuarioService(sesion));
        ClienteController clienteController = new ClienteController(new ClienteService(sesion));
        MembresiaController membresiaController = new MembresiaController(new MembresiaService(sesion));
        IngresoController ingresoController = new IngresoController(new IngresoService(sesion));

        System.out.println("=== IronForge Gym - Consultas del administrador ===");

        try {
            usuarioController.iniciarSesion(Consola.leer("Username del administrador: "),
                    Consola.leerClave("Contrasena"));
            System.out.println();

            System.out.println("1. Membresias proximas a vencer (proximos 7 dias)");
            System.out.println("2. Historial de ingresos de un cliente");
            System.out.println("0. Salir");

            switch (Consola.leerOpcion(2)) {
                case 1 -> mostrarPorVencer(membresiaController);
                case 2 -> mostrarHistorial(clienteController, ingresoController);
                default -> System.out.println("Sin consultas.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operacion no realizada: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioController.cerrarSesion();
            System.out.println("Sesion cerrada.");
        }
    }

    private static void mostrarPorVencer(MembresiaController membresiaController) throws SQLException {
        List<Membresia> porVencer = membresiaController.listarPorVencer();
        System.out.println("Membresias vigentes que vencen en los proximos 7 dias:");
        if (porVencer.isEmpty()) {
            System.out.println("   No existen membresias proximas a vencer.");
        }
        for (Membresia m : porVencer) {
            System.out.println("   " + m.getCliente().getNombreCompleto() + " - DNI " + m.getCliente().getDni()
                    + " | " + m.getTipo().getNombre() + " | vence " + m.getFechaFin());
        }
    }

    private static void mostrarHistorial(ClienteController clienteController,
            IngresoController ingresoController) throws SQLException {
        Cliente cliente = Consola.elegirCliente(clienteController.listarClientes());
        if (cliente == null) {
            return;
        }
        List<Ingreso> historial = ingresoController.consultarHistorial(cliente.getDni());
        System.out.println("Historial de " + cliente.getNombreCompleto() + " (" + historial.size() + " ingresos):");
        if (historial.isEmpty()) {
            System.out.println("   No existen ingresos registrados para este cliente.");
        }
        for (Ingreso i : historial) {
            System.out.println("   " + i.getFechaHora().format(FORMATO_FECHA_HORA)
                    + " | registrado por " + i.getUsuario().getUsername());
        }
    }
}
