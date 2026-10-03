package com.tpoo.upn.gui;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.IngresoService;
import com.tpoo.upn.service.MembresiaService;
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

// La pantalla solo muestra las membresias; quien decide el acceso es IngresoService.
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

    private ClienteService clienteService;
    private MembresiaService membresiaService;
    private IngresoService ingresoService;
    private Cliente clienteActual;
    private List<Membresia> membresiasCliente = new ArrayList<>();

    public void inicializar(ClienteService clienteService, MembresiaService membresiaService,
            IngresoService ingresoService) {
        this.clienteService = clienteService;
        this.membresiaService = membresiaService;
        this.ingresoService = ingresoService;

        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().getNombre()));
        colInicio.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaInicio())));
        colFin.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaFin())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().obtenerEstado(LocalDate.now())));
        colEstado.setCellFactory(Formato.celdaEstado());
        cmbClientes.setConverter(Formato.convertidorCliente());

        Tarea.ejecutar(clienteService::listarClientes,
                clientes -> cmbClientes.getItems().setAll(clientes), lblMensaje);
    }

    @FXML
    private void buscarPorDni() {
        String dni = txtDni.getText().trim();
        cmbClientes.getSelectionModel().clearSelection();
        Tarea.ejecutar(() -> clienteService.buscarCliente(dni), cliente -> {
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
        Tarea.ejecutar(() -> ingresoService.registrarIngreso(cliente.getDni()), ingreso -> {
            // Si mientras tanto se eligio otro cliente, este resultado ya no se muestra.
            if (cliente == clienteActual) {
                mostrarResultado(ingreso);
            }
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

        Tarea.ejecutar(() -> membresiaService.consultarVigencia(cliente.getDni()), membresias -> {
            if (cliente == clienteActual) {
                membresiasCliente = membresias;
                aplicarFiltro();
                lblResumen.setText(Formato.resumenMembresias(membresias));
            }
        }, lblMensaje);
    }

    @FXML
    private void cambiarHistorial() {
        aplicarFiltro();
    }

    // Por defecto solo se ven las vigentes y programadas; las vencidas, con "Mostrar historial".
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
