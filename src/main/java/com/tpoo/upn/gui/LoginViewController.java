package com.tpoo.upn.gui;

import com.tpoo.upn.app.AppGUI;
import com.tpoo.upn.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Button btnIngresar;
    @FXML private Label lblMensaje;

    private AppGUI app;
    private UsuarioService usuarioService;

    public void inicializar(AppGUI app, UsuarioService usuarioService) {
        this.app = app;
        this.usuarioService = usuarioService;
    }

    @FXML
    private void iniciarSesion() {
        String username = txtUsuario.getText().trim();
        String clave = txtClave.getText();
        Tarea.ejecutar(() -> usuarioService.iniciarSesion(username, clave),
                usuario -> app.mostrarPrincipal(usuario),
                lblMensaje, btnIngresar);
    }
}
