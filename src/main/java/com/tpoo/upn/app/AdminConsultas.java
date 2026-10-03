package com.tpoo.upn.app;

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

// Administrador: membresias proximas a vencer o historial de ingresos. Solo lee datos.
public class AdminConsultas {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        ClienteService clienteService = new ClienteService(sesion);
        MembresiaService membresiaService = new MembresiaService(sesion);
        IngresoService ingresoService = new IngresoService(sesion);

        System.out.println("=== IronForge Gym - Consultas del administrador ===");

        try {
            usuarioService.iniciarSesion(Consola.leer("Username del administrador: "),
                    Consola.leerClave("Contrasena"));
            System.out.println();

            System.out.println("1. Membresias proximas a vencer (proximos 7 dias)");
            System.out.println("2. Historial de ingresos de un cliente");
            System.out.println("0. Salir");

            switch (Consola.leerOpcion(2)) {
                case 1 -> mostrarPorVencer(membresiaService);
                case 2 -> mostrarHistorial(clienteService, ingresoService);
                default -> System.out.println("Sin consultas.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operacion no realizada: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioService.cerrarSesion();
            System.out.println("Sesion cerrada.");
        }
    }

    private static void mostrarPorVencer(MembresiaService membresiaService) throws SQLException {
        List<Membresia> porVencer = membresiaService.listarPorVencer();
        System.out.println("Membresias vigentes que vencen en los proximos 7 dias:");
        if (porVencer.isEmpty()) {
            System.out.println("   No existen membresias proximas a vencer.");
        }
        for (Membresia m : porVencer) {
            System.out.println("   " + m.getCliente().getNombreCompleto() + " - DNI " + m.getCliente().getDni()
                    + " | " + m.getTipo().getNombre() + " | vence " + m.getFechaFin());
        }
    }

    private static void mostrarHistorial(ClienteService clienteService, IngresoService ingresoService)
            throws SQLException {
        Cliente cliente = Consola.elegirCliente(clienteService.listarClientes());
        if (cliente == null) {
            return;
        }
        List<Ingreso> historial = ingresoService.consultarHistorial(cliente.getDni());
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
