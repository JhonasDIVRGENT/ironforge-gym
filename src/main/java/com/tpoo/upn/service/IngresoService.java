package com.tpoo.upn.service;

import com.tpoo.upn.dao.ClienteDAO;
import com.tpoo.upn.dao.IngresoDAO;
import com.tpoo.upn.dao.MembresiaDAO;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reglas de negocio de ingresos: registrar el acceso de un cliente
 * y consultar su historial.
 */
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

    /**
     * Registra el ingreso solo si el cliente existe y tiene una membresia vigente.
     * El usuario se toma de la Sesion y la fecha y hora del reloj del sistema.
     */
    public Ingreso registrarIngreso(String dni) throws SQLException {
        exigirRecepcionista();
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }

        Cliente cliente = clienteDAO.buscarPorDni(dni);
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }

        // Se toma una sola vez: la misma marca de tiempo decide la vigencia y se guarda en el ingreso.
        LocalDateTime momento = LocalDateTime.now();

        Membresia autorizada = buscarMembresiaVigente(cliente, momento);
        if (autorizada == null) {
            throw new IllegalArgumentException("El cliente no tiene una membresia vigente");
        }

        Usuario empleado = sesion.getUsuarioActual();
        Ingreso ingreso = new Ingreso(cliente, autorizada, empleado, momento);
        // Se informa el fallo en lugar de devolver un ingreso que no quedo guardado.
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

        // Si el cliente no existe es un error; si existe pero no ha entrado nunca, la lista va vacia.
        if (clienteDAO.buscarPorDni(dni) == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        return ingresoDAO.listarPorCliente(dni);
    }

    /**
     * Devuelve la membresia vigente del cliente, o null si no tiene ninguna.
     * Si hubiera varias vigentes se elige la de mayor idMembresia, que es la registrada
     * mas recientemente; es un criterio tecnico para que la eleccion sea siempre la misma.
     */
    private Membresia buscarMembresiaVigente(Cliente cliente, LocalDateTime momento) throws SQLException {
        List<Membresia> membresias = membresiaDAO.listarPorCliente(cliente.getDni());
        Membresia elegida = null;

        for (Membresia membresia : membresias) {
            // La membresia debe ser del mismo cliente; se comparan los ids, no las referencias.
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
