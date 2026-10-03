package com.tpoo.upn.gui;

import com.tpoo.upn.app.AppGUI;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

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
    private UsuarioService usuarioService;
    private ClienteService clienteService;
    private MembresiaService membresiaService;
    private IngresoService ingresoService;

    public void inicializar(AppGUI app, Usuario usuario, UsuarioService usuarioService,
            ClienteService clienteService, MembresiaService membresiaService, IngresoService ingresoService) {
        this.app = app;
        this.usuario = usuario;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.membresiaService = membresiaService;
        this.ingresoService = ingresoService;

        lblUsuarioNombre.setText(usuario.getNombreCompleto());
        lblUsuarioRol.setText(usuario.getRol());
        lblFecha.setText(Formato.fecha(LocalDate.now()));

        // Ocultar el menu de otro rol es solo comodidad: el permiso lo comprueba el servicio.
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
        vista.inicializar(clienteService, membresiaService, ingresoService);
        cambiarContenido(cargador.getRoot(), btnIngresos);
    }

    @FXML
    private void mostrarClientes() {
        FXMLLoader cargador = AppGUI.cargarVista("clientes");
        ClientesViewController vista = cargador.getController();
        vista.inicializar(clienteService);
        cambiarContenido(cargador.getRoot(), btnClientes);
    }

    @FXML
    private void mostrarMembresias() {
        FXMLLoader cargador = AppGUI.cargarVista("membresias");
        MembresiasViewController vista = cargador.getController();
        vista.inicializar(clienteService, membresiaService);
        cambiarContenido(cargador.getRoot(), btnMembresias);
    }

    @FXML
    private void mostrarConsultas() {
        FXMLLoader cargador = AppGUI.cargarVista("consultas");
        ConsultasViewController vista = cargador.getController();
        vista.inicializar(clienteService, membresiaService, ingresoService);
        cambiarContenido(cargador.getRoot(), btnConsultas);
    }

    @FXML
    private void mostrarTipos() {
        FXMLLoader cargador = AppGUI.cargarVista("tipos-membresia");
        TiposMembresiaViewController vista = cargador.getController();
        vista.inicializar(membresiaService);
        cambiarContenido(cargador.getRoot(), btnTipos);
    }

    @FXML
    private void mostrarUsuarios() {
        FXMLLoader cargador = AppGUI.cargarVista("usuarios");
        UsuariosViewController vista = cargador.getController();
        vista.inicializar(usuarioService, usuario);
        cambiarContenido(cargador.getRoot(), btnUsuarios);
    }

    @FXML
    private void cerrarSesion() {
        usuarioService.cerrarSesion();
        app.mostrarLogin();
    }

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
