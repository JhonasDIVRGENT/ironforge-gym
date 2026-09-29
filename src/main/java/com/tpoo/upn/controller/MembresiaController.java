package com.tpoo.upn.controller;

import com.tpoo.upn.dao.ClienteDAO;
import com.tpoo.upn.dao.MembresiaDAO;
import com.tpoo.upn.dao.TipoMembresiaDAO;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Reglas de negocio de membresias: registrar , renovar ,
 * consultar vigencia , proximas a vencer  y tipos .
 */
public class MembresiaController {

    private final MembresiaDAO membresiaDAO = new MembresiaDAO();
    private final TipoMembresiaDAO tipoDAO = new TipoMembresiaDAO();
    // ClienteDAO se usa solo para comprobar que el cliente existe realmente en la base.
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Sesion sesion;

    public MembresiaController(Sesion sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesion es obligatoria");
        }
        this.sesion = sesion;
    }

    public Membresia registrarMembresia(Cliente cliente, TipoMembresia tipo,
            LocalDate inicio, LocalDate fin) throws SQLException {
        exigirRecepcionista();
        return guardarMembresia(cliente, tipo, inicio, fin);
    }

    /** Renovar es insertar un periodo nuevo: las membresias anteriores se conservan. */
    public Membresia renovarMembresia(Cliente cliente, TipoMembresia tipo,
            LocalDate inicio, LocalDate fin) throws SQLException {
        exigirRecepcionista();
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (membresiaDAO.listarPorCliente(cliente.getDni()).isEmpty()) {
            throw new IllegalArgumentException("El cliente no tiene una membresia anterior que renovar");
        }
        return guardarMembresia(cliente, tipo, inicio, fin);
    }

    public List<Membresia> consultarVigencia(String dni) throws SQLException {
        exigirRecepcionista();
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        return membresiaDAO.listarPorCliente(dni);
    }

    /** RF-11: membresias vigentes que vencen entre hoy y los siguientes siete dias. */
    public List<Membresia> listarPorVencer() throws SQLException {
        exigirAdministrador();
        LocalDate hoy = LocalDate.now();
        return membresiaDAO.listarPorVencer(hoy, hoy.plusDays(7));
    }

    public boolean registrarTipo(TipoMembresia tipo) throws SQLException {
        exigirAdministrador();
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de membresia es obligatorio");
        }
        if (tipoDAO.existeNombre(tipo.getNombre())) {
            throw new IllegalArgumentException("El nombre del tipo ya esta registrado");
        }
        return tipoDAO.insertar(tipo);
    }

    public List<TipoMembresia> listarTipos() throws SQLException {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        return tipoDAO.listar();
    }

    /** Parte comun de registrar y renovar. */
    private Membresia guardarMembresia(Cliente cliente, TipoMembresia tipo,
            LocalDate inicio, LocalDate fin) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de membresia es obligatorio");
        }

        // Un id distinto de cero no demuestra que el registro exista: hay que consultarlo.
        Cliente clienteGuardado = clienteDAO.buscarPorDni(cliente.getDni());
        if (clienteGuardado == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        if (clienteGuardado.getIdCliente() != cliente.getIdCliente()) {
            throw new IllegalArgumentException("El cliente no corresponde al registro guardado");
        }

        TipoMembresia tipoGuardado = tipoDAO.buscarPorId(tipo.getIdTipo());
        if (tipoGuardado == null) {
            throw new IllegalArgumentException("El tipo de membresia no existe");
        }

        // El constructor de Membresia valida las fechas.
        Membresia membresia = new Membresia(clienteGuardado, tipoGuardado, inicio, fin);
        membresiaDAO.insertar(membresia);
        return membresia;
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
