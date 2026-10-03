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

// Como se muestran los datos en pantalla. Solo presentacion, sin reglas del gimnasio.
public class Formato {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    public static String fechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(FECHA_HORA);
    }

    public static String precio(double precio) {
        return String.format(Locale.US, "S/ %.2f", precio);
    }

    public static String cliente(Cliente c) {
        return c.getNombreCompleto() + " - DNI " + c.getDni();
    }

    public static String estado(String estado) {
        if (Membresia.VIGENTE.equals(estado)) {
            return "VIGENTE";
        }
        if (Membresia.VENCIDA.equals(estado)) {
            return "VENCIDA";
        }
        return "AÚN NO VIGENTE";
    }

    public static int contarVencidas(List<Membresia> membresias) {
        int vencidas = 0;
        for (Membresia m : membresias) {
            if (Membresia.VENCIDA.equals(m.obtenerEstado(LocalDate.now()))) {
                vencidas++;
            }
        }
        return vencidas;
    }

    // Solo oculta las vencidas en la tabla; no borra nada.
    public static List<Membresia> filtrarMembresias(List<Membresia> membresias, boolean conHistorial) {
        List<Membresia> visibles = new ArrayList<>();
        for (Membresia m : membresias) {
            if (conHistorial || !Membresia.VENCIDA.equals(m.obtenerEstado(LocalDate.now()))) {
                visibles.add(m);
            }
        }
        return visibles;
    }

    public static String textoHistorial(int vencidas) {
        return "Mostrar historial (" + vencidas + (vencidas == 1 ? " vencida)" : " vencidas)");
    }

    // Fecha de fin mas lejana, o null si no hay periodos.
    public static LocalDate ultimoVencimiento(List<Membresia> membresias) {
        LocalDate ultimo = null;
        for (Membresia m : membresias) {
            if (ultimo == null || m.getFechaFin().isAfter(ultimo)) {
                ultimo = m.getFechaFin();
            }
        }
        return ultimo;
    }

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

    // Celda de tabla que pinta el estado con su color.
    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> celdaEstado() {
        return columna -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean vacia) {
                super.updateItem(estado, vacia);
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

    public static StringConverter<Cliente> convertidorCliente() {
        return new StringConverter<>() {
            @Override
            public String toString(Cliente c) {
                return c == null ? "" : cliente(c);
            }

            @Override
            public Cliente fromString(String texto) {
                return null;
            }
        };
    }

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
