package com.tpoo.upn.app;

import javafx.application.Application;

/**
 * Punto de entrada de la aplicacion grafica 
 * Solo arranca AppGUI, que crea la Sesion, los servicios y los controladores.
 * Es una clase aparte porque, en un proyecto sin module-info, Java no puede
 * iniciar directamente una clase que extiende Application desde el classpath.
 * Los casos de consola (Recepcion*, Admin*) se siguen ejecutando por separado.
 */
public class Main {

    public static void main(String[] args) {
        Application.launch(AppGUI.class, args);
    }
}
