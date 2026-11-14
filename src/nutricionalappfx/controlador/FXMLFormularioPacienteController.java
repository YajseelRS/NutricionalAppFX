/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package nutricionalappfx.controlador;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import nutricionalappfx.utilidades.Utilidades;

/**
 * FXML Controller class
 *
 * @author chris
 */
public class FXMLFormularioPacienteController implements Initializable {

    @FXML
    private TextField tfCodigo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidoPaterno;
    @FXML
    private TextField tfApellidoMaterno;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private TextField tfTelefono;
    @FXML
    private RadioButton rbFemenino;
    @FXML
    private ToggleGroup tgSexo;
    @FXML
    private RadioButton rbMasculino;
    @FXML
    private TextField tfAltura;
    @FXML
    private TextField tfPesoInicial;
    @FXML
    private TextField tfCorreo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void hableBtnCancelar(ActionEvent event) {
       ((Stage) tfNombre.getScene().getWindow()).close();
    }

    @FXML
    private void hableBtnGuardar(ActionEvent event) {
        String error = validarCampos();
        if (error != null) {
            Utilidades.mostrarAlertaSimple("Datos inválidos", error, Alert.AlertType.WARNING);
            return;
        }
    }
    
    private String validarCampos() {
        if (tfNombre.getText().trim().isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (tfApellidoPaterno.getText().trim().isEmpty()) {
            return "El apellido paterno es obligatorio.";
        }
        LocalDate fecha = dpFechaNacimiento.getValue();
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            return "Selecciona una fecha de nacimiento válida.";
        }
        if (!tfTelefono.getText().matches("\\d{10}")) {
            return "El teléfono debe tener 10 dígitos.";
        }
        if (tgSexo.getSelectedToggle() == null) {
            return "Selecciona el sexo del paciente.";
        }
        try {
            double altura = Double.parseDouble(tfAltura.getText());
            if (altura <= 0 || altura >= 3) {
                return "La altura debe estar en metros (por ejemplo 1.65).";
            }
        } catch (NumberFormatException e) {
            return "La altura debe ser un número (por ejemplo 1.65).";
        }
        try {
            if (Double.parseDouble(tfPesoInicial.getText()) <= 0) {
                return "El peso debe ser mayor a 0.";
            }
        } catch (NumberFormatException e) {
            return "El peso debe ser un número (por ejemplo 62.5).";
        }
        if (!tfCorreo.getText().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            return "El correo electrónico no tiene un formato válido.";
        }
        return null;
    }
}
