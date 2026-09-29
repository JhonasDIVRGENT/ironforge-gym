package com.tpoo.upn.gui;

import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.session.Sesion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DashboardView {

    private final Sesion sesion;

    public DashboardView(Sesion sesion) {
        this.sesion = sesion;
    }

    public void mostrar(Stage stage) {
        Usuario user = sesion.getUsuarioActual();
        String rol = sesion.esAdministrador() ? "ADMINISTRADOR" : "RECEPCIONISTA";

        stage.setTitle("IronForge Gym - Panel Principal");

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #0f172a;");

        // Barra superior
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 0 1 0;");

        Label lblLogo = new Label("⚡ IRONFORGE GYM");
        lblLogo.setTextFill(Color.web("#a855f7"));
        lblLogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblUsuario = new Label("👤 " + (user != null ? user.getUsername() : "Usuario") + " | " + rol);
        lblUsuario.setTextFill(Color.web("#94a3b8"));
        lblUsuario.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        Button btnCerrarSesion = new Button("Cerrar Sesión");
        btnCerrarSesion.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12;");
        btnCerrarSesion.setOnAction(e -> {
            sesion.cerrar();
            new LoginView().start(stage);
        });

        topBar.getChildren().addAll(lblLogo, spacer, lblUsuario, btnCerrarSesion);
        layout.setTop(topBar);

        // Panel de pestañas
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #0f172a;");

        // 1. Pestaña de Accesos / Ingresos
        Tab tabAccesos = new Tab("Control de Accesos");
        tabAccesos.setClosable(false);
        tabAccesos.setContent(crearModuloAccesos());

        // 2. Pestaña de Clientes y Membresías
        Tab tabClientes = new Tab("Clientes y Membresías");
        tabClientes.setClosable(false);
        tabClientes.setContent(crearModuloClientes());

        tabPane.getTabs().addAll(tabAccesos, tabClientes);

        // 3. Pestaña de Administración (Solo si es Administrador)
        if (sesion.esAdministrador()) {
            Tab tabAdmin = new Tab("Gestión de Usuarios");
            tabAdmin.setClosable(false);
            tabAdmin.setContent(crearModuloUsuarios());
            tabPane.getTabs().add(tabAdmin);
        }

        layout.setCenter(tabPane);

        Scene scene = new Scene(layout, 950, 620);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private VBox crearModuloAccesos() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));

        Label lbl = new Label("Registro Rápido de Ingreso de Clientes");
        lbl.setTextFill(Color.web("#cbd5e1"));
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        HBox form = new HBox(12);
        form.setAlignment(Pos.CENTER_LEFT);

        TextField txtDni = new TextField();
        txtDni.setPromptText("Ingrese DNI del cliente");
        txtDni.setPrefWidth(220);
        txtDni.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-border-color: #475569; -fx-border-radius: 6; -fx-padding: 8;");

        Button btnVerificar = new Button("Registrar Entrada");
        btnVerificar.setStyle("-fx-background-color: #9333ea; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");

        Label lblResultado = new Label();
        lblResultado.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        btnVerificar.setOnAction(e -> {
            String dni = txtDni.getText().trim();
            if (dni.isEmpty()) {
                lblResultado.setText("⚠️ Ingrese un DNI válido.");
                lblResultado.setTextFill(Color.web("#f59e0b"));
            } else {
                lblResultado.setText("✅ Ingreso autorizado para el DNI " + dni + " (Membresía activa).");
                lblResultado.setTextFill(Color.web("#22c55e"));
                txtDni.clear();
            }
        });

        form.getChildren().addAll(txtDni, btnVerificar, lblResultado);
        box.getChildren().addAll(lbl, form);
        return box;
    }

    private VBox crearModuloClientes() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));

        Label lbl = new Label("Padrón de Clientes y Estado de Planes");
        lbl.setTextFill(Color.web("#cbd5e1"));
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TableView<String> tabla = new TableView<>();
        TableColumn<String, String> colDni = new TableColumn<>("DNI");
        TableColumn<String, String> colNombre = new TableColumn<>("Nombre Completo");
        TableColumn<String, String> colPlan = new TableColumn<>("Plan");
        TableColumn<String, String> colEstado = new TableColumn<>("Estado");

        colDni.setPrefWidth(120);
        colNombre.setPrefWidth(250);
        colPlan.setPrefWidth(150);
        colEstado.setPrefWidth(120);

        tabla.getColumns().addAll(colDni, colNombre, colPlan, colEstado);
        tabla.setPlaceholder(new Label("No hay clientes pendientes de validación."));

        box.getChildren().addAll(lbl, tabla);
        return box;
    }

    private VBox crearModuloUsuarios() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));

        Label lbl = new Label("Control de Accesos de Personal (Módulo Exclusivo Admin)");
        lbl.setTextFill(Color.web("#cbd5e1"));
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label desc = new Label("Permite activar, suspender o registrar recepcionistas del gimnasio.");
        desc.setTextFill(Color.web("#94a3b8"));

        box.getChildren().addAll(lbl, desc);
        return box;
    }
}