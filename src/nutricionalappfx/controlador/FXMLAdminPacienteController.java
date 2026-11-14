/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package nutricionalappfx.controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author chris
 */
public class FXMLAdminPacienteController implements Initializable {

    @FXML
    private TextField tfBusqueda;
    @FXML
    private TableView<?> tvPacientes;
    @FXML
    private TableColumn<?, ?> colCodigo;
    @FXML
    private TableColumn<?, ?> colNombre;
    @FXML
    private TableColumn<?, ?> colApellidoPaterno;
    @FXML
    private TableColumn<?, ?> colApellidoMaterno;
    @FXML
    private TableColumn<?, ?> colTelefono;
    @FXML
    private TableColumn<?, ?> colCorreo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
