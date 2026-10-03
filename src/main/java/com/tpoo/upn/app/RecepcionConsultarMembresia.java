package com.tpoo.upn.app;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

// Recepcionista: ver las membresias de un cliente y su estado de hoy. Solo lee datos.
public class RecepcionConsultarMembresia {

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        ClienteService clienteService = new ClienteService(sesion);
        MembresiaService membresiaService = new MembresiaService(sesion);

        System.out.println("=== IronForge Gym - Consultar membresia de un cliente ===");

        try {
            usuarioService.iniciarSesion(Consola.leer("Username del recepcionista: "),
                    Consola.leerClave("Contrasena"));

            Cliente cliente = Consola.elegirCliente(clienteService.listarClientes());
            if (cliente == null) {
                return;
            }
            System.out.println();
            System.out.println("Cliente: " + cliente.getNombreCompleto() + " - DNI " + cliente.getDni());
            System.out.println("Membresias (estado a la fecha de hoy):");
            Consola.mostrarMembresias(membresiaService.consultarVigencia(cliente.getDni()));
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
