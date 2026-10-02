package com.tpoo.upn.gui;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Formas de mostrar datos en pantalla: fechas, precios, estados y como se ve
 * un cliente o un tipo dentro de una lista. Solo presentacion, sin reglas.
 */
public class Formato {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    public static String fechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(FECHA_HORA);
    }

    /** Precio con punto decimal, igual que se escribe en el formulario. */
    public static String precio(double precio) {
        return String.format(Locale.US, "S/ %.2f", precio);
    }

    public static String cliente(Cliente c) {
        return c.getNombreCompleto() + " - DNI " + c.getDni();
    }

    /** Texto legible del estado que calcula Membresia.obtenerEstado. */
    public static String estado(String estado) {
        if (Membresia.VIGENTE.equals(estado)) {
            return "VIGENTE";
        }
        if (Membresia.VENCIDA.equals(estado)) {
            return "VENCIDA";
        }
        return "AÚN NO VIGENTE";
    }

    /** Cuantos periodos estan vencidos hoy. */
    public static int contarVencidas(List<Membresia> membresias) {
        int vencidas = 0;
        for (Membresia m : membresias) {
            if (Membresia.VENCIDA.equals(m.obtenerEstado(LocalDate.now()))) {
                vencidas++;
            }
        }
        return vencidas;
    }

    /**
     * Periodos que se muestran en la tabla: por defecto solo los vigentes y los
     * programados; con el historial activado, todos. No se borra nada: solo se oculta.
     */
    public static List<Membresia> filtrarMembresias(List<Membresia> membresias, boolean conHistorial) {
        List<Membresia> visibles = new ArrayList<>();
        for (Membresia m : membresias) {
            if (conHistorial || !Membresia.VENCIDA.equals(m.obtenerEstado(LocalDate.now()))) {
                visibles.add(m);
            }
        }
        return visibles;
    }

    /** Texto de la casilla de historial, por ejemplo "Mostrar historial (2 vencidas)". */
    public static String textoHistorial(int vencidas) {
        return "Mostrar historial (" + vencidas + (vencidas == 1 ? " vencida)" : " vencidas)");
    }

    /** Fecha de fin mas lejana entre los periodos, o null si no hay ninguno. */
    public static LocalDate ultimoVencimiento(List<Membresia> membresias) {
        LocalDate ultimo = null;
        for (Membresia m : membresias) {
            if (ultimo == null || m.getFechaFin().isAfter(ultimo)) {
                ultimo = m.getFechaFin();
            }
        }
        return ultimo;
    }

    /**
     * Resumen para la recepcionista: si hoy tiene una membresia vigente y hasta
     * cuando hay periodos registrados. La decision de acceso la toma el servicio.
     */
    public static String resumenMembresias(List<Membresia> membresias) {
        if (membresias.isEmpty()) {
            return "SIN MEMBRESÍAS REGISTRADAS";
        }
        boolean vigenteHoy = false;
        for (Membresia m : membresias) {
            if (m.estaVigente(LocalDate.now())) {
                vigenteHoy = true;
            }
        }
        return (vigenteHoy ? "MEMBRESÍA VIGENTE" : "SIN MEMBRESÍA VIGENTE")
                + " · ÚLTIMO VENCIMIENTO: " + fecha(ultimoVencimiento(membresias));
    }

    /** Celda de tabla que muestra el estado con su color (negro, rojo o gris). */
    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> celdaEstado() {
        return columna -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean vacia) {
                super.updateItem(estado, vacia);
                // Las celdas se reutilizan al desplazarse: se quita el color anterior.
                getStyleClass().removeAll("estado-vigente", "estado-vencida", "estado-pendiente");
                if (vacia || estado == null) {
                    setText(null);
                    return;
                }
                setText(estado(estado));
                if (Membresia.VIGENTE.equals(estado)) {
                    getStyleClass().add("estado-vigente");
                } else if (Membresia.VENCIDA.equals(estado)) {
                    getStyleClass().add("estado-vencida");
                } else {
                    getStyleClass().add("estado-pendiente");
                }
            }
        };
    }

    /** Muestra "Ana Torres - DNI 12345678" en los ComboBox de clientes. */
    public static StringConverter<Cliente> convertidorCliente() {
        return new StringConverter<>() {
            @Override
            public String toString(Cliente c) {
                return c == null ? "" : cliente(c);
            }

            @Override
            public Cliente fromString(String texto) {
                return null; // El ComboBox no es editable.
            }
        };
    }

    /** Muestra "Mensual - S/ 120.00" en el ComboBox de tipos. */
    public static StringConverter<TipoMembresia> convertidorTipo() {
        return new StringConverter<>() {
            @Override
            public String toString(TipoMembresia t) {
                return t == null ? "" : t.getNombre() + " - " + precio(t.getPrecio());
            }

            @Override
            public TipoMembresia fromString(String texto) {
                return null;
            }
        };
    }

    /** Muestra las fechas de los DatePicker como dd/MM/yyyy. */
    public static StringConverter<LocalDate> convertidorFecha() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha(fecha);
            }

            @Override
            public LocalDate fromString(String texto) {
                return texto == null || texto.isBlank() ? null : LocalDate.parse(texto.trim(), FECHA);
            }
        };
    }
}
