package com.tpoo.upn.gui;

import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.service.MembresiaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TiposMembresiaViewController {

    @FXML private TableView<TipoMembresia> tblTipos;
    @FXML private TableColumn<TipoMembresia, String> colNombre;
    @FXML private TableColumn<TipoMembresia, String> colPrecio;
    @FXML private Label lblSinTipos;
    @FXML private Label lblTotal;
    @FXML private Label lblMensajeLista;
    @FXML private Label lblMensaje;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private Button btnRegistrar;

    private MembresiaService membresiaService;

    public void inicializar(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty(Formato.precio(d.getValue().getPrecio())));
        cargarTipos();
    }

    @FXML
    private void registrar() {
        TipoMembresia tipo;
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
            tipo = new TipoMembresia(txtNombre.getText().trim(), precio);
        } catch (NumberFormatException e) {
            Mensajes.error(lblMensaje, "El precio debe ser un número, por ejemplo 120.00.");
            return;
        } catch (IllegalArgumentException e) {
            Mensajes.error(lblMensaje, e.getMessage());
            return;
        }

        Tarea.ejecutar(() -> membresiaService.registrarTipo(tipo), guardado -> {
            if (!guardado) {
                Mensajes.error(lblMensaje, "No se guardó el tipo. Intente de nuevo.");
                return;
            }
            txtNombre.clear();
            txtPrecio.clear();
            Mensajes.exito(lblMensaje, "Tipo registrado: " + tipo.getNombre() + " (" + Formato.precio(tipo.getPrecio()) + ").");
            cargarTipos();
        }, lblMensaje, btnRegistrar);
    }

    private void cargarTipos() {
        lblSinTipos.setText("");
        Tarea.ejecutar(membresiaService::listarTipos, tipos -> {
            tblTipos.getItems().setAll(tipos);
            lblTotal.setText(tipos.size() + " registrados");
            lblSinTipos.setText("Todavía no hay tipos de membresía registrados.");
        }, lblMensajeLista);
    }
}
