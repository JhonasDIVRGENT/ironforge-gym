package com.tpoo.upn.gui;

import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.session.Sesion;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView extends Application {

    private final Sesion sesion = new Sesion();
    private final UsuarioController usuarioController = new UsuarioController(sesion);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("IronForge Gym - Iniciar Sesión");

        // Contenedor principal con fondo oscuro
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #0f172a;");

        // Título del Gimnasio
        Label lblTitulo = new Label("IRONFORGE GYM");
        lblTitulo.setTextFill(Color.web("#a855f7")); // Morado neón
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));

        Label lblSubtitulo = new Label("Control de Acceso y Gestión");
        lblSubtitulo.setTextFill(Color.web("#94a3b8"));
        lblSubtitulo.setFont(Font.font("Segoe UI", 13));

        // Tarjeta central (Card)
        VBox card = new VBox(14);
        card.setMaxWidth(340);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-radius: 12;");

        // Campo Usuario
        Label lblUser = new Label("Usuario");
        lblUser.setTextFill(Color.web("#cbd5e1"));
        lblUser.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));

        TextField txtUsuario = new TextField();
        txtUsuario.setPromptText("Ej. admin");
        txtUsuario.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-border-color: #475569; -fx-border-radius: 6; -fx-padding: 8;");

        // Campo Contraseña
        Label lblPass = new Label("Contraseña");
        lblPass.setTextFill(Color.web("#cbd5e1"));
        lblPass.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("••••••••");
        txtPassword.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-border-color: #475569; -fx-border-radius: 6; -fx-padding: 8;");

        // Mensaje de estado/error
        Label lblMensaje = new Label();
        lblMensaje.setWrapText(true);
        lblMensaje.setFont(Font.font("Segoe UI", 12));

        // Botón Ingresar
        Button btnLogin = new Button("INGRESAR");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle("-fx-background-color: #9333ea; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 6; -fx-cursor: hand;");

        btnLogin.setOnAction(e -> {
            String user = txtUsuario.getText().trim();
            String pass = txtPassword.getText().trim();

            try {
                Usuario usuario = usuarioController.iniciarSesion(user, pass);
                lblMensaje.setText("¡Bienvenido/a, " + usuario.getUsername() + "!");
                lblMensaje.setTextFill(Color.web("#22c55e"));

                // Transición al Dashboard
                DashboardView dashboard = new DashboardView(sesion);
                dashboard.mostrar(primaryStage);

            } catch (IllegalArgumentException ex) {
                lblMensaje.setText(ex.getMessage());
                lblMensaje.setTextFill(Color.web("#ef4444"));
            } catch (SQLException ex) {
                lblMensaje.setText("Error al conectar con la base de datos.");
                lblMensaje.setTextFill(Color.web("#ef4444"));
            }
        });

        card.getChildren().addAll(lblUser, txtUsuario, lblPass, txtPassword, btnLogin, lblMensaje);
        root.getChildren().addAll(lblTitulo, lblSubtitulo, card);

        Scene scene = new Scene(root, 420, 520);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}