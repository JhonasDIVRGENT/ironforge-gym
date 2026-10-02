package com.tpoo.upn.app;

import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Demostracion de consultas del administrador (RF-09, RF-11, RF-15).
 * Necesita una cuenta ADMINISTRADOR activa y el DNI de un cliente existente.
 * Solo lee datos, salvo que se escriba el nombre de un tipo de membresia nuevo
 * en el ultimo paso (Enter lo omite).
 */
public class PruebaConsultas {

    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        Sesion sesion = new Sesion();
        UsuarioService usuarioService = new UsuarioService(sesion);
        MembresiaService membresiaService = new MembresiaService(sesion);
        IngresoService ingresoService = new IngresoService(sesion);

        System.out.println("=== Prueba de consultas del administrador ===");

        try {
            Usuario admin = usuarioService.iniciarSesion(leer("Username del administrador: "), leerClave());
            System.out.println("1. Sesion iniciada como " + admin.getRol());

            String dni = leer("DNI para consultar el historial: ");
            List<Ingreso> historial = ingresoService.consultarHistorial(dni);
            System.out.println("2. Historial de ingresos (" + historial.size() + "):");
            if (historial.isEmpty()) {
                System.out.println("   No existen ingresos registrados para este cliente.");
            }
            for (Ingreso i : historial) {
                System.out.println("   " + i.getFechaHora() + " | registrado por " + i.getUsuario().getUsername());
            }

            List<Membresia> porVencer = membresiaService.listarPorVencer();
            System.out.println("3. Membresias vigentes que vencen en los proximos 7 dias:");
            if (porVencer.isEmpty()) {
                System.out.println("   No existen membresias proximas a vencer.");
            }
            for (Membresia m : porVencer) {
                System.out.println("   " + m.getCliente().getNombreCompleto() + " | DNI "
                        + m.getCliente().getDni() + " | vence " + m.getFechaFin());
            }

            System.out.println("4. Tipos de membresia:");
            mostrarTipos(membresiaService);

            String nombre = leer("Nombre de un tipo NUEVO (Enter para no registrar ninguno): ");
            if (!nombre.isEmpty()) {
                double precio = Double.parseDouble(leer("Precio, por ejemplo 99.90: "));
                membresiaService.registrarTipo(new TipoMembresia(nombre, precio));
                System.out.println("5. Tipo registrado. Tipos actuales:");
                mostrarTipos(membresiaService);
            }
        } catch (NumberFormatException e) {
            System.out.println("El precio debe ser un numero; no se registro el tipo.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Demostracion detenida: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos. Compruebe que MySQL este activo.");
        } finally {
            usuarioService.cerrarSesion();
        }
    }

    private static void mostrarTipos(MembresiaService membresiaService) throws SQLException {
        for (TipoMembresia t : membresiaService.listarTipos()) {
            System.out.println("   id " + t.getIdTipo() + " | " + t.getNombre() + " | S/ " + t.getPrecio());
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
