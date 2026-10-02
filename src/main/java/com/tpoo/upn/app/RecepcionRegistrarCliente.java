package com.tpoo.upn.app;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

/**
 * Caso: la recepcionista registra un cliente nuevo (RF-01, RF-02).
 * Necesita una cuenta RECEPCIONISTA activa y un DNI que todavia no exista.
 * Guarda un cliente nuevo en la base de datos.
 */
public class RecepcionRegistrarCliente {

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioController usuarioController = new UsuarioController(new UsuarioService(sesion));
        ClienteController clienteController = new ClienteController(new ClienteService(sesion));

        System.out.println("=== IronForge Gym - Registrar cliente ===");

        try {
            usuarioController.iniciarSesion(Consola.leer("Username del recepcionista: "),
                    Consola.leerClave("Contrasena"));
            System.out.println();

            String dni = Consola.leer("DNI (8 digitos): ");
            String nombres = Consola.leer("Nombres: ");
            String apellidos = Consola.leer("Apellidos: ");
            String telefono = Consola.leer("Telefono (Enter si no tiene): ");

            // El constructor de Cliente valida los datos; el servicio comprueba que el DNI no exista.
            Cliente cliente = new Cliente(dni, nombres, apellidos, telefono.isEmpty() ? null : telefono);
            clienteController.registrarCliente(cliente);

            // Se busca de nuevo para mostrar lo que realmente quedo guardado.
            Cliente guardado = clienteController.buscarCliente(dni);
            System.out.println("CLIENTE REGISTRADO: " + guardado.getNombreCompleto() + " - DNI " + guardado.getDni()
                    + " - tel. " + (guardado.getTelefono() == null ? "sin telefono" : guardado.getTelefono()));
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
