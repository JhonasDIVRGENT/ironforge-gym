package com.tpoo.upn.app;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Demostracion principal: una secuencia corta como recepcionista que cubre
 * RF-10, RF-01, RF-02, RF-05, RF-04, RF-06, RF-07 y RF-08.
 * Registra datos nuevos: un cliente, una membresia y un ingreso.
 * Las demas operaciones se demuestran en las clases Prueba* de este paquete.
 */
public class Main {

    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        // Una sola Sesion compartida por los servicios de esta ejecucion.
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        ClienteService clienteService = new ClienteService(sesion);
        MembresiaService membresiaService = new MembresiaService(sesion);
        IngresoService ingresoService = new IngresoService(sesion);

        System.out.println("=== IronForge Gym - demostracion principal ===");
        System.out.println("Se registraran un cliente, una membresia y un ingreso nuevos.");

        try {
            Usuario usuario = usuarioService.iniciarSesion(leer("Username del recepcionista: "), leerClave());
            System.out.println("1. Sesion iniciada: " + usuario.getNombreCompleto() + " (" + usuario.getRol() + ")");

            String dni = leer("DNI nuevo del cliente (8 digitos, que no exista): ");
            clienteService.registrarCliente(new Cliente(dni, "Cliente", "Demostracion", "999000111"));
            System.out.println("2. Cliente registrado correctamente.");

            Cliente cliente = clienteService.buscarCliente(dni);
            System.out.println("3. Busqueda por DNI: " + cliente.getNombreCompleto() + " | DNI " + cliente.getDni());

            List<TipoMembresia> tipos = membresiaService.listarTipos();
            if (tipos.isEmpty()) {
                System.out.println("No hay tipos de membresia registrados; no se puede continuar.");
                return;
            }
            TipoMembresia tipo = tipos.get(0);
            LocalDate hoy = LocalDate.now();
            Membresia membresia = membresiaService.registrarMembresia(cliente, tipo, hoy, hoy.plusDays(29));
            System.out.println("4. Membresia " + tipo.getNombre() + " registrada del "
                    + membresia.getFechaInicio() + " al " + membresia.getFechaFin());

            for (Membresia m : membresiaService.consultarVigencia(dni)) {
                System.out.println("5. Vigencia de la membresia " + m.getIdMembresia() + ": " + m.obtenerEstado(hoy));
            }

            Ingreso ingreso = ingresoService.registrarIngreso(dni);
            System.out.println("6. Ingreso registrado el " + ingreso.getFechaHora().withNano(0)
                    + " por " + ingreso.getUsuario().getUsername()
                    + " con la membresia " + ingreso.getMembresia().getIdMembresia());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Demostracion detenida: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioService.cerrarSesion();
            System.out.println("7. Sesion cerrada.");
        }
    }

    private static String leer(String etiqueta) {
        System.out.print(etiqueta);
        return ENTRADA.nextLine().trim();
    }

    /** La contrasena no se recorta: se compara tal como se escribe. */
    private static String leerClave() {
        System.out.print("Contrasena (visible al escribir): ");
        return ENTRADA.nextLine();
    }
}
