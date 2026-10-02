package com.tpoo.upn.gui;

import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Eventos de UsuariosView.fxml (RF-13 y RF-14), solo para el administrador.
 * Las contrasenas nunca se muestran; el campo se vacia al terminar.
 */
public class UsuariosViewController {

    @FXML private Label lblMensajeCrear;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtUsername;
    @FXML private ComboBox<String> cmbRol;
    @FXML private PasswordField txtClave;
    @FXML private Button btnCrear;
    @FXML private Label lblMensajeEstado;
    @FXML private TextField txtUsernameEstado;
    @FXML private Button btnActivar;
    @FXML private Button btnDesactivar;

    private UsuarioController usuarioController;
    private Usuario usuarioActual;

    public void inicializar(UsuarioController usuarioController, Usuario usuarioActual) {
        this.usuarioController = usuarioController;
        this.usuarioActual = usuarioActual;
        cmbRol.getItems().setAll(Usuario.ROL_RECEPCIONISTA, Usuario.ROL_ADMINISTRADOR);
    }

    @FXML
    private void crearUsuario() {
        Usuario nuevo;
        try {
            // El constructor de Usuario valida los datos obligatorios y el rol.
            nuevo = new Usuario(txtNombres.getText().trim(), txtApellidos.getText().trim(),
                    txtUsername.getText().trim(), txtClave.getText(), cmbRol.getValue());
        } catch (IllegalArgumentException e) {
            Mensajes.error(lblMensajeCrear, e.getMessage());
            return;
        }

        Tarea.ejecutar(() -> usuarioController.crearUsuario(nuevo), creado -> {
            if (!creado) {
                Mensajes.error(lblMensajeCrear, "No se creó la cuenta. Intente de nuevo.");
                return;
            }
            txtNombres.clear();
            txtApellidos.clear();
            txtUsername.clear();
            txtClave.clear();
            cmbRol.getSelectionModel().clearSelection();
            Mensajes.exito(lblMensajeCrear, "Cuenta \"" + nuevo.getUsername() + "\" creada como "
                    + nuevo.getRol() + ". La cuenta está activa.");
        }, lblMensajeCrear, btnCrear);
    }

    @FXML
    private void activar() {
        cambiarEstado(true);
    }

    @FXML
    private void desactivar() {
        String username = txtUsernameEstado.getText().trim();
        if (!username.isEmpty()) {
            String texto = "La cuenta \"" + username + "\" no podrá iniciar sesión hasta que se active de nuevo.";
            if (username.equals(usuarioActual.getUsername())) {
                texto += "\n\nAtención: es la cuenta con la que inició sesión.";
            }
            if (!Mensajes.confirmar(btnDesactivar, "Desactivar cuenta", texto, "Desactivar")) {
                return;
            }
        }
        cambiarEstado(false);
    }

    private void cambiarEstado(boolean activo) {
        String username = txtUsernameEstado.getText().trim();
        Tarea.ejecutar(() -> usuarioController.cambiarEstadoUsuario(username, activo), cambiado -> {
            if (!cambiado) {
                Mensajes.error(lblMensajeEstado, "No se actualizó la cuenta. Intente de nuevo.");
                return;
            }
            Mensajes.exito(lblMensajeEstado, "Cuenta \"" + username + "\" "
                    + (activo ? "activada." : "desactivada."));
        }, lblMensajeEstado, btnActivar, btnDesactivar);
    }
}
