package com.tpoo.upn.app;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

// Recepcionista: registrar un cliente nuevo. Guarda 1 cliente.
public class RecepcionRegistrarCliente {

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        ClienteService clienteService = new ClienteService(sesion);

        System.out.println("=== IronForge Gym - Registrar cliente ===");

        try {
            usuarioService.iniciarSesion(Consola.leer("Username del recepcionista: "),
                    Consola.leerClave("Contrasena"));
            System.out.println();

            String dni = Consola.leer("DNI (8 digitos): ");
            String nombres = Consola.leer("Nombres: ");
            String apellidos = Consola.leer("Apellidos: ");
            String telefono = Consola.leer("Telefono (Enter si no tiene): ");

            Cliente cliente = new Cliente(dni, nombres, apellidos, telefono.isEmpty() ? null : telefono);
            clienteService.registrarCliente(cliente);

            Cliente guardado = clienteService.buscarCliente(dni);
            System.out.println("CLIENTE REGISTRADO: " + guardado.getNombreCompleto() + " - DNI " + guardado.getDni()
                    + " - tel. " + (guardado.getTelefono() == null ? "sin telefono" : guardado.getTelefono()));
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
