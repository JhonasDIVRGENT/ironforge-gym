package com.tpoo.upn.gui;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.model.Cliente;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Eventos de ClientesView.fxml (RF-01, RF-02, RF-03).
 * Un mismo formulario sirve para registrar (clienteEditado == null) y para
 * editar. Al editar, el DNI no se cambia porque identifica al cliente.
 */
public class ClientesViewController {

    @FXML private TextField txtBuscarDni;
    @FXML private Button btnBuscar;
    @FXML private Label lblMensajeBusqueda;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private Label lblSinClientes;
    @FXML private Label lblTotal;
    @FXML private Label lblTituloFormulario;
    @FXML private Label lblModo;
    @FXML private Label lblMensaje;
    @FXML private Label lblNotaDni;
    @FXML private TextField txtDni;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private Button btnGuardar;

    private ClienteController clienteController;
    /** Cliente que se esta editando; null cuando el formulario registra uno nuevo. */
    private Cliente clienteEditado;

    public void inicializar(ClienteController clienteController) {
        this.clienteController = clienteController;

        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreCompleto()));
        colDni.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDni()));
        colTelefono.setCellValueFactory(d -> new SimpleStringProperty(textoTelefono(d.getValue())));
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, cliente) -> {
            if (cliente != null) {
                editar(cliente);
            }
        });

        prepararNuevo();
        cargarClientes();
    }

    @FXML
    private void buscar() {
        String dni = txtBuscarDni.getText().trim();
        Tarea.ejecutar(() -> clienteController.buscarCliente(dni), cliente -> {
            tblClientes.getSelectionModel().clearSelection();
            if (cliente == null) {
                // RF-02: se indica que no existe y se deja el DNI listo para registrarlo.
                prepararNuevo();
                txtDni.setText(dni);
                Mensajes.info(lblMensajeBusqueda, "No existe un cliente registrado con el DNI " + dni
                        + ". Puede registrarlo con el formulario.");
            } else {
                editar(cliente);
                Mensajes.exito(lblMensajeBusqueda, "Cliente encontrado: " + Formato.cliente(cliente));
            }
        }, lblMensajeBusqueda, btnBuscar);
    }

    @FXML
    private void nuevoCliente() {
        tblClientes.getSelectionModel().clearSelection();
        prepararNuevo();
    }

    @FXML
    private void guardar() {
        String telefono = txtTelefono.getText().trim();
        Cliente datos;
        try {
            // El constructor de Cliente valida DNI, nombres y apellidos antes de llamar al servicio.
            if (clienteEditado == null) {
                datos = new Cliente(txtDni.getText().trim(), txtNombres.getText().trim(),
                        txtApellidos.getText().trim(), telefono.isEmpty() ? null : telefono);
            } else {
                datos = new Cliente(clienteEditado.getIdCliente(), clienteEditado.getDni(),
                        txtNombres.getText().trim(), txtApellidos.getText().trim(),
                        telefono.isEmpty() ? null : telefono);
            }
        } catch (IllegalArgumentException e) {
            Mensajes.error(lblMensaje, e.getMessage());
            return;
        }

        boolean esNuevo = clienteEditado == null;
        Tarea.ejecutar(() -> esNuevo ? clienteController.registrarCliente(datos)
                                     : clienteController.actualizarCliente(datos), guardado -> {
            if (!guardado) {
                Mensajes.error(lblMensaje, "No se guardaron los datos. Intente de nuevo.");
                return;
            }
            if (esNuevo) {
                prepararNuevo();
                Mensajes.exito(lblMensaje, "Cliente registrado: " + Formato.cliente(datos) + ".");
            } else {
                clienteEditado = datos;
                Mensajes.exito(lblMensaje, "Datos actualizados. Sus membresías e ingresos se conservan.");
            }
            cargarClientes();
        }, lblMensaje, btnGuardar);
    }

    private void cargarClientes() {
        lblSinClientes.setText("");
        Tarea.ejecutar(clienteController::listarClientes, clientes -> {
            tblClientes.getItems().setAll(clientes);
            lblTotal.setText(clientes.size() + " registrados");
            lblSinClientes.setText("Todavía no hay clientes registrados.");
        }, lblMensajeBusqueda);
    }

    private void prepararNuevo() {
        clienteEditado = null;
        lblTituloFormulario.setText("Nuevo cliente");
        lblModo.setText("REGISTRO");
        btnGuardar.setText("Guardar cliente");
        txtDni.setDisable(false);
        lblNotaDni.setVisible(false);
        lblNotaDni.setManaged(false);
        txtDni.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        Mensajes.limpiar(lblMensaje);
    }

    private void editar(Cliente cliente) {
        clienteEditado = cliente;
        lblTituloFormulario.setText("Editar cliente");
        lblModo.setText("EDICIÓN");
        btnGuardar.setText("Guardar cambios");
        txtDni.setText(cliente.getDni());
        txtDni.setDisable(true);
        lblNotaDni.setVisible(true);
        lblNotaDni.setManaged(true);
        txtNombres.setText(cliente.getNombres());
        txtApellidos.setText(cliente.getApellidos());
        txtTelefono.setText(cliente.getTelefono() == null ? "" : cliente.getTelefono());
        Mensajes.limpiar(lblMensaje);
    }

    private String textoTelefono(Cliente cliente) {
        return cliente.getTelefono() == null ? "—" : cliente.getTelefono();
    }
}
