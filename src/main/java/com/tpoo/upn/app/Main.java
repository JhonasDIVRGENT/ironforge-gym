package com.tpoo.upn.app;

import javafx.application.Application;

// Sin module-info, Java no puede iniciar directamente una clase que extiende Application.
public class Main {

    public static void main(String[] args) {
        Application.launch(AppGUI.class, args);
    }
}
