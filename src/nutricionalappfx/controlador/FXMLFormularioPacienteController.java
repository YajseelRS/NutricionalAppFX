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
import nutricionalappfx.dominio.PacienteDM;
import nutricionalappfx.dominio.pojo.OperacionResultado;
import nutricionalappfx.dominio.pojo.Paciente;
import nutricionalappfx.modelo.dao.PacienteDAO;
import nutricionalappfx.utilidades.Utilidades;

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
    private int idNutriologo;
    private final PacienteDM pacienteDM = new PacienteDM(new PacienteDAO());

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void hableBtnCancelar(ActionEvent event) {
        cerrarVentana();
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
        paciente.setSexo(rbFemenino.isSelected() ? 'F' : 'M');
        paciente.setAltura(Integer.parseInt(tfAltura.getText().trim()));
        paciente.setPesoInicial(Float.parseFloat(tfPesoInicial.getText().trim()));
        paciente.setCorreo(tfCorreo.getText().trim());
        paciente.setIdNutriologo(idNutriologo);

        try {
            OperacionResultado resultado;
            if (pacienteEdicion == null) {
                resultado = pacienteDM.registrarPaciente(paciente);
            } else {
                paciente.setIdPaciente(pacienteEdicion.getIdPaciente());
                resultado = pacienteDM.editarPaciente(paciente);
            }
            Utilidades.mostrarAlertaSimple(resultado.isError() ? "Error" : "Operación exitosa",
                    resultado.getMensajes(),
                    resultado.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
            if (!resultado.isError()) {
                cerrarVentana();
            }
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
            int altura = Integer.parseInt(tfAltura.getText().trim());
            if (altura < 40 || altura > 250) {
                return "La altura debe estar en centímetros (por ejemplo 165).";
            }
        } catch (NumberFormatException e) {
            return "La altura debe ser un número entero en centímetros (por ejemplo 165).";
        }
        try {
            float peso = Float.parseFloat(tfPesoInicial.getText().trim());
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

    public void inicializarFormulario(Paciente paciente, int idNutriologo) {
        this.pacienteEdicion = paciente;
        this.idNutriologo = idNutriologo;
        if (paciente != null) {
            tfCodigo.setText(paciente.getCodigo());
            tfNombre.setText(paciente.getNombre());
            tfApellidoPaterno.setText(paciente.getApellidoPaterno());
            tfApellidoMaterno.setText(paciente.getApellidoMaterno());
            dpFechaNacimiento.setValue(LocalDate.parse(paciente.getFechaNacimiento()));
            tfTelefono.setText(paciente.getTelefono());
            if (paciente.getSexo() == 'F') {
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