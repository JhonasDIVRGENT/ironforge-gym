package com.tpoo.upn.gui;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;

// Ejecuta una consulta a MySQL en otro hilo para que la ventana no se congele.
// El resultado se aplica en el hilo de JavaFX y los botones quedan bloqueados mientras dura.
public class Tarea {

    public static <T> void ejecutar(Callable<T> trabajo, Consumer<T> alTerminar,
            Label mensaje, Node... bloquear) {
        cambiarEstado(bloquear, true);
        Mensajes.limpiar(mensaje);

        Task<T> tarea = new Task<>() {
            @Override
            protected T call() throws Exception {
                return trabajo.call();
            }
        };
        tarea.setOnSucceeded(evento -> {
            cambiarEstado(bloquear, false);
            alTerminar.accept(tarea.getValue());
        });
        tarea.setOnFailed(evento -> {
            cambiarEstado(bloquear, false);
            Mensajes.error(mensaje, tarea.getException());
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private static void cambiarEstado(Node[] nodos, boolean deshabilitado) {
        for (Node nodo : nodos) {
            nodo.setDisable(deshabilitado);
        }
    }
}
