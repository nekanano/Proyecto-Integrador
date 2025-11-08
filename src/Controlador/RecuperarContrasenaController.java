package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Utilidad.Navegacion;
import javafx.scene.control.Alert.AlertType;

public class RecuperarContrasenaController {

    @FXML private TextField txtCorreo;
    @FXML private Label lblVolverLogin;

    @FXML
    public void initialize() {
        lblVolverLogin.setOnMouseClicked(e -> {
            Stage stage = (Stage) lblVolverLogin.getScene().getWindow();
            Navegacion.cambiarAEscena(stage, "/Vista/Login.fxml");
        });
    }

    @FXML
    private void enviarEnlaceRecuperacion() {
        String correo = txtCorreo.getText().trim();

        if (correo.isEmpty() || !correo.contains("@")) {
            mostrarAlerta("Error", "Ingresa un correo válido.");
            return;
        }

        if (existeCorreo(correo)) {
            mostrarAlerta("Éxito", "Si el correo existe, recibirás un enlace para restablecer tu contraseña.");
        } else {
            mostrarAlerta("Éxito", "Si el correo existe, recibirás un enlace para restablecer tu contraseña.");
        }

        Stage stage = (Stage) txtCorreo.getScene().getWindow();
        Navegacion.cambiarAEscena(stage, "/Vistas/Login.fxml");
    }

    private boolean existeCorreo(String correo) {
        String query = "SELECT 1 FROM usuario WHERE correo = ?";
        try (var conn = Conexion.getConnection();
             var stmt = conn.prepareStatement(query)) {

            if (conn == null) return false;
            stmt.setString(1, correo);
            return stmt.executeQuery().next();
        } catch (Exception e) {
            System.err.println("Error al verificar correo: " + e.getMessage());
            return false;
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}