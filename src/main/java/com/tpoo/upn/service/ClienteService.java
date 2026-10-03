package com.tpoo.upn.service;

import com.tpoo.upn.dao.ClienteDAO;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Sesion sesion;

    public ClienteService(Sesion sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesion es obligatoria");
        }
        this.sesion = sesion;
    }

    public boolean registrarCliente(Cliente cliente) throws SQLException {
        exigirRecepcionista();
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (clienteDAO.existeDni(cliente.getDni())) {
            throw new IllegalArgumentException("El DNI ya esta registrado");
        }
        return clienteDAO.insertar(cliente);
    }

    // Devuelve null si el DNI no existe.
    public Cliente buscarCliente(String dni) throws SQLException {
        exigirRecepcionista();
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        return clienteDAO.buscarPorDni(dni);
    }

    // Cambia nombres, apellidos y telefono; el DNI y el id no cambian, asi se conservan membresias e ingresos.
    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        exigirRecepcionista();
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        Cliente guardado = clienteDAO.buscarPorDni(cliente.getDni());
        if (guardado == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        if (guardado.getIdCliente() != cliente.getIdCliente()) {
            throw new IllegalArgumentException("El cliente no corresponde al registro guardado");
        }
        return clienteDAO.actualizar(cliente);
    }

    // Ambos roles pueden listar clientes para elegirlos.
    public List<Cliente> listarClientes() throws SQLException {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        return clienteDAO.listar();
    }

    private void exigirRecepcionista() {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        if (!sesion.esRecepcionista()) {
            throw new IllegalStateException("Solo el recepcionista puede realizar esta operacion");
        }
    }
}
