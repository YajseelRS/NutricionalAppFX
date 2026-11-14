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
import nutricionalappfx.dominio.pojo.Paciente;
import nutricionalappfx.modelo.dao.PacienteDAO;
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
    
    private Paciente pacienteEdicion;

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
        Paciente paciente = new Paciente();
        paciente.setNombre(tfNombre.getText().trim());
        paciente.setApellidoPaterno(tfApellidoPaterno.getText().trim());
        String materno = tfApellidoMaterno.getText().trim();
        paciente.setApellidoMaterno(materno.isEmpty() ? null : materno);
        paciente.setFechaNacimiento(dpFechaNacimiento.getValue().toString());
        paciente.setTelefono(tfTelefono.getText().trim());
        paciente.setSexo(rbFemenino.isSelected() ? "F" : "M");
        paciente.setAltura(Double.parseDouble(tfAltura.getText().trim()));
        paciente.setPesoInicial(Double.parseDouble(tfPesoInicial.getText().trim()));
        paciente.setCorreo(tfCorreo.getText().trim());

        try {
            if (pacienteEdicion == null) {
                PacienteDAO.insertarPaciente(paciente);
                Utilidades.mostrarAlertaSimple("Paciente registrado",
                        "El paciente fue registrado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                paciente.setIdPaciente(pacienteEdicion.getIdPaciente());
                PacienteDAO.editarPaciente(paciente);
                Utilidades.mostrarAlertaSimple("Paciente actualizado",
                        "Los datos del paciente fueron actualizados.", Alert.AlertType.INFORMATION);
            }
            cerrarVentana();
        } catch (RuntimeException e) {
            Utilidades.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.ERROR);
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
        if (!tfTelefono.getText().trim().matches("\\d{10}")) {
            return "El teléfono debe tener 10 dígitos.";
        }
        if (tgSexo.getSelectedToggle() == null) {
            return "Selecciona el sexo del paciente.";
        }
        try {
            double altura = Double.parseDouble(tfAltura.getText().trim());
            if (altura <= 0 || altura >= 3) {
                return "La altura debe estar en metros (por ejemplo 1.65).";
            }
        } catch (NumberFormatException e) {
            return "La altura debe ser un número (por ejemplo 1.65).";
        }
        try {
            double peso = Double.parseDouble(tfPesoInicial.getText().trim());
            if (peso <= 0 || peso >= 1000) {
                return "El peso debe ser mayor a 0 y menor a 1000 kg.";
            }
        } catch (NumberFormatException e) {
            return "El peso debe ser un número (por ejemplo 62.5).";
        }
        if (!tfCorreo.getText().trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            return "El correo electrónico no tiene un formato válido.";
        }
        return null;
    }
    
    private void cerrarVentana() {
        ((Stage) tfNombre.getScene().getWindow()).close();
    }
    
    public void inicializarFormulario(Paciente paciente) {
        this.pacienteEdicion = paciente;
        if (paciente != null) {
            tfCodigo.setText(paciente.getCodigoPaciente());
            tfNombre.setText(paciente.getNombre());
            tfApellidoPaterno.setText(paciente.getApellidoPaterno());
            tfApellidoMaterno.setText(paciente.getApellidoMaterno());
            dpFechaNacimiento.setValue(LocalDate.parse(paciente.getFechaNacimiento()));
            tfTelefono.setText(paciente.getTelefono());
            if ("F".equals(paciente.getSexo())) {
                rbFemenino.setSelected(true);
            } else {
                rbMasculino.setSelected(true);
            }
            tfAltura.setText(String.valueOf(paciente.getAltura()));
            tfPesoInicial.setText(String.valueOf(paciente.getPesoInicial()));
            tfCorreo.setText(paciente.getCorreo());
        }
    }
}
