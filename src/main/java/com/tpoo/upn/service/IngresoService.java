package com.tpoo.upn.service;

import com.tpoo.upn.dao.ClienteDAO;
import com.tpoo.upn.dao.IngresoDAO;
import com.tpoo.upn.dao.MembresiaDAO;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class IngresoService {

    private final IngresoDAO ingresoDAO = new IngresoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MembresiaDAO membresiaDAO = new MembresiaDAO();
    private final Sesion sesion;

    public IngresoService(Sesion sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesion es obligatoria");
        }
        this.sesion = sesion;
    }

    // Solo registra si el cliente existe y tiene una membresia vigente.
    public Ingreso registrarIngreso(String dni) throws SQLException {
        exigirRecepcionista();
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        Cliente cliente = clienteDAO.buscarPorDni(dni);
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }

        LocalDateTime momento = LocalDateTime.now();
        Membresia autorizada = buscarMembresiaVigente(cliente, momento);
        if (autorizada == null) {
            throw new IllegalArgumentException("El cliente no tiene una membresia vigente");
        }

        Ingreso ingreso = new Ingreso(cliente, autorizada, sesion.getUsuarioActual(), momento);
        if (!ingresoDAO.insertar(ingreso)) {
            throw new SQLException("No se pudo guardar el ingreso");
        }
        return ingreso;
    }

    public List<Ingreso> consultarHistorial(String dni) throws SQLException {
        exigirAdministrador();
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        if (clienteDAO.buscarPorDni(dni) == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        return ingresoDAO.listarPorCliente(dni);
    }

    // Si hay varias vigentes se usa la de mayor idMembresia (la mas reciente).
    private Membresia buscarMembresiaVigente(Cliente cliente, LocalDateTime momento) throws SQLException {
        Membresia elegida = null;
        for (Membresia membresia : membresiaDAO.listarPorCliente(cliente.getDni())) {
            if (membresia.getCliente().getIdCliente() != cliente.getIdCliente()) {
                continue;
            }
            if (!membresia.estaVigente(momento.toLocalDate())) {
                continue;
            }
            if (elegida == null || membresia.getIdMembresia() > elegida.getIdMembresia()) {
                elegida = membresia;
            }
        }
        return elegida;
    }

    private void exigirRecepcionista() {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        if (!sesion.esRecepcionista()) {
            throw new IllegalStateException("Solo el recepcionista puede realizar esta operacion");
        }
    }

    private void exigirAdministrador() {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        if (!sesion.esAdministrador()) {
            throw new IllegalStateException("Solo el administrador puede realizar esta operacion");
        }
    }
}
