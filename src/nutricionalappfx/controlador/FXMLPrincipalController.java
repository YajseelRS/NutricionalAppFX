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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nutricionalappfx.dominio.pojo.Colaborador;
import nutricionalappfx.utilidades.Sesion;
import nutricionalappfx.utilidades.Utilidades;

public class FXMLPrincipalController implements Initializable {

    private static final int ROL_ADMINISTRADOR = 1;
    private static final int ROL_NUTRIOLOGO = 2;

    private Colaborador colaborador;
    @FXML
    private Label lbNombre;
    @FXML
    private Label lbRol;
    @FXML
    private Button btnColaboradores;
    @FXML
    private Button btnPacientes;
    @FXML
    private Button btnCitas;
    @FXML
    private Button btnDietas;
    @FXML
    private Button btnConsultas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colaborador = Sesion.getColaborador();
        if (colaborador != null) {
            lbNombre.setText("Bienvenido(a) " + colaborador.getNombre() + " " + colaborador.getApellidoPaterno());
            lbRol.setText("Rol: " + colaborador.getRol());
            aplicarPermisos();
        }
    }

    private void aplicarPermisos() {
        int rol = colaborador.getIdRol();
        mostrar(btnColaboradores, rol == ROL_ADMINISTRADOR);
        mostrar(btnPacientes, rol == ROL_NUTRIOLOGO);
        mostrar(btnCitas, rol == ROL_ADMINISTRADOR || rol == ROL_NUTRIOLOGO);
        mostrar(btnDietas, rol == ROL_ADMINISTRADOR || rol == ROL_NUTRIOLOGO);
        mostrar(btnConsultas, true); // todos los roles
    }

    private void mostrar(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    private void irA(String rutaFxml, String titulo) {
        try {
            Utilidades.cambiarEscena(lbNombre, rutaFxml, titulo);
        } catch (IOException e) {
            e.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir la pantalla.", Alert.AlertType.ERROR);
        }
    }

    private void proximamente(String modulo) {
        Utilidades.mostrarAlertaSimple(modulo, "Este módulo todavía no está disponible.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void hableBtnColaboradores(ActionEvent event) {
        proximamente("Administrador de colaboradores");
    }

    @FXML
    private void hableBtnPacientes(ActionEvent event) {
        try {
            Stage escenario = new Stage();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/nutricionalappfx/vista/FXMLAdminPaciente.fxml"));
            Parent vista = loader.load();
            FXMLAdminPacienteController controlador = loader.getController();
            controlador.recibirInformacion(colaborador.getIdColaborador());
            escenario.setScene(new Scene(vista));
            escenario.setTitle("Administrador de pacientes");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir la pantalla.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void hableBtnCitas(ActionEvent event) {
        proximamente("Administrador de citas");
    }

    @FXML
    private void hableBtnDietas(ActionEvent event) {
        proximamente("Administrador de dietas");
    }

    @FXML
    private void hableBtnConsultas(ActionEvent event) {
        proximamente("Consultas");
    }

    @FXML
    private void hableBtnCerrarSesion(ActionEvent event) {
        Sesion.cerrar();
        irA("/nutricionalappfx/vista/FXMLAutenticacion.fxml", "Iniciar sesión");
    }
}