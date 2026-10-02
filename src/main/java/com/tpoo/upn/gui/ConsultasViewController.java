package com.tpoo.upn.gui;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.IngresoController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Eventos de ConsultasView.fxml (RF-09 y RF-11), solo para el administrador.
 * Una lista vacia se muestra como "sin resultados"; un fallo de la base se
 * muestra aparte como error, para no confundirlos.
 */
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

    private MembresiaController membresiaController;
    private IngresoController ingresoController;

    public void inicializar(ClienteController clienteController, MembresiaController membresiaController,
            IngresoController ingresoController) {
        this.membresiaController = membresiaController;
        this.ingresoController = ingresoController;

        colFechaHora.setCellValueFactory(d -> new SimpleStringProperty(Formato.fechaHora(d.getValue().getFechaHora())));
        colRegistradoPor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsuario().getUsername()));
        colMembresia.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMembresia().getTipo().getNombre()));
        colCliente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCliente().getNombreCompleto()));
        colDni.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCliente().getDni()));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().getNombre()));
        colVence.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaFin())));
        cmbClientes.setConverter(Formato.convertidorCliente());

        // Solo lecturas: la lista de clientes para elegir y las proximas a vencer.
        Tarea.ejecutar(clienteController::listarClientes,
                clientes -> cmbClientes.getItems().setAll(clientes), lblMensajeHistorial);
        consultarPorVencer();
    }

    /** RF-09: historial de ingresos del cliente elegido, del mas reciente al mas antiguo. */
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
        Tarea.ejecutar(() -> ingresoController.consultarHistorial(cliente.getDni()), ingresos -> {
            tblHistorial.getItems().setAll(ingresos);
            lblResumenHistorial.setText(Formato.cliente(cliente).toUpperCase() + " · " + ingresos.size() + " INGRESOS");
            lblSinHistorial.setText("No existen ingresos registrados para este cliente.");
        }, lblMensajeHistorial, btnHistorial);
    }

    /** RF-11: membresias vigentes que vencen entre hoy y los proximos siete dias. */
    @FXML
    private void consultarPorVencer() {
        tblPorVencer.getItems().clear();
        lblSinPorVencer.setText("");
        Tarea.ejecutar(membresiaController::listarPorVencer, membresias -> {
            tblPorVencer.getItems().setAll(membresias);
            lblSinPorVencer.setText("No hay membresías vigentes que venzan en los próximos 7 días.");
        }, lblMensajeVencer, btnActualizar);
    }
}
