package com.tpoo.upn.gui;

import com.tpoo.upn.app.AppGUI;
import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.IngresoController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.model.Usuario;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Eventos de PrincipalView.fxml: menu lateral segun el rol, datos del usuario
 * autenticado y cierre de sesion. Cada opcion carga su vista y le entrega solo
 * los controladores que necesita.
 */
public class PrincipalViewController {

    @FXML private VBox menuRecepcion;
    @FXML private VBox menuAdministracion;
    @FXML private Button btnIngresos;
    @FXML private Button btnClientes;
    @FXML private Button btnMembresias;
    @FXML private Button btnConsultas;
    @FXML private Button btnTipos;
    @FXML private Button btnUsuarios;
    @FXML private Label lblUsuarioNombre;
    @FXML private Label lblUsuarioRol;
    @FXML private Label lblFecha;
    @FXML private ScrollPane scrollContenido;

    private AppGUI app;
    private Usuario usuario;
    private UsuarioController usuarioController;
    private ClienteController clienteController;
    private MembresiaController membresiaController;
    private IngresoController ingresoController;

    public void inicializar(AppGUI app, Usuario usuario, UsuarioController usuarioController,
            ClienteController clienteController, MembresiaController membresiaController,
            IngresoController ingresoController) {
        this.app = app;
        this.usuario = usuario;
        this.usuarioController = usuarioController;
        this.clienteController = clienteController;
        this.membresiaController = membresiaController;
        this.ingresoController = ingresoController;

        lblUsuarioNombre.setText(usuario.getNombreCompleto());
        lblUsuarioRol.setText(usuario.getRol());
        lblFecha.setText(Formato.fecha(LocalDate.now()));

        // Se ocultan las opciones de otro rol. Los servicios siguen comprobando los permisos.
        mostrarMenu(menuRecepcion, usuario.esRecepcionista());
        mostrarMenu(menuAdministracion, usuario.esAdministrador());

        if (usuario.esRecepcionista()) {
            mostrarIngresos();
        } else {
            mostrarConsultas();
        }
    }

    @FXML
    private void mostrarIngresos() {
        FXMLLoader cargador = AppGUI.cargarVista("ingresos");
        IngresosViewController vista = cargador.getController();
        vista.inicializar(clienteController, membresiaController, ingresoController);
        cambiarContenido(cargador.getRoot(), btnIngresos);
    }

    @FXML
    private void mostrarClientes() {
        FXMLLoader cargador = AppGUI.cargarVista("clientes");
        ClientesViewController vista = cargador.getController();
        vista.inicializar(clienteController);
        cambiarContenido(cargador.getRoot(), btnClientes);
    }

    @FXML
    private void mostrarMembresias() {
        FXMLLoader cargador = AppGUI.cargarVista("membresias");
        MembresiasViewController vista = cargador.getController();
        vista.inicializar(clienteController, membresiaController);
        cambiarContenido(cargador.getRoot(), btnMembresias);
    }

    @FXML
    private void mostrarConsultas() {
        FXMLLoader cargador = AppGUI.cargarVista("consultas");
        ConsultasViewController vista = cargador.getController();
        vista.inicializar(clienteController, membresiaController, ingresoController);
        cambiarContenido(cargador.getRoot(), btnConsultas);
    }

    @FXML
    private void mostrarTipos() {
        FXMLLoader cargador = AppGUI.cargarVista("tipos-membresia");
        TiposMembresiaViewController vista = cargador.getController();
        vista.inicializar(membresiaController);
        cambiarContenido(cargador.getRoot(), btnTipos);
    }

    @FXML
    private void mostrarUsuarios() {
        FXMLLoader cargador = AppGUI.cargarVista("usuarios");
        UsuariosViewController vista = cargador.getController();
        vista.inicializar(usuarioController, usuario);
        cambiarContenido(cargador.getRoot(), btnUsuarios);
    }

    /** Cierra la sesion mediante UsuarioController y vuelve a un login vacio. */
    @FXML
    private void cerrarSesion() {
        usuarioController.cerrarSesion();
        app.mostrarLogin();
    }

    /**
     * Cada vez se carga una vista nueva, asi no quedan datos ni resultados
     * de la pantalla anterior.
     */
    private void cambiarContenido(Parent vista, Button botonActivo) {
        scrollContenido.setContent(vista);
        scrollContenido.setVvalue(0);
        Button[] botones = { btnIngresos, btnClientes, btnMembresias, btnConsultas, btnTipos, btnUsuarios };
        for (Button boton : botones) {
            boton.getStyleClass().remove("nav-boton-activo");
        }
        botonActivo.getStyleClass().add("nav-boton-activo");
    }

    private void mostrarMenu(VBox menu, boolean visible) {
        menu.setVisible(visible);
        menu.setManaged(visible);
    }
}
