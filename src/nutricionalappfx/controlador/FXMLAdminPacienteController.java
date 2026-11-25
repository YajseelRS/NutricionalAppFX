package nutricionalappfx.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nutricionalappfx.dominio.PacienteDM;
import nutricionalappfx.dominio.pojo.OperacionResultado;
import nutricionalappfx.dominio.pojo.Paciente;
import nutricionalappfx.modelo.dao.PacienteDAO;
import nutricionalappfx.utilidades.Utilidades;

public class FXMLAdminPacienteController implements Initializable {

    private int idNutriologo;
    private ObservableList<Paciente> pacientes;
    private final PacienteDM pacienteDM = new PacienteDM(new PacienteDAO());

    @FXML
    private TextField tfBuscarPaciente;
    @FXML
    private TableView<Paciente> tvPacientes;
    @FXML
    private TableColumn colCodigo;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colApPaterno;
    @FXML
    private TableColumn colApMaterno;
    @FXML
    private TableColumn colFechaNacimiento;
    @FXML
    private TableColumn colPeso;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnasTabla();
        // La tabla se llena en recibirInformacion(), cuando ya se conoce el nutriólogo.
    }

    private void configurarColumnasTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApPaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colApMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory("fechaNacimiento"));
        colPeso.setCellValueFactory(new PropertyValueFactory("pesoInicial")); // antes decía "peso"
    }

    public void recibirInformacion(int idNutriologo) {
        this.idNutriologo = idNutriologo;
        cargarPacientes();
    }

    private void cargarPacientes() {
        try {
            List<Paciente> lista = pacienteDM.obtenerPacientesNutriologo(idNutriologo);
            pacientes = FXCollections.observableArrayList(lista);
            configurarBusqueda();
        } catch (RuntimeException e) {
            Utilidades.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Filtra por código o nombre completo mientras se escribe (el FXML solo tiene el TextField).
    private void configurarBusqueda() {
        FilteredList<Paciente> filtrados = new FilteredList<>(pacientes, p -> true);
        tfBuscarPaciente.textProperty().addListener((obs, anterior, nuevo) -> {
            String texto = nuevo == null ? "" : nuevo.trim().toLowerCase();
            filtrados.setPredicate(p -> {
                if (texto.isEmpty()) {
                    return true;
                }
                String materno = p.getApellidoMaterno() == null ? "" : p.getApellidoMaterno();
                String completo = (p.getNombre() + " " + p.getApellidoPaterno() + " " + materno).toLowerCase();
                return p.getCodigo().toLowerCase().contains(texto) || completo.contains(texto);
            });
        });
        SortedList<Paciente> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tvPacientes.comparatorProperty());
        tvPacientes.setItems(ordenados);
    }

    @FXML
    private void hableBtnRegresar(ActionEvent event) {
        ((Stage) tvPacientes.getScene().getWindow()).close();
    }

    @FXML
    private void hableBtnRegistrar(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML
    private void hableBtnEditar(ActionEvent event) {
        Paciente seleccionado = tvPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Selecciona un paciente",
                    "Elige un paciente de la tabla para editarlo.", Alert.AlertType.WARNING);
            return;
        }
        abrirFormulario(seleccionado);
    }

    @FXML
    private void hableBtnEliminar(ActionEvent event) {
        Paciente seleccionado = tvPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Selecciona un paciente",
                    "Elige un paciente de la tabla para darlo de baja.", Alert.AlertType.WARNING);
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar baja");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Dar de baja a " + seleccionado.getNombre() + " "
                + seleccionado.getApellidoPaterno() + " (" + seleccionado.getCodigo() + ")?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                OperacionResultado resultado = pacienteDM.eliminarPaciente(seleccionado.getIdPaciente());
                Utilidades.mostrarAlertaSimple(resultado.isError() ? "Error" : "Baja realizada",
                        resultado.getMensajes(),
                        resultado.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
                cargarPacientes();
            } catch (RuntimeException e) {
                Utilidades.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void abrirFormulario(Paciente pacienteEdicion) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/nutricionalappfx/vista/FXMLFormularioPaciente.fxml"));
            Parent vista = loader.load();
            FXMLFormularioPacienteController controlador = loader.getController();
            controlador.inicializarFormulario(pacienteEdicion, idNutriologo);

            Stage escenario = new Stage();
            escenario.setScene(new Scene(vista));
            escenario.setTitle(pacienteEdicion == null ? "Alta de paciente" : "Editar paciente");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

            cargarPacientes();
        } catch (IOException e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error",
                    "No se pudo abrir el formulario de paciente.", Alert.AlertType.ERROR);
        }
    }
}