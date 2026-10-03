package com.tpoo.upn.gui;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.MembresiaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ConsultasViewController {

    @FXML private ComboBox<Cliente> cmbClientes;
    @FXML private Button btnHistorial;
    @FXML private Label lblMensajeHistorial;
    @FXML private Label lblResumenHistorial;
    @FXML private TableView<Ingreso> tblHistorial;
    @FXML private TableColumn<Ingreso, String> colFechaHora;
    @FXML private TableColumn<Ingreso, String> colRegistradoPor;
    @FXML private TableColumn<Ingreso, String> colMembresia;
    @FXML private Label lblSinHistorial;
    @FXML private Button btnActualizar;
    @FXML private Label lblMensajeVencer;
    @FXML private TableView<Membresia> tblPorVencer;
    @FXML private TableColumn<Membresia, String> colCliente;
    @FXML private TableColumn<Membresia, String> colDni;
    @FXML private TableColumn<Membresia, String> colTipo;
    @FXML private TableColumn<Membresia, String> colVence;
    @FXML private Label lblSinPorVencer;

    private MembresiaService membresiaService;
    private IngresoService ingresoService;

    public void inicializar(ClienteService clienteService, MembresiaService membresiaService,
            IngresoService ingresoService) {
        this.membresiaService = membresiaService;
        this.ingresoService = ingresoService;

        colFechaHora.setCellValueFactory(d -> new SimpleStringProperty(Formato.fechaHora(d.getValue().getFechaHora())));
        colRegistradoPor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsuario().getUsername()));
        colMembresia.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMembresia().getTipo().getNombre()));
        colCliente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCliente().getNombreCompleto()));
        colDni.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCliente().getDni()));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().getNombre()));
        colVence.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaFin())));
        cmbClientes.setConverter(Formato.convertidorCliente());

        Tarea.ejecutar(clienteService::listarClientes,
                clientes -> cmbClientes.getItems().setAll(clientes), lblMensajeHistorial);
        consultarPorVencer();
    }

    @FXML
    private void consultarHistorial() {
        Cliente cliente = cmbClientes.getValue();
        if (cliente == null) {
            Mensajes.error(lblMensajeHistorial, "Seleccione un cliente.");
            return;
        }
        tblHistorial.getItems().clear();
        lblResumenHistorial.setText("");
        lblSinHistorial.setText("");
        Tarea.ejecutar(() -> ingresoService.consultarHistorial(cliente.getDni()), ingresos -> {
            tblHistorial.getItems().setAll(ingresos);
            lblResumenHistorial.setText(Formato.cliente(cliente).toUpperCase() + " · " + ingresos.size() + " INGRESOS");
            lblSinHistorial.setText("No existen ingresos registrados para este cliente.");
        }, lblMensajeHistorial, btnHistorial);
    }

    @FXML
    private void consultarPorVencer() {
        tblPorVencer.getItems().clear();
        lblSinPorVencer.setText("");
        Tarea.ejecutar(membresiaService::listarPorVencer, membresias -> {
            tblPorVencer.getItems().setAll(membresias);
            lblSinPorVencer.setText("No hay membresías vigentes que venzan en los próximos 7 días.");
        }, lblMensajeVencer, btnActualizar);
    }
}
