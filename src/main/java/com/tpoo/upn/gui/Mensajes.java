package com.tpoo.upn.gui;

import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

// Muestra mensajes de exito, aviso o error. Ante un fallo de MySQL no muestra detalles tecnicos.
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

    public static void limpiar(Label etiqueta) {
        etiqueta.setText("");
        etiqueta.setVisible(false);
        etiqueta.setManaged(false);
    }

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
        causa.printStackTrace();
        return "Ocurrió un error inesperado. Intente de nuevo.";
    }

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
