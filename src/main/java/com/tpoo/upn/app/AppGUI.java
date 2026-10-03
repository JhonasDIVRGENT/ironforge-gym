package com.tpoo.upn.app;

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

public class AppGUI extends Application {

    private static final String CSS = "/com/tpoo/upn/css/ironforge.css";
    private static final String ICONO = "/com/tpoo/upn/images/emblema.png";

    private Scene escena;
    private UsuarioService usuarioService;
    private ClienteService clienteService;
    private MembresiaService membresiaService;
    private IngresoService ingresoService;

    @Override
    public void start(Stage ventana) {
        // Una sola Sesion compartida por los cuatro servicios.
        Sesion sesion = new Sesion();
        usuarioService = new UsuarioService(sesion);
        clienteService = new ClienteService(sesion);
        membresiaService = new MembresiaService(sesion);
        ingresoService = new IngresoService(sesion);

        ventana.setTitle("IronForge Gym");
        ventana.getIcons().add(new Image(AppGUI.class.getResource(ICONO).toExternalForm()));
        ventana.setMinWidth(1024);
        ventana.setMinHeight(640);
        mostrarLogin();
        ventana.setScene(escena);
        ventana.show();
    }

    public void mostrarLogin() {
        FXMLLoader cargador = cargarVista("login");
        LoginViewController vista = cargador.getController();
        vista.inicializar(this, usuarioService);
        mostrar(cargador.getRoot());
    }

    public void mostrarPrincipal(Usuario usuario) {
        FXMLLoader cargador = cargarVista("principal");
        PrincipalViewController vista = cargador.getController();
        vista.inicializar(this, usuario, usuarioService, clienteService, membresiaService, ingresoService);
        mostrar(cargador.getRoot());
    }

    private void mostrar(Parent raiz) {
        if (escena == null) {
            escena = new Scene(raiz, 1280, 800);
            escena.getStylesheets().add(AppGUI.class.getResource(CSS).toExternalForm());
        } else {
            escena.setRoot(raiz);
        }
    }

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
