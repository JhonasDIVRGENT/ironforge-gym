package com.tpoo.upn.gui;

import com.tpoo.upn.controller.ClienteController;
import com.tpoo.upn.controller.MembresiaController;
import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
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

/**
 * Eventos de MembresiasView.fxml (RF-04, RF-05, RF-12, RF-15).
 * Las fechas las elige el recepcionista; las valida el modelo y el servicio
 * (fin no anterior a inicio, cliente y tipo existentes, membresia previa al renovar).
 */
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

    private ClienteController clienteController;
    private MembresiaController membresiaController;
    private Cliente clienteActual;
    /** Todos los periodos del cliente; la tabla muestra solo los que pasan el filtro. */
    private List<Membresia> membresiasCliente = new ArrayList<>();

    public void inicializar(ClienteController clienteController, MembresiaController membresiaController) {
        this.clienteController = clienteController;
        this.membresiaController = membresiaController;

        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().getNombre()));
        colInicio.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaInicio())));
        colFin.setCellValueFactory(d -> new SimpleStringProperty(Formato.fecha(d.getValue().getFechaFin())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().obtenerEstado(LocalDate.now())));
        colEstado.setCellFactory(Formato.celdaEstado());
        cmbClientes.setConverter(Formato.convertidorCliente());
        cmbTipos.setConverter(Formato.convertidorTipo());
        dpInicio.setConverter(Formato.convertidorFecha());
        dpFin.setConverter(Formato.convertidorFecha());

        Tarea.ejecutar(clienteController::listarClientes,
                clientes -> cmbClientes.getItems().setAll(clientes), lblMensajeCliente);
        Tarea.ejecutar(membresiaController::listarTipos, tipos -> {
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
        Tarea.ejecutar(() -> clienteController.buscarCliente(dni), cliente -> {
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

        // Con los datos completos, se muestra lo que se va a guardar y se pide confirmacion.
        // Si falta algo, el servicio o el modelo lo rechazan con su motivo.
        if (tipo != null && inicio != null && fin != null && !confirmarFechas(cliente, tipo, inicio, fin, esRenovacion)) {
            return;
        }

        Tarea.ejecutar(() -> esRenovacion
                ? membresiaController.renovarMembresia(cliente, tipo, inicio, fin)
                : membresiaController.registrarMembresia(cliente, tipo, inicio, fin), membresia -> {
            Mensajes.exito(lblMensaje, (esRenovacion ? "Membresía renovada: " : "Membresía registrada: ")
                    + membresia.getTipo().getNombre() + " del " + Formato.fecha(membresia.getFechaInicio())
                    + " al " + Formato.fecha(membresia.getFechaFin()) + ".");
            dpFin.setValue(null);
            if (cliente == clienteActual) {
                cargarPeriodos(cliente);
            }
        }, lblMensaje, btnRegistrar, btnRenovar);
    }

    /** Muestra cliente, periodos existentes y fechas propuestas antes de guardar. */
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
        Tarea.ejecutar(() -> membresiaController.consultarVigencia(cliente.getDni()), membresias -> {
            if (cliente != clienteActual) {
                return; // Mientras tanto se eligio otro cliente.
            }
            membresiasCliente = membresias;
            aplicarFiltro();
            lblResumen.setText(Formato.resumenMembresias(membresias));
            sugerirInicio(Formato.ultimoVencimiento(membresias));
        }, lblMensajeCliente);
    }

    /**
     * Solo una sugerencia que el recepcionista puede cambiar: el dia siguiente al
     * ultimo vencimiento, o hoy si no tiene periodos pendientes. La regla de no
     * cruzar periodos la comprueba MembresiaService.
     */
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

    /**
     * Por defecto se ven solo los periodos vigentes y programados, para no
     * confundir; las vencidas se ven al marcar "Mostrar historial".
     */
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
