package com.tpoo.upn.gui;

import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

/**
 * Muestra en pantalla el resultado de una operacion: exito, aviso o error.
 * Los errores siguen la misma estrategia que la consola (AGENTS.md, seccion 7):
 * se muestra el motivo de las reglas y un texto generico ante fallos de la base,
 * sin credenciales, SQL ni trazas tecnicas.
 */
public class Mensajes {

    private static final String[] CLASES = { "mensaje-exito", "mensaje-error", "mensaje-info" };

    public static void exito(Label etiqueta, String texto) {
        mostrar(etiqueta, texto, "mensaje-exito");
    }

    public static void info(Label etiqueta, String texto) {
        mostrar(etiqueta, texto, "mensaje-info");
    }

    public static void error(Label etiqueta, String texto) {
        mostrar(etiqueta, texto, "mensaje-error");
    }

    public static void error(Label etiqueta, Throwable causa) {
        error(etiqueta, textoError(causa));
    }

    /** Oculta la etiqueta para que no quede un recuadro vacio. */
    public static void limpiar(Label etiqueta) {
        etiqueta.setText("");
        etiqueta.setVisible(false);
        etiqueta.setManaged(false);
    }

    /** Traduce una excepcion del backend a un texto comprensible para el usuario. */
    public static String textoError(Throwable causa) {
        if (causa instanceof IllegalStateException) {
            return "Operación no permitida: " + causa.getMessage();
        }
        if (causa instanceof IllegalArgumentException) {
            return causa.getMessage();
        }
        if (causa instanceof SQLException) {
            return "No se pudo completar la operación por un problema de conexión con la base de datos. "
                    + "Verifique que MySQL esté activo e intente de nuevo.";
        }
        // Un error no previsto se deja en la consola del programa para el equipo, no en la ventana.
        causa.printStackTrace();
        return "Ocurrió un error inesperado. Intente de nuevo.";
    }

    /** Pide confirmacion antes de una accion delicada. Devuelve true si se acepta. */
    public static boolean confirmar(Node origen, String titulo, String texto, String textoAccion) {
        ButtonType aceptar = new ButtonType(textoAccion, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, texto, aceptar, cancelar);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.initOwner(origen.getScene().getWindow());
        alerta.getDialogPane().getStylesheets().addAll(origen.getScene().getStylesheets());

        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == aceptar;
    }

    private static void mostrar(Label etiqueta, String texto, String clase) {
        etiqueta.getStyleClass().removeAll(CLASES);
        etiqueta.getStyleClass().add(clase);
        etiqueta.setText(texto);
        etiqueta.setVisible(true);
        etiqueta.setManaged(true);
    }
}
