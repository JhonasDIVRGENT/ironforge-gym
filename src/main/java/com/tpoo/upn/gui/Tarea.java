package com.tpoo.upn.gui;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Ejecuta una llamada a los controladores (que consultan MySQL) en un hilo aparte,
 * para que la ventana no se congele mientras espera a la base de datos.
 * El resultado se entrega en el hilo de JavaFX, que es el unico que puede
 * modificar la pantalla.
 */
public class Tarea {

    /**
     * @param trabajo    lo que se hace en segundo plano (por ejemplo, registrar un ingreso)
     * @param alTerminar lo que se hace con el resultado, ya en el hilo de JavaFX
     * @param mensaje    etiqueta donde se informa un error
     * @param bloquear   botones que se deshabilitan mientras dura la operacion (evita doble envio)
     */
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

        Thread hilo = new Thread(tarea, "ironforge-bd");
        // Un hilo "daemon" no impide cerrar la aplicacion si la consulta sigue en curso.
        hilo.setDaemon(true);
        hilo.start();
    }

    private static void cambiarEstado(Node[] nodos, boolean deshabilitado) {
        for (Node nodo : nodos) {
            nodo.setDisable(deshabilitado);
        }
    }
}
