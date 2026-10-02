package com.tpoo.upn.app;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.IngresoController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.controller.UsuarioController;
import com.tpoo.upn.gui.LoginViewController;
import com.tpoo.upn.gui.PrincipalViewController;
import com.tpoo.upn.model.Usuario;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.MembresiaService;
import com.tpoo.upn.service.UsuarioService;
import com.tpoo.upn.session.Sesion;
import java.io.IOException;
import java.io.UncheckedIOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Aplicacion grafica de IronForge Gym (JavaFX). Se inicia desde Main.
 * Crea una sola Sesion, la comparte con los cuatro servicios y entrega esos
 * servicios a los controladores existentes. Las vistas reciben despues los
 * controladores que necesitan: Vista FXML -> controlador de la vista ->
 * controlador -> servicio -> DAO.
 */
public class AppGUI extends Application {

    private static final String CSS = "/com/tpoo/upn/css/ironforge.css";
    private static final String ICONO = "/com/tpoo/upn/images/emblema.png";

    private Scene escena;
    private UsuarioController usuarioController;
    private ClienteController clienteController;
    private MembresiaController membresiaController;
    private IngresoController ingresoController;

    @Override
    public void start(Stage ventana) {
        Sesion sesion = new Sesion();
        usuarioController = new UsuarioController(new UsuarioService(sesion));
        clienteController = new ClienteController(new ClienteService(sesion));
        membresiaController = new MembresiaController(new MembresiaService(sesion));
        ingresoController = new IngresoController(new IngresoService(sesion));

        ventana.setTitle("IronForge Gym");
        ventana.getIcons().add(new Image(AppGUI.class.getResource(ICONO).toExternalForm()));
        ventana.setMinWidth(1024);
        ventana.setMinHeight(640);
        mostrarLogin();
        ventana.setScene(escena);
        ventana.show();
    }

    /** Muestra login.fxml con campos vacios (tambien despues de cerrar sesion). */
    public void mostrarLogin() {
        FXMLLoader cargador = cargarVista("login");
        LoginViewController vista = cargador.getController();
        vista.inicializar(this, usuarioController);
        mostrar(cargador.getRoot());
    }

    /** Muestra principal.fxml con el menu que corresponde al rol del usuario. */
    public void mostrarPrincipal(Usuario usuario) {
        FXMLLoader cargador = cargarVista("principal");
        PrincipalViewController vista = cargador.getController();
        vista.inicializar(this, usuario, usuarioController, clienteController,
                membresiaController, ingresoController);
        mostrar(cargador.getRoot());
    }

    /**
     * Una sola escena para toda la aplicacion: la primera vez se crea con la vista
     * recibida; despues solo se reemplaza su contenido.
     */
    private void mostrar(Parent raiz) {
        if (escena == null) {
            escena = new Scene(raiz, 1280, 800);
            escena.getStylesheets().add(AppGUI.class.getResource(CSS).toExternalForm());
        } else {
            escena.setRoot(raiz);
        }
    }

    /**
     * Carga una vista desde src/main/resources/com/tpoo/upn/view/{nombre}.fxml.
     * FXMLLoader crea los controles definidos en el FXML y su ViewController.
     */
    public static FXMLLoader cargarVista(String nombre) {
        FXMLLoader cargador = new FXMLLoader(AppGUI.class.getResource("/com/tpoo/upn/view/" + nombre + ".fxml"));
        try {
            cargador.load();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar la vista " + nombre, e);
        }
        return cargador;
    }
}
