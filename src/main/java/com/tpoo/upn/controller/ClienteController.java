package com.tpoo.upn.controller;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.service.ClienteService;
import java.sql.SQLException;
import java.util.List;

/**
 * Recibe las solicitudes de la presentacion (consola o futura vista JavaFX)
 * sobre clientes y las delega en ClienteService.
 * No lee teclado ni muestra mensajes: devuelve resultados o propaga los errores.
 */
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        if (clienteService == null) {
            throw new IllegalArgumentException("El servicio de clientes es obligatorio");
        }
        this.clienteService = clienteService;
    }

    public boolean registrarCliente(Cliente cliente) throws SQLException {
        return clienteService.registrarCliente(cliente);
    }

    /** Devuelve null cuando el DNI no corresponde a ningun cliente. */
    public Cliente buscarCliente(String dni) throws SQLException {
        return clienteService.buscarCliente(dni);
    }

    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        return clienteService.actualizarCliente(cliente);
    }

    /** Ampliacion de usabilidad para elegir clientes de una lista (pendiente en el UML). */
    public List<Cliente> listarClientes() throws SQLException {
        return clienteService.listarClientes();
    }
}
