package com.tpoo.upn.app;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Demostracion de clientes (RF-01, RF-02, RF-03).
 * Necesita una cuenta RECEPCIONISTA activa y un DNI que todavia no exista.
 * Registra un cliente nuevo y despues cambia sus apellidos y telefono.
 */
public class PruebaClientes {

    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        ClienteService clienteService = new ClienteService(sesion);

        System.out.println("=== Prueba de clientes ===");
        System.out.println("Se registrara un cliente nuevo y luego se actualizaran sus datos.");

        try {
            usuarioService.iniciarSesion(leer("Username del recepcionista: "), leerClave());
            String dni = leer("DNI nuevo (8 digitos, que no exista): ");

            // Antes de registrar, la busqueda debe indicar que no existe.
            if (clienteService.buscarCliente(dni) != null) {
                System.out.println("Ese DNI ya esta registrado. Ejecute de nuevo con otro DNI.");
                return;
            }
            System.out.println("1. Busqueda previa: Cliente no encontrado.");

            clienteService.registrarCliente(new Cliente(dni, "Cliente", "Prueba Clientes", "999000111"));
            Cliente cliente = clienteService.buscarCliente(dni);
            System.out.println("2. Registrado y encontrado: " + cliente.getNombreCompleto()
                    + " | tel. " + cliente.getTelefono() + " | id " + cliente.getIdCliente());

            cliente.setApellidos("Prueba Actualizado");
            cliente.setTelefono("999000222");
            clienteService.actualizarCliente(cliente);
            Cliente actualizado = clienteService.buscarCliente(dni);
            System.out.println("3. Actualizado: " + actualizado.getNombreCompleto()
                    + " | tel. " + actualizado.getTelefono() + " | mismo id " + actualizado.getIdCliente());

            try {
                clienteService.registrarCliente(new Cliente(dni, "Otro", "Cliente", null));
                System.out.println("4. FALLO: se acepto un DNI duplicado.");
            } catch (IllegalArgumentException e) {
                System.out.println("4. DNI duplicado rechazado como se esperaba: " + e.getMessage());
            }

            // El modelo valida los datos obligatorios antes de llegar al servicio (HU-01 CA-02).
            try {
                clienteService.registrarCliente(new Cliente(dni, "", "", null));
                System.out.println("5. FALLO: se acepto un cliente sin nombres ni apellidos.");
            } catch (IllegalArgumentException e) {
                System.out.println("5. Datos vacios rechazados como se esperaba: " + e.getMessage());
            }
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
