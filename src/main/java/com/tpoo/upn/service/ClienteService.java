package com.tpoo.upn.service;

import java.sql.SQLException;
import java.util.List;

import com.tpoo.upn.dao.ClienteDAO;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.session.Sesion;

/**
 * Reglas de negocio de clientes: registrar, buscar y actualizar.

 */
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
     * Las membresias e ingresos se conservan porque apuntan al mismo id_cliente.
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

    /**
     * Ampliacion de usabilidad (aun no reflejada en el UML ni en el informe):
     * permite elegir un cliente de una lista en lugar de escribir su DNI.
     * La pueden usar ambos roles; no da permiso al administrador para registrar
     * ni modificar clientes.
     */
    public List<Cliente> listarClientes() throws SQLException {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        if (!sesion.esRecepcionista() && !sesion.esAdministrador()) {
            throw new IllegalStateException("No tiene permiso para consultar la lista de clientes");
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
