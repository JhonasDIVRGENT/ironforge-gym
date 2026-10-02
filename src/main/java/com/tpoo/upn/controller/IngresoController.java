package com.tpoo.upn.controller;

import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.service.IngresoService;
import java.sql.SQLException;
import java.util.List;

/**
 * Recibe las solicitudes de la presentacion sobre ingresos y las delega en
 * IngresoService, que decide si el cliente puede ingresar.
 */
public class IngresoController {

    private final IngresoService ingresoService;

    public IngresoController(IngresoService ingresoService) {
        if (ingresoService == null) {
            throw new IllegalArgumentException("El servicio de ingresos es obligatorio");
        }
        this.ingresoService = ingresoService;
    }

    public Ingreso registrarIngreso(String dni) throws SQLException {
        return ingresoService.registrarIngreso(dni);
    }

    public List<Ingreso> consultarHistorial(String dni) throws SQLException {
        return ingresoService.consultarHistorial(dni);
    }
}
