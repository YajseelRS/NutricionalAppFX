package nutricionalappfx.utilidades;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Utilidades {

    public static String generarCifradoSHA256(String dato) throws RuntimeException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(dato.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hexadecimal = Integer.toHexString(0xff & b);
                if (hexadecimal.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hexadecimal);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error al cifrar la información.");
        }
    }

    public static void mostrarAlertaSimple(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
    
    public static <T> T cambiarEscena(Node nodoActual, String rutaFxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(Utilidades.class.getResource(rutaFxml));
        Parent vista = loader.load();
        Stage escenario = (Stage) nodoActual.getScene().getWindow();
        escenario.setScene(new Scene(vista));
        escenario.setTitle(titulo);
        escenario.sizeToScene();
        escenario.centerOnScreen();
        return loader.getController();
    }
}