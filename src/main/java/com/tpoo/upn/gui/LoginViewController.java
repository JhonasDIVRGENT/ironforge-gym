package com.tpoo.upn.gui;

import com.tpoo.upn.app.AppGUI;
import com.tpoo.upn.controller.UsuarioController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Eventos de LoginView.fxml. El rol no se elige: lo trae el usuario autenticado.
 * Credenciales incorrectas o cuenta inactiva se informan con el mensaje del servicio.
 */
public class LoginViewController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Button btnIngresar;
    @FXML private Label lblMensaje;

    private AppGUI app;
    private UsuarioController usuarioController;

    public void inicializar(AppGUI app, UsuarioController usuarioController) {
        this.app = app;
        this.usuarioController = usuarioController;
    }

    @FXML
    private void iniciarSesion() {
        String username = txtUsuario.getText().trim();
        String clave = txtClave.getText();
        Tarea.ejecutar(() -> usuarioController.iniciarSesion(username, clave),
                usuario -> app.mostrarPrincipal(usuario),
                lblMensaje, btnIngresar);
    }
}
