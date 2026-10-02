package com.tpoo.upn.app;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Utilidad pequena compartida por los casos de consola.
 * Solo lee lo que se escribe y presenta listas numeradas: no consulta la base de
 * datos ni aplica reglas del gimnasio. Las listas las obtiene cada caso
 * mediante los controladores.
 * Hay un unico Scanner para que dos lectores no se repartan la misma entrada.
 */
public class Consola {

    private static final Scanner ENTRADA = new Scanner(System.in);

    public static String leer(String etiqueta) {
        System.out.print(etiqueta);
        return ENTRADA.nextLine().trim();
    }

    /** La contrasena no se recorta: se compara tal como se escribe. */
    public static String leerClave(String etiqueta) {
        System.out.print(etiqueta + " (visible al escribir): ");
        return ENTRADA.nextLine();
    }

    /** Pide un numero entre 0 y maximo; repite la pregunta si se escribe otra cosa. */
    public static int leerOpcion(int maximo) {
        while (true) {
            String texto = leer("Opcion: ");
            try {
                int opcion = Integer.parseInt(texto);
                if (opcion >= 0 && opcion <= maximo) {
                    return opcion;
                }
            } catch (NumberFormatException e) {
                // Se muestra el mismo aviso que para un numero fuera de rango.
            }
            System.out.println("Escriba un numero entre 0 y " + maximo + ".");
        }
    }

    public static boolean confirmar(String pregunta) {
        String respuesta = leer(pregunta + " (s/n): ");
        return respuesta.equalsIgnoreCase("s");
    }

    /** Muestra los clientes numerados y devuelve el elegido, o null si se cancela. */
    public static Cliente elegirCliente(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados. Registre uno con RecepcionRegistrarCliente.");
            return null;
        }
        System.out.println("Clientes registrados:");
        for (int i = 0; i < clientes.size(); i++) {
            Cliente c = clientes.get(i);
            System.out.println((i + 1) + ". " + c.getNombreCompleto() + " - DNI " + c.getDni());
        }
        System.out.println("0. Cancelar");

        int opcion = leerOpcion(clientes.size());
        if (opcion == 0) {
            System.out.println("Operacion cancelada.");
            return null;
        }
        return clientes.get(opcion - 1);
    }

    /** Muestra cada periodo con el estado que calcula Membresia para el dia de hoy. */
    public static void mostrarMembresias(List<Membresia> membresias) {
        if (membresias.isEmpty()) {
            System.out.println("   El cliente no tiene membresias registradas.");
            return;
        }
        LocalDate hoy = LocalDate.now();
        for (Membresia m : membresias) {
            System.out.println("   " + m.getTipo().getNombre() + " | del " + m.getFechaInicio()
                    + " al " + m.getFechaFin() + " | " + m.obtenerEstado(hoy));
        }
    }
}
