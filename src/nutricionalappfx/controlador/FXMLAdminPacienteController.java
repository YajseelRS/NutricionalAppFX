package nutricionalappfx.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import nutricionalappfx.dominio.pojo.Paciente;
import nutricionalappfx.modelo.dao.PacienteDAO;
import nutricionalappfx.utilidades.Utilidades;

public class FXMLAdminPacienteController implements Initializable {
    
    private int idNutriologo;
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
        
        tvPacientes.setItems(value);
    }
    
    private void configurarColumnasTabla(){
        colCodigo.setCellValueFactory(new PropertyValueFactory("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApPaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colApMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory("fechaNacimiento"));
        colPeso.setCellValueFactory(new PropertyValueFactory("peso"));
    }

    public void recibirInformacion(int idNutriologo) {
        this.idNutriologo = idNutriologo;
    }

    @FXML
    private void hableBtnRegresar(ActionEvent event) {
        ((Stage) tfBusqueda.getScene().getWindow()).close();
    }

    private void hableBtnBuscar(ActionEvent event) {
        cargarPacientes(tfBusqueda.getText().trim());
    }

    private void hableBtnAlta(ActionEvent event) {
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

    private void hableBtnBaja(ActionEvent event) {
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
                PacienteDAO.darDeBajaPaciente(seleccionado.getIdPaciente());
                Utilidades.mostrarAlertaSimple("Baja realizada",
                        "El paciente fue dado de baja.", Alert.AlertType.INFORMATION);
                cargarPacientes(tfBusqueda.getText().trim());
            } catch (RuntimeException e) {
                Utilidades.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
    }

    private void cargarPacientes(String texto) {
        try {
            List<Paciente> lista = PacienteDAO.buscarPacientes(texto);
            pacientes = FXCollections.observableArrayList(lista);
            tvPacientes.setItems(pacientes);
            if (lista.isEmpty() && !texto.isEmpty()) {
                Utilidades.mostrarAlertaSimple("Sin resultados",
                        "No se encontraron pacientes con \"" + texto + "\".", Alert.AlertType.INFORMATION);
            }
        } catch (RuntimeException e) {
            Utilidades.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.ERROR);
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

            cargarPacientes(tfBusqueda.getText().trim());
        } catch (IOException e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error",
                    "No se pudo abrir el formulario de paciente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void hableBtnRegistrar(ActionEvent event) {
    }

    @FXML
    private void hableBtnEliminar(ActionEvent event) {
    }
}