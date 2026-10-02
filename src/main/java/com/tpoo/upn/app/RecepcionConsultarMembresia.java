package com.tpoo.upn.app;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

/**
 * Caso: la recepcionista verifica el estado de las membresias de un cliente (RF-04).
 * Necesita una cuenta RECEPCIONISTA activa y clientes registrados. Solo lee datos.
 */
public class RecepcionConsultarMembresia {

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioController usuarioController = new UsuarioController(new UsuarioService(sesion));
        ClienteController clienteController = new ClienteController(new ClienteService(sesion));
        MembresiaController membresiaController = new MembresiaController(new MembresiaService(sesion));

        System.out.println("=== IronForge Gym - Consultar membresia de un cliente ===");

        try {
            usuarioController.iniciarSesion(Consola.leer("Username del recepcionista: "),
                    Consola.leerClave("Contrasena"));

            Cliente cliente = Consola.elegirCliente(clienteController.listarClientes());
            if (cliente == null) {
                return;
            }
            System.out.println();
            System.out.println("Cliente: " + cliente.getNombreCompleto() + " - DNI " + cliente.getDni());
            System.out.println("Membresias (estado a la fecha de hoy):");
            Consola.mostrarMembresias(membresiaController.consultarVigencia(cliente.getDni()));
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
