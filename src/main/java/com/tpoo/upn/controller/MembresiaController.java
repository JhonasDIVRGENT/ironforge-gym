package com.tpoo.upn.controller;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.service.MembresiaService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Recibe las solicitudes de la presentacion sobre membresias y tipos de membresia
 * y las delega en MembresiaService.
 */
public class MembresiaController {

    private final MembresiaService membresiaService;

    public MembresiaController(MembresiaService membresiaService) {
        if (membresiaService == null) {
            throw new IllegalArgumentException("El servicio de membresias es obligatorio");
        }
        this.membresiaService = membresiaService;
    }

    public Membresia registrarMembresia(Cliente cliente, TipoMembresia tipo,
            LocalDate inicio, LocalDate fin) throws SQLException {
        return membresiaService.registrarMembresia(cliente, tipo, inicio, fin);
    }

    public Membresia renovarMembresia(Cliente cliente, TipoMembresia tipo,
            LocalDate inicio, LocalDate fin) throws SQLException {
        return membresiaService.renovarMembresia(cliente, tipo, inicio, fin);
    }

    public List<Membresia> consultarVigencia(String dni) throws SQLException {
        return membresiaService.consultarVigencia(dni);
    }

    public List<Membresia> listarPorVencer() throws SQLException {
        return membresiaService.listarPorVencer();
    }

    public boolean registrarTipo(TipoMembresia tipo) throws SQLException {
        return membresiaService.registrarTipo(tipo);
    }

    public List<TipoMembresia> listarTipos() throws SQLException {
        return membresiaService.listarTipos();
    }
}
