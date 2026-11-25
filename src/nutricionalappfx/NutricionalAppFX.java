package nutricionalappfx;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class NutricionalAppFX extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource("vista/FXMLAutenticacion.fxml"));
            Scene escena = new Scene(vista);
            primaryStage.setScene(escena);
            primaryStage.setTitle("Iniciar Sesion");
            primaryStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
