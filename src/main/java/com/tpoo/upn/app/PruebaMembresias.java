package com.tpoo.upn.app;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Demostracion de membresias (RF-15, RF-05, RF-04, RF-12).
 * Necesita una cuenta RECEPCIONISTA activa y el DNI de un cliente existente,
 * por ejemplo el creado con PruebaClientes. Registra dos periodos nuevos para ese cliente:
 * uno desde hoy y su renovacion a continuacion.
 */
public class PruebaMembresias {

    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        ClienteService clienteService = new ClienteService(sesion);
        MembresiaService membresiaService = new MembresiaService(sesion);

        System.out.println("=== Prueba de membresias ===");
        System.out.println("Se registraran una membresia y su renovacion para el cliente indicado.");

        try {
            usuarioService.iniciarSesion(leer("Username del recepcionista: "), leerClave());

            List<TipoMembresia> tipos = membresiaService.listarTipos();
            System.out.println("1. Tipos disponibles:");
            for (TipoMembresia t : tipos) {
                System.out.println("   id " + t.getIdTipo() + " | " + t.getNombre() + " | S/ " + t.getPrecio());
            }
            if (tipos.isEmpty()) {
                System.out.println("No hay tipos registrados; no se puede continuar.");
                return;
            }

            Cliente cliente = clienteService.buscarCliente(leer("DNI de un cliente existente: "));
            if (cliente == null) {
                System.out.println("Cliente no encontrado.");
                return;
            }
            TipoMembresia tipo = tipos.get(0);
            LocalDate hoy = LocalDate.now();

            Membresia primera = membresiaService.registrarMembresia(cliente, tipo, hoy, hoy.plusDays(29));
            System.out.println("2. Registrada membresia " + tipo.getNombre() + " del "
                    + primera.getFechaInicio() + " al " + primera.getFechaFin());

            try {
                membresiaService.registrarMembresia(cliente, tipo, hoy, hoy.minusDays(1));
                System.out.println("3. FALLO: se acepto un vencimiento anterior al inicio.");
            } catch (IllegalArgumentException e) {
                System.out.println("3. Fechas invalidas rechazadas como se esperaba: " + e.getMessage());
            }

            // La renovacion es un periodo nuevo que empieza al dia siguiente del vencimiento.
            LocalDate inicioRenovacion = primera.getFechaFin().plusDays(1);
            Membresia renovacion = membresiaService.renovarMembresia(
                    cliente, tipo, inicioRenovacion, inicioRenovacion.plusDays(29));
            System.out.println("4. Renovacion registrada del " + renovacion.getFechaInicio()
                    + " al " + renovacion.getFechaFin());

            System.out.println("5. Vigencia hoy (los periodos anteriores se conservan):");
            for (Membresia m : membresiaService.consultarVigencia(cliente.getDni())) {
                System.out.println("   id " + m.getIdMembresia() + " | " + m.getFechaInicio()
                        + " a " + m.getFechaFin() + " | " + m.obtenerEstado(hoy));
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Demostracion detenida: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioService.cerrarSesion();
        }
    }

    private static String leer(String etiqueta) {
        System.out.print(etiqueta);
        return ENTRADA.nextLine().trim();
    }

    private static String leerClave() {
        System.out.print("Contrasena (visible al escribir): ");
        return ENTRADA.nextLine();
    }
}
