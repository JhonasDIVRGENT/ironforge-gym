package com.tpoo.upn.app;

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

// Recepcionista: elegir cliente, ver sus membresias y registrar su ingreso.
// Guarda 1 ingreso solo si se confirma y el servicio lo autoriza.
public class RecepcionMarcarAsistencia {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        ClienteService clienteService = new ClienteService(sesion);
        MembresiaService membresiaService = new MembresiaService(sesion);
        IngresoService ingresoService = new IngresoService(sesion);

        System.out.println("=== IronForge Gym - Marcar asistencia ===");

        try {
            Usuario usuario = usuarioService.iniciarSesion(
                    Consola.leer("Username del recepcionista: "), Consola.leerClave("Contrasena"));
            System.out.println("Sesion iniciada: " + usuario.getNombreCompleto() + " (" + usuario.getRol() + ")");
            System.out.println();

            Cliente cliente = Consola.elegirCliente(clienteService.listarClientes());
            if (cliente == null) {
                return;
            }

            System.out.println();
            System.out.println("Cliente: " + cliente.getNombreCompleto() + " - DNI " + cliente.getDni());
            System.out.println("Membresias:");
            Consola.mostrarMembresias(membresiaService.consultarVigencia(cliente.getDni()));

            if (!Consola.confirmar("Registrar el ingreso de " + cliente.getNombreCompleto() + "?")) {
                System.out.println("No se registro ningun ingreso.");
                return;
            }
            registrarIngreso(ingresoService, cliente);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operacion no realizada: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioService.cerrarSesion();
            System.out.println("Sesion cerrada.");
        }
    }

    private static void registrarIngreso(IngresoService ingresoService, Cliente cliente) throws SQLException {
        try {
            Ingreso ingreso = ingresoService.registrarIngreso(cliente.getDni());
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
