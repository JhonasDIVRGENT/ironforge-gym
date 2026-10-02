package com.tpoo.upn.gui;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.IngresoController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Eventos de IngresosView.fxml (RF-06, RF-07, RF-08).
 * La pantalla solo muestra las membresias: quien decide si el cliente puede
 * ingresar es IngresoService, que vuelve a validar al registrar.
 */
public class IngresosViewController {

    @FXML private TextField txtDni;
    @FXML private Button btnConsultar;
    @FXML private ComboBox<Cliente> cmbClientes;
    @FXML private Label lblMensaje;
    @FXML private Node panelVacio;
    @FXML private Node panelCliente;
    @FXML private Label lblNombre;
    @FXML private Label lblDni;
    @FXML private Label lblTelefono;
    @FXML private Label lblResumen;
    @FXML private CheckBox chkHistorial;
    @FXML private TableView<Membresia> tblMembresias;
    @FXML private TableColumn<Membresia, String> colTipo;
    @FXML private TableColumn<Membresia, String> colInicio;
    @FXML private TableColumn<Membresia, String> colFin;
    @FXML private TableColumn<Membresia, String> colEstado;
    @FXML private Label lblSinMembresias;
    @FXML private Button btnRegistrar;
    @FXML private Label lblMensajeIngreso;
    @FXML private Node panelResultado;
    @FXML private Label lblResultadoFecha;
    @FXML private Label lblResultadoDetalle;

    private ClienteController clienteController;
    private MembresiaController membresiaController;
    private IngresoController ingresoController;
    private Cliente clienteActual;
    /** Todos los periodos del cliente; la tabla muestra solo los que pasan el filtro. */
    private List<Membresia> membresiasCliente = new ArrayList<>();

    public void inicializar(ClienteController clienteController, MembresiaController membresiaController,
            IngresoController ingresoController) {
        this.clienteController = clienteController;
        this.membresiaController = membresiaController;
        this.ingresoController = ingresoController;

        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().getNombre()));
        colInicio.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaInicio())));
        colFin.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaFin())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().obtenerEstado(LocalDate.now())));
        colEstado.setCellFactory(Formato.celdaEstado());
        cmbClientes.setConverter(Formato.convertidorCliente());

        Tarea.ejecutar(clienteController::listarClientes,
                clientes -> cmbClientes.getItems().setAll(clientes),
                lblMensaje);
    }

    /** RF-02 desde recepcion: buscar por DNI con el servicio. */
    @FXML
    private void buscarPorDni() {
        String dni = txtDni.getText().trim();
        cmbClientes.getSelectionModel().clearSelection();
        Tarea.ejecutar(() -> clienteController.buscarCliente(dni), cliente -> {
            if (cliente == null) {
                ocultarCliente();
                Mensajes.info(lblMensaje, "No existe un cliente registrado con el DNI " + dni + ".");
            } else {
                mostrarCliente(cliente);
            }
        }, lblMensaje, btnConsultar);
    }

    @FXML
    private void seleccionarDeLista() {
        Cliente cliente = cmbClientes.getValue();
        if (cliente != null) {
            txtDni.setText(cliente.getDni());
            Mensajes.limpiar(lblMensaje);
            mostrarCliente(cliente);
        }
    }

    @FXML
    private void limpiar() {
        txtDni.clear();
        cmbClientes.getSelectionModel().clearSelection();
        Mensajes.limpiar(lblMensaje);
        ocultarCliente();
    }

    @FXML
    private void registrarIngreso() {
        Cliente cliente = clienteActual;
        if (cliente == null) {
            return;
        }
        mostrar(panelResultado, false);
        Tarea.ejecutar(() -> ingresoController.registrarIngreso(cliente.getDni()), ingreso -> {
            if (cliente != clienteActual) {
                return; // Mientras tanto se eligio otro cliente: este resultado ya no corresponde.
            }
            mostrarResultado(ingreso);
        }, lblMensajeIngreso, btnRegistrar);
    }

    private void mostrarCliente(Cliente cliente) {
        clienteActual = cliente;
        lblNombre.setText(cliente.getNombreCompleto());
        lblDni.setText("DNI " + cliente.getDni());
        lblTelefono.setText(cliente.getTelefono() == null ? "Sin teléfono" : "Teléfono: " + cliente.getTelefono());
        membresiasCliente = new ArrayList<>();
        tblMembresias.getItems().clear();
        lblSinMembresias.setText("");
        lblResumen.setText("CONSULTANDO MEMBRESÍAS...");
        chkHistorial.setSelected(false);
        mostrar(chkHistorial, false);
        Mensajes.limpiar(lblMensajeIngreso);
        mostrar(panelResultado, false);
        mostrar(panelVacio, false);
        mostrar(panelCliente, true);

        Tarea.ejecutar(() -> membresiaController.consultarVigencia(cliente.getDni()), membresias -> {
            if (cliente != clienteActual) {
                return;
            }
            membresiasCliente = membresias;
            aplicarFiltro();
            lblResumen.setText(Formato.resumenMembresias(membresias));
        }, lblMensaje);
    }

    @FXML
    private void cambiarHistorial() {
        aplicarFiltro();
    }

    /**
     * Por defecto se ven solo los periodos vigentes y programados, para no
     * confundir; las vencidas se ven al marcar "Mostrar historial".
     */
    private void aplicarFiltro() {
        int vencidas = Formato.contarVencidas(membresiasCliente);
        chkHistorial.setText(Formato.textoHistorial(vencidas));
        mostrar(chkHistorial, vencidas > 0);
        tblMembresias.getItems().setAll(Formato.filtrarMembresias(membresiasCliente, chkHistorial.isSelected()));
        if (membresiasCliente.isEmpty()) {
            lblSinMembresias.setText("El cliente no tiene membresías registradas.");
        } else {
            lblSinMembresias.setText("Sin membresías vigentes ni programadas. Marque «Mostrar historial» para ver las vencidas.");
        }
    }

    private void mostrarResultado(Ingreso ingreso) {
        Membresia membresia = ingreso.getMembresia();
        lblResultadoFecha.setText(Formato.fechaHora(ingreso.getFechaHora()));
        lblResultadoDetalle.setText(ingreso.getCliente().getNombreCompleto()
                + " · Membresía " + membresia.getTipo().getNombre()
                + " (vence " + Formato.fecha(membresia.getFechaFin()) + ")"
                + " · Registrado por " + ingreso.getUsuario().getUsername());
        mostrar(panelResultado, true);
        Mensajes.exito(lblMensajeIngreso, "Ingreso registrado correctamente.");
    }

    private void ocultarCliente() {
        clienteActual = null;
        membresiasCliente = new ArrayList<>();
        tblMembresias.getItems().clear();
        mostrar(panelCliente, false);
        mostrar(panelVacio, true);
    }

    private void mostrar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }
}
