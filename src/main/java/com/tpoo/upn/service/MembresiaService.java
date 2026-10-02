package com.tpoo.upn.service;

import com.tpoo.upn.dao.ClienteDAO;
import com.tpoo.upn.dao.MembresiaDAO;
import com.tpoo.upn.dao.TipoMembresiaDAO;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Reglas de negocio de membresias: registrar, renovar, consultar vigencia,
 * proximas a vencer y gestion de tipos de membresia.
 */
public class MembresiaService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MembresiaDAO membresiaDAO = new MembresiaDAO();
    // ClienteDAO se usa para comprobar que el cliente existe realmente en la base.
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoMembresiaDAO tipoDAO = new TipoMembresiaDAO();
    private final Sesion sesion;

    public MembresiaService(Sesion sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesion es obligatoria");
        }
        this.sesion = sesion;
    }

    /**
     * Registrar es para un cliente sin membresia vigente ni programada.
     * Si ya tiene una, debe usarse renovar para que los periodos se encadenen.
     */
    public Membresia registrarMembresia(Cliente cliente, TipoMembresia tipo,
            LocalDate inicio, LocalDate fin) throws SQLException {
        exigirRecepcionista();
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        LocalDate ultimoFin = ultimoVencimiento(membresiaDAO.listarPorCliente(cliente.getDni()));
        if (ultimoFin != null && !ultimoFin.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("El cliente ya tiene una membresia vigente o programada hasta el "
                    + ultimoFin.format(FECHA) + "; use Renovar");
        }
        validarSinSuperposicion(ultimoFin, inicio);
        return guardarMembresia(cliente, tipo, inicio, fin);
    }

    /**
     * Renovar es insertar un periodo nuevo que empieza despues del ultimo vencimiento:
     * los periodos se acumulan uno detras de otro y los anteriores se conservan.
     */
    public Membresia renovarMembresia(Cliente cliente, TipoMembresia tipo,
            LocalDate inicio, LocalDate fin) throws SQLException {
        exigirRecepcionista();
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        List<Membresia> anteriores = membresiaDAO.listarPorCliente(cliente.getDni());
        if (anteriores.isEmpty()) {
            throw new IllegalArgumentException("El cliente no tiene una membresia anterior que renovar");
        }
        validarSinSuperposicion(ultimoVencimiento(anteriores), inicio);
        return guardarMembresia(cliente, tipo, inicio, fin);
    }

    /** Devuelve todas las membresias del cliente; el estado se calcula con Membresia.obtenerEstado. */
    public List<Membresia> consultarVigencia(String dni) throws SQLException {
        exigirRecepcionista();
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        // Se distingue "el cliente no existe" de "el cliente no tiene membresias".
        if (clienteDAO.buscarPorDni(dni) == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        return membresiaDAO.listarPorCliente(dni);
    }

    /** RF-11: membresias vigentes hoy que vencen entre hoy y los siguientes siete dias. */
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

    /** Ambos roles pueden consultar los tipos (RF-15). */
    public List<TipoMembresia> listarTipos() throws SQLException {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesion para realizar esta operacion");
        }
        return tipoDAO.listar();
    }

    /**
     * Un cliente no puede tener dos periodos que cubran el mismo dia: el nuevo
     * periodo debe empezar despues del ultimo vencimiento registrado.
     * Si falta la fecha de inicio, la rechaza despues el constructor de Membresia.
     */
    private void validarSinSuperposicion(LocalDate ultimoFin, LocalDate inicio) {
        if (ultimoFin != null && inicio != null && !inicio.isAfter(ultimoFin)) {
            throw new IllegalArgumentException("La membresia debe empezar despues del "
                    + ultimoFin.format(FECHA) + ", fecha del ultimo vencimiento del cliente");
        }
    }

    /** Fecha de fin mas lejana entre los periodos del cliente, o null si no tiene ninguno. */
    private LocalDate ultimoVencimiento(List<Membresia> membresias) {
        LocalDate ultimo = null;
        for (Membresia m : membresias) {
            if (ultimo == null || m.getFechaFin().isAfter(ultimo)) {
                ultimo = m.getFechaFin();
            }
        }
        return ultimo;
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

        // El constructor de Membresia valida que las fechas existan y que fin no sea anterior a inicio.
        Membresia membresia = new Membresia(clienteGuardado, tipoGuardado, inicio, fin);
        // Se informa el fallo en lugar de devolver una membresia que no quedo guardada.
        if (!membresiaDAO.insertar(membresia)) {
            throw new SQLException("No se pudo guardar la membresia");
        }
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
