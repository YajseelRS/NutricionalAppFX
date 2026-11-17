package nutricionalappfx.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nutricionalappfx.NutricionalAppFX;
import nutricionalappfx.dominio.AutenticacionDM;
import nutricionalappfx.dominio.pojo.Autenticacion;
import nutricionalappfx.dominio.pojo.Colaborador;
import nutricionalappfx.modelo.dao.AutenticacionDAO;
import nutricionalappfx.utilidades.Sesion;
import nutricionalappfx.utilidades.Utilidades;

public class FXMLAutenticacionController implements Initializable {

    @FXML private TextField tfNoPersonal;
    @FXML private PasswordField pfContrasenia;
    @FXML private Label lbErrorNoPersonal;
    @FXML private Label lbErrorContrasenia;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbErrorNoPersonal.setText("");
        lbErrorContrasenia.setText("");
    }

    @FXML
    private void hableBtnIngresar(ActionEvent event) {
        if (sonCamposValidos()) {
            validarCredenciales(tfNoPersonal.getText(), pfContrasenia.getText());
        }
    }

    private void validarCredenciales(String noPersonal, String password) {
        AutenticacionDM autenticacionDM = new AutenticacionDM(new AutenticacionDAO());
        try {
            Autenticacion respuesta = autenticacionDM.verificarCredencialesColaborador(noPersonal, password);
            if (respuesta.isCredencialesCorrectas() && respuesta.isActivo()) {
                Sesion.iniciar(respuesta.getColaborador());
                irAlMenuPrincipal();
            } else {
                Utilidades.mostrarAlertaSimple("Lo sentimos :(", respuesta.getMensaje(), Alert.AlertType.WARNING);
            }
        } catch (RuntimeException e) {
            Utilidades.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean sonCamposValidos() {
        boolean esValido = true;
        lbErrorNoPersonal.setText("");
        lbErrorContrasenia.setText("");
        if (tfNoPersonal.getText().isEmpty()) {
            lbErrorNoPersonal.setText("El número de personal es obligatorio");
            esValido = false;
        }
        if (pfContrasenia.getText().isEmpty()) {
            lbErrorContrasenia.setText("La contraseña es obligatoria");
            esValido = false;
        }
        return esValido;
    }
    
    private void irAlMenuPrincipal() {
        try {
            Utilidades.cambiarEscena(tfNoPersonal, "/nutricionalappfx/vista/FXMLPrincipal.fxml", "Menú principal");
        } catch (IOException e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el menú principal.", Alert.AlertType.ERROR);
        }
    }
}
