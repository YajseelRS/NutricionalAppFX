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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nutricionalappfx.dominio.pojo.Colaborador;

/**
 * FXML Controller class
 *
 * @author chris
 */
public class FXMLPrincipalController implements Initializable {

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
        // TODO
    }    
    
    public void inicializarInformacion(Colaborador colaborador){
        this.colaborador = colaborador;
        mostrarInfoPantalla();
    }
    
    public void mostrarInfoPantalla(){
        if(colaborador != null){
            lbNombre.setText("Bienvenido "+colaborador.toString());
            lbRol.setText("Rol: "+colaborador.getRol());
        } 
    }

    @FXML
    private void hableBtnColaboradores(ActionEvent event) {
    }

    @FXML
    private void hableBtnPacientes(ActionEvent event) {
    }

    @FXML
    private void hableBtnCitas(ActionEvent event) {
    }

    @FXML
    private void hableBtnDietas(ActionEvent event) {
    }

    @FXML
    private void hableBtnConsultas(ActionEvent event) {
    }

    @FXML
    private void hableBtnCerrarSesion(ActionEvent event) {
    }
}
