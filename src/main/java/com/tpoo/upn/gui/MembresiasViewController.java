package com.tpoo.upn.gui;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.service.ClienteService;
import com.tpoo.upn.service.MembresiaService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

// Las fechas las elige el recepcionista; las reglas (sin superposicion, etc.) las valida MembresiaService.
public class MembresiasViewController {

    @FXML private TextField txtDni;
    @FXML private Button btnConsultar;
    @FXML private ComboBox<Cliente> cmbClientes;
    @FXML private Label lblMensajeCliente;
    @FXML private Label lblClienteSeleccionado;
    @FXML private Label lblResumen;
    @FXML private CheckBox chkHistorial;
    @FXML private TableView<Membresia> tblMembresias;
    @FXML private TableColumn<Membresia, String> colTipo;
    @FXML private TableColumn<Membresia, String> colInicio;
    @FXML private TableColumn<Membresia, String> colFin;
    @FXML private TableColumn<Membresia, String> colEstado;
    @FXML private Label lblSinMembresias;
    @FXML private Label lblMensaje;
    @FXML private ComboBox<TipoMembresia> cmbTipos;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private Button btnRegistrar;
    @FXML private Button btnRenovar;

    private ClienteService clienteService;
    private MembresiaService membresiaService;
    private Cliente clienteActual;
    private List<Membresia> membresiasCliente = new ArrayList<>();

    public void inicializar(ClienteService clienteService, MembresiaService membresiaService) {
        this.clienteService = clienteService;
        this.membresiaService = membresiaService;

        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().getNombre()));
        colInicio.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaInicio())));
        colFin.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaFin())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().obtenerEstado(LocalDate.now())));
        colEstado.setCellFactory(Formato.celdaEstado());
        cmbClientes.setConverter(Formato.convertidorCliente());
        cmbTipos.setConverter(Formato.convertidorTipo());
        dpInicio.setConverter(Formato.convertidorFecha());
        dpFin.setConverter(Formato.convertidorFecha());

        Tarea.ejecutar(clienteService::listarClientes,
                clientes -> cmbClientes.getItems().setAll(clientes), lblMensajeCliente);
        Tarea.ejecutar(membresiaService::listarTipos, tipos -> {
            cmbTipos.getItems().setAll(tipos);
            if (tipos.isEmpty()) {
                Mensajes.info(lblMensaje, "No hay tipos de membresía. Un administrador debe registrarlos primero.");
            }
        }, lblMensaje);
    }

    @FXML
    private void buscarPorDni() {
        String dni = txtDni.getText().trim();
        cmbClientes.getSelectionModel().clearSelection();
        Tarea.ejecutar(() -> clienteService.buscarCliente(dni), cliente -> {
            if (cliente == null) {
                quitarCliente();
                Mensajes.info(lblMensajeCliente, "No existe un cliente registrado con el DNI " + dni + ".");
            } else {
                mostrarCliente(cliente);
            }
        }, lblMensajeCliente, btnConsultar);
    }

    @FXML
    private void seleccionarDeLista() {
        Cliente cliente = cmbClientes.getValue();
        if (cliente != null) {
            txtDni.setText(cliente.getDni());
            Mensajes.limpiar(lblMensajeCliente);
            mostrarCliente(cliente);
        }
    }

    @FXML
    private void registrar() {
        guardar(false);
    }

    @FXML
    private void renovar() {
        guardar(true);
    }

    private void guardar(boolean esRenovacion) {
        Cliente cliente = clienteActual;
        if (cliente == null) {
            Mensajes.error(lblMensaje, "Primero busque o seleccione un cliente.");
            return;
        }
        TipoMembresia tipo = cmbTipos.getValue();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();

        // Si falta algun dato no se pregunta: el servicio lo rechaza con su motivo.
        if (tipo != null && inicio != null && fin != null && !confirmarFechas(cliente, tipo, inicio, fin, esRenovacion)) {
            return;
        }

        Tarea.ejecutar(() -> esRenovacion
                ? membresiaService.renovarMembresia(cliente, tipo, inicio, fin)
                : membresiaService.registrarMembresia(cliente, tipo, inicio, fin), membresia -> {
            Mensajes.exito(lblMensaje, (esRenovacion ? "Membresía renovada: " : "Membresía registrada: ")
                    + membresia.getTipo().getNombre() + " del " + Formato.fecha(membresia.getFechaInicio())
                    + " al " + Formato.fecha(membresia.getFechaFin()) + ".");
            dpFin.setValue(null);
            if (cliente == clienteActual) {
                cargarPeriodos(cliente);
            }
        }, lblMensaje, btnRegistrar, btnRenovar);
    }

    private boolean confirmarFechas(Cliente cliente, TipoMembresia tipo, LocalDate inicio, LocalDate fin,
            boolean esRenovacion) {
        String periodos = membresiasCliente.isEmpty()
                ? "ninguno"
                : membresiasCliente.size() + " (último vencimiento: "
                        + Formato.fecha(Formato.ultimoVencimiento(membresiasCliente)) + ")";
        String texto = "Cliente: " + Formato.cliente(cliente)
                + "\nPeríodos registrados: " + periodos
                + "\nNuevo período: " + tipo.getNombre() + " del " + Formato.fecha(inicio)
                + " al " + Formato.fecha(fin);
        String accion = esRenovacion ? "Renovar" : "Registrar";
        return Mensajes.confirmar(btnRegistrar, accion + " membresía", texto, accion);
    }

    private void mostrarCliente(Cliente cliente) {
        clienteActual = cliente;
        lblClienteSeleccionado.setText(Formato.cliente(cliente).toUpperCase());
        Mensajes.limpiar(lblMensaje);
        cargarPeriodos(cliente);
    }

    private void cargarPeriodos(Cliente cliente) {
        membresiasCliente = new ArrayList<>();
        tblMembresias.getItems().clear();
        lblSinMembresias.setText("");
        lblResumen.setText("");
        chkHistorial.setSelected(false);
        mostrarCasillaHistorial(false);
        Tarea.ejecutar(() -> membresiaService.consultarVigencia(cliente.getDni()), membresias -> {
            if (cliente == clienteActual) {
                membresiasCliente = membresias;
                aplicarFiltro();
                lblResumen.setText(Formato.resumenMembresias(membresias));
                sugerirInicio(Formato.ultimoVencimiento(membresias));
            }
        }, lblMensajeCliente);
    }

    // Sugerencia: el dia siguiente al ultimo vencimiento, o hoy si no tiene periodos pendientes.
    private void sugerirInicio(LocalDate ultimoVencimiento) {
        LocalDate hoy = LocalDate.now();
        if (ultimoVencimiento == null || ultimoVencimiento.isBefore(hoy)) {
            dpInicio.setValue(hoy);
        } else {
            dpInicio.setValue(ultimoVencimiento.plusDays(1));
        }
    }

    @FXML
    private void cambiarHistorial() {
        aplicarFiltro();
    }

    // Por defecto solo se ven las vigentes y programadas; las vencidas, con "Mostrar historial".
    private void aplicarFiltro() {
        int vencidas = Formato.contarVencidas(membresiasCliente);
        chkHistorial.setText(Formato.textoHistorial(vencidas));
        mostrarCasillaHistorial(vencidas > 0);
        tblMembresias.getItems().setAll(Formato.filtrarMembresias(membresiasCliente, chkHistorial.isSelected()));
        if (membresiasCliente.isEmpty()) {
            lblSinMembresias.setText("El cliente no tiene membresías registradas.");
        } else {
            lblSinMembresias.setText("Sin membresías vigentes ni programadas. Marque «Mostrar historial» para ver las vencidas.");
        }
    }

    private void mostrarCasillaHistorial(boolean visible) {
        chkHistorial.setVisible(visible);
        chkHistorial.setManaged(visible);
    }

    private void quitarCliente() {
        clienteActual = null;
        membresiasCliente = new ArrayList<>();
        chkHistorial.setSelected(false);
        mostrarCasillaHistorial(false);
        lblResumen.setText("");
        lblClienteSeleccionado.setText("SIN CLIENTE");
        tblMembresias.getItems().clear();
        lblSinMembresias.setText("Seleccione un cliente para ver sus membresías.");
    }
}
