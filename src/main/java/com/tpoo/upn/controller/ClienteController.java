package com.tpoo.upn.controller;

import com.tpoo.upn.dao.ClienteDAO;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;

/**
 * Reglas de negocio de clientes: registrar , buscar 
 * y actualizar . Todas corresponden al recepcionista.
 */
public class ClienteController {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Sesion sesion;

    public ClienteController(Sesion sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesion es obligatoria");
        }
        this.sesion = sesion;
    }

    public boolean registrar(Cliente cliente) throws SQLException {
        exigirRecepcionista();
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (clienteDAO.existeDni(cliente.getDni())) {
            throw new IllegalArgumentException("El DNI ya esta registrado");
        }
        return clienteDAO.insertar(cliente);
    }

    /** Devuelve null cuando el DNI no corresponde a ningun cliente. */
    public Cliente buscarCliente(String dni) throws SQLException {
        exigirRecepcionista();
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        return clienteDAO.buscarPorDni(dni);
    }

    /**
     * Actualiza nombres, apellidos y telefono del cliente.
     * El DNI y el id identifican el registro, por eso no se cambian aqui.
     */
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

    private void exigirRecepcionista() {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        if (!sesion.esRecepcionista()) {
            throw new IllegalStateException("Solo el recepcionista puede realizar esta operacion");
        }
    }
}
