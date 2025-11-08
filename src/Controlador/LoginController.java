package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

import Modelo.Usuario;
import Utilidad.Navegacion;
import javafx.scene.control.Label;

public class LoginController {

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnregistrar;
    
    @FXML
    private Label lblOlvidaste;
    
    @FXML
    private Label lblRegistrarse;    
    
    @FXML
    private void initialize() {
        lblOlvidaste.setOnMouseClicked(e -> {
            Stage stage = (Stage) lblOlvidaste.getScene().getWindow();
            Navegacion.cambiarAEscena(stage, "/Vista/RecuperarContrasena.fxml");
        });
        
        lblRegistrarse.setOnMouseClicked(e -> {
            Stage stage = (Stage) lblRegistrarse.getScene().getWindow();
            Navegacion.cambiarAEscena(stage, "/Vista/Crearcuenta.fxml");
        });
    }    

    @FXML
    private void handleLogin() {
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText();

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingresa correo y contraseña.");
            return;
        }

        Usuario usuario = autenticarUsuario(correo, password);
        if (usuario != null) {
            mostrarAlerta("Éxito", "Inicio de sesión exitoso.");
            abrirMenuPrincipal(usuario);
        } else {
            mostrarAlerta("Error", "Credenciales incorrectas.");
        }
    }

    private Usuario autenticarUsuario(String correo, String password) {
        String query = "SELECT idUsuario, nombre, apellido, correo, dni FROM usuario WHERE correo = ? AND contraseña = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (conn == null) return null;

            stmt.setString(1, correo);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("idUsuario"), rs.getString("nombre"), rs.getString("apellido"), rs.getString("correo"), rs.getInt("dni"));
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void abrirMenuPrincipal(Usuario usuario) {
        Stage loginStage = (Stage) btnregistrar.getScene().getWindow();
        Navegacion.cambiarAEscenaConControlador(
            loginStage,
            "/Vista/Menuprincipal.fxml",
            (MenuprincipalController c) -> c.inicializarDatos(usuario, loginStage)
        );
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}