/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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

/**
 * FXML Controller class
 *
 * @author chris
 */
public class FXMLAdminPacienteController implements Initializable {

    @FXML
    private TextField tfBusqueda;
    @FXML
    private TableView<Paciente> tvPacientes;
    @FXML
    private TableColumn<Paciente, String> colCodigo;
    @FXML
    private TableColumn<Paciente, String> colNombre;
    @FXML
    private TableColumn<Paciente, String> colApellidoPaterno;
    @FXML
    private TableColumn<Paciente, String> colApellidoMaterno;
    @FXML
    private TableColumn<Paciente, String> colTelefono;
    @FXML
    private TableColumn<Paciente, String> colCorreo;
    
    private ObservableList<Paciente> pacientes;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarPacientes("");
    }    
    
    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoPaciente"));
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

    @FXML
    private void hableBtnRegresar(ActionEvent event) {
    }

    @FXML
    private void hableBtnBuscar(ActionEvent event) {
    }

    @FXML
    private void hableBtnAlta(ActionEvent event) {
    }

    @FXML
    private void hableBtnEditar(ActionEvent event) {
    }

    @FXML
    private void hableBtnBaja(ActionEvent event) {
    }
    
}
