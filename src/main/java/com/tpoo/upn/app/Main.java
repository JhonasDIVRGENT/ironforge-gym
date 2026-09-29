package com.tpoo.upn.app;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.IngresoController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Demostracion por consola de los requerimientos funcionales sobre MySQL.
 * Main solo pide datos, llama a los controladores y muestra resultados.
 */
public class Main {

    // Toda la entrada pasa por un unico Scanner
    private static final Scanner ENTRADA = new Scanner(System.in);

    private static final List<String> PASOS = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== IronForge Gym - demostracion de requerimientos ===");
        System.out.println("ATENCION: esta demostracion CREA datos reales en la base");
        System.out.println("(un cliente, una membresia y un ingreso) y los deja guardados.");
        System.out.println("Nota: la contrasena sera visible mientras la escribe.");
        System.out.println();

        Sesion sesion = new Sesion();
        UsuarioController usuarioCtrl = new UsuarioController(sesion);
        ClienteController clienteCtrl = new ClienteController(sesion);
        MembresiaController membresiaCtrl = new MembresiaController(sesion);
        IngresoController ingresoCtrl = new IngresoController(sesion);

        try {
            ejecutarDemostracion(sesion, usuarioCtrl, clienteCtrl, membresiaCtrl, ingresoCtrl);
        } catch (SQLException e) {
            // No se imprime el mensaje completo para no exponer datos de conexion.
            System.out.println();
            System.out.println("ERROR de base de datos (" + e.getClass().getSimpleName() + ").");
            System.out.println("Compruebe que MySQL este activo y que la base exista.");
        } finally {
            sesion.cerrar();
            System.out.println();
            System.out.println("Sesion cerrada.");
            mostrarResumen();
        }
    }

    private static void ejecutarDemostracion(Sesion sesion, UsuarioController usuarioCtrl,
            ClienteController clienteCtrl, MembresiaController membresiaCtrl,
            IngresoController ingresoCtrl) throws SQLException {

        // ---------- PASO 1 - RF-10: iniciar sesion como recepcionista ----------
        System.out.println("--- PASO 1 (RF-10): iniciar sesion como RECEPCIONISTA ---");
        Usuario recepcionista;
        try {
            recepcionista = usuarioCtrl.iniciarSesion(
                    leerTexto("Username del recepcionista: "),
                    leerClave("Contrasena: "));
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo iniciar sesion: " + e.getMessage());
            return;
        }
        if (!sesion.esRecepcionista()) {
            System.out.println("La cuenta '" + recepcionista.getUsername() + "' tiene rol "
                    + recepcionista.getRol() + ".");
            System.out.println("Esta demostracion necesita una cuenta de RECEPCIONISTA. Se termina aqui.");
            return;
        }
        System.out.println("Sesion iniciada: " + recepcionista.getNombreCompleto()
                + " (" + recepcionista.getRol() + ")");
        PASOS.add("RF-10 login de recepcionista: OK");

        // ---------- PASO 2 - RF-01: registrar un cliente ----------
        System.out.println();
        System.out.println("--- PASO 2 (RF-01): registrar un cliente de demostracion ---");
        Cliente cliente = pedirClienteNuevo(clienteCtrl);
        if (!clienteCtrl.registrar(cliente)) {
            System.out.println("El cliente no se pudo registrar. Se termina la demostracion.");
            return;
        }
        System.out.println("Cliente registrado con id " + cliente.getIdCliente());
        PASOS.add("RF-01 registro de cliente: OK (id " + cliente.getIdCliente() + ")");

        String dni = cliente.getDni();

        // ---------- PASO 3 - RF-02: buscar al cliente por DNI ----------
        System.out.println();
        System.out.println("--- PASO 3 (RF-02): buscar al cliente por su DNI ---");
        Cliente encontrado = clienteCtrl.buscarCliente(dni);
        if (encontrado == null) {
            System.out.println("No se encontro el cliente recien registrado. Se termina la demostracion.");
            return;
        }
        System.out.println("DNI: " + encontrado.getDni());
        System.out.println("Nombre: " + encontrado.getNombreCompleto());
        System.out.println("Telefono: " + encontrado.getTelefono());
        PASOS.add("RF-02 busqueda por DNI: OK");

        // ---------- PASO 4 - RF-07: ingreso rechazado por no tener membresia ----------
        System.out.println();
        System.out.println("--- PASO 4 (RF-07): intentar un ingreso SIN membresia ---");
        try {
            ingresoCtrl.registrarIngreso(dni);
            System.out.println("FALLO DE LA DEMOSTRACION: se permitio el ingreso sin membresia vigente.");
            PASOS.add("RF-07 rechazo sin membresia: FALLO (se permitio)");
        } catch (IllegalArgumentException e) {
            System.out.println("Rechazado como se esperaba: " + e.getMessage());
            PASOS.add("RF-07 rechazo sin membresia: OK");
        }

        // ---------- PASO 5 - RF-05: registrar una membresia ----------
        System.out.println();
        System.out.println("--- PASO 5 (RF-05): registrar una membresia ---");
        List<TipoMembresia> tipos = membresiaCtrl.listarTipos();
        if (tipos.isEmpty()) {
            System.out.println("No hay tipos de membresia cargados en la base.");
            System.out.println("Ejecute Insert.sql o registre un tipo antes de repetir la demostracion.");
            return;
        }
        TipoMembresia tipo = elegirTipo(tipos);
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(30);
        System.out.println("Fechas de ejemplo para demostrar la vigencia: hoy y hoy + 30 dias.");
        System.out.println("El tipo de membresia no define una duracion automatica.");

        Membresia membresia = membresiaCtrl.registrarMembresia(cliente, tipo, inicio, fin);
        System.out.println("Membresia guardada: id " + membresia.getIdMembresia()
                + " | tipo " + membresia.getTipo().getNombre()
                + " | " + membresia.getFechaInicio() + " a " + membresia.getFechaFin());
        PASOS.add("RF-05 registro de membresia: OK (id " + membresia.getIdMembresia() + ")");

        // ---------- PASO 6 - RF-04: consultar la vigencia ----------
        System.out.println();
        System.out.println("--- PASO 6 (RF-04): consultar la vigencia de sus membresias ---");
        LocalDate hoy = LocalDate.now();
        for (Membresia m : membresiaCtrl.consultarVigencia(dni)) {
            System.out.println("  id " + m.getIdMembresia() + " | " + m.getFechaInicio()
                    + " a " + m.getFechaFin() + " | estado: " + m.obtenerEstado(hoy));
        }
        PASOS.add("RF-04 consulta de vigencia: OK");

        // ---------- PASO 7 - RF-06, RF-07, RF-08: ingreso autorizado ----------
        System.out.println();
        System.out.println("--- PASO 7 (RF-06, RF-07, RF-08): registrar un ingreso autorizado ---");
        Ingreso ingreso = ingresoCtrl.registrarIngreso(dni);
        System.out.println("Ingreso id " + ingreso.getIdIngreso());
        System.out.println("  Cliente: " + ingreso.getCliente().getNombreCompleto());
        System.out.println("  Membresia usada: " + ingreso.getMembresia().getIdMembresia());
        System.out.println("  Registrado por: " + ingreso.getUsuario().getNombreCompleto());
        System.out.println("  Fecha y hora: " + ingreso.getFechaHora());
        PASOS.add("RF-06/07/08 ingreso autorizado: OK (id " + ingreso.getIdIngreso() + ")");

        // ---------- PASO 8 - RF-01: rechazo de DNI duplicado ----------
        System.out.println();
        System.out.println("--- PASO 8 (RF-01): intentar registrar el mismo DNI otra vez ---");
        try {
            clienteCtrl.registrar(new Cliente(dni, "Duplicado", "Demostracion", null));
            System.out.println("FALLO DE LA DEMOSTRACION: se acepto un DNI duplicado.");
            PASOS.add("RF-01 rechazo de DNI duplicado: FALLO (se acepto)");
        } catch (IllegalArgumentException e) {
            System.out.println("Rechazado como se esperaba: " + e.getMessage());
            PASOS.add("RF-01 rechazo de DNI duplicado: OK");
        }

        // ---------- PASO 9 - RF-16: operacion sin permiso ----------
        System.out.println();
        System.out.println("--- PASO 9 (RF-16): el recepcionista intenta ver un historial ---");
        try {
            ingresoCtrl.consultarHistorial(dni);
            System.out.println("FALLO DE LA DEMOSTRACION: el recepcionista accedio al historial.");
            PASOS.add("RF-16 restriccion por rol: FALLO (se permitio)");
        } catch (IllegalStateException e) {
            System.out.println("Rechazado como se esperaba: " + e.getMessage());
            PASOS.add("RF-16 restriccion por rol: OK");
        }

        // ---------- PASO 10: cambiar a administrador ----------
        System.out.println();
        System.out.println("--- PASO 10: cerrar sesion e iniciar como ADMINISTRADOR ---");
        sesion.cerrar();
        System.out.println("Sesion del recepcionista cerrada.");
        Usuario administrador;
        try {
            administrador = usuarioCtrl.iniciarSesion(
                    leerTexto("Username del administrador: "),
                    leerClave("Contrasena: "));
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo iniciar sesion: " + e.getMessage());
            return;
        }
        if (!sesion.esAdministrador()) {
            System.out.println("La cuenta '" + administrador.getUsername() + "' tiene rol "
                    + administrador.getRol() + ".");
            System.out.println("El ultimo paso necesita una cuenta de ADMINISTRADOR. Se termina aqui.");
            return;
        }
        System.out.println("Sesion iniciada: " + administrador.getNombreCompleto()
                + " (" + administrador.getRol() + ")");
        PASOS.add("Cambio de rol a administrador: OK");

        // ---------- PASO 11 - RF-09: historial del cliente ----------
        System.out.println();
        System.out.println("--- PASO 11 (RF-09): consultar el historial del cliente ---");
        List<Ingreso> historial = ingresoCtrl.consultarHistorial(dni);
        boolean apareceElIngreso = false;
        for (Ingreso i : historial) {
            System.out.println("  id " + i.getIdIngreso() + " | " + i.getFechaHora()
                    + " | registrado por " + i.getUsuario().getUsername());
            if (i.getIdIngreso() == ingreso.getIdIngreso()) {
                apareceElIngreso = true;
            }
        }
        System.out.println("Total de ingresos del cliente: " + historial.size());

        if (apareceElIngreso && historial.size() == 1) {
            System.out.println("Correcto: aparece el ingreso autorizado y el intento del paso 4");
            System.out.println("no dejo ningun registro.");
            PASOS.add("RF-09 historial de ingresos: OK");
        } else if (!apareceElIngreso) {
            System.out.println("FALLO: el ingreso id " + ingreso.getIdIngreso() + " no aparece.");
            PASOS.add("RF-09 historial de ingresos: FALLO (falta el ingreso)");
        } else {
            System.out.println("FALLO: se esperaba un unico ingreso para un cliente nuevo.");
            PASOS.add("RF-09 historial de ingresos: FALLO (" + historial.size() + " registros)");
        }
    }

    /** Pide un DNI libre y construye el cliente de demostracion. */
    private static Cliente pedirClienteNuevo(ClienteController clienteCtrl) throws SQLException {
        while (true) {
            String dni = leerTexto("DNI nuevo para el cliente de demostracion (8 digitos): ");
            Cliente cliente;
            try {
                cliente = new Cliente(dni, "DEMO", "Cliente de prueba", "999000111");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }
            if (clienteCtrl.buscarCliente(dni) != null) {
                System.out.println("Ese DNI ya esta registrado. Escriba otro; no se modificara el existente.");
                continue;
            }
            return cliente;
        }
    }

    private static TipoMembresia elegirTipo(List<TipoMembresia> tipos) {
        System.out.println("Tipos de membresia disponibles:");
        for (TipoMembresia t : tipos) {
            System.out.println("  id " + t.getIdTipo() + " | " + t.getNombre() + " | S/ " + t.getPrecio());
        }
        while (true) {
            String texto = leerTexto("Id del tipo que desea usar: ");
            for (TipoMembresia t : tipos) {
                if (String.valueOf(t.getIdTipo()).equals(texto.trim())) {
                    return t;
                }
            }
            System.out.println("Ese id no esta en la lista.");
        }
    }

    private static String leerTexto(String etiqueta) {
        System.out.print(etiqueta);
        return ENTRADA.nextLine().trim();
    }

    private static String leerClave(String etiqueta) {
        System.out.print(etiqueta);
        return ENTRADA.nextLine();
    }

    private static void mostrarResumen() {
        System.out.println();
        System.out.println("=== Resumen de la demostracion ===");
        if (PASOS.isEmpty()) {
            System.out.println("No se completo ningun paso.");
            return;
        }
        for (String paso : PASOS) {
            System.out.println("  " + paso);
        }
    }
}
