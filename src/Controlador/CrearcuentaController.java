package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import Utilidad.Navegacion;
import javafx.scene.control.Alert.AlertType;

public class CrearcuentaController {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnRegistrar;
    @FXML private Label lblLogin;

    @FXML
    public void initialize() {
        lblLogin.setOnMouseClicked(e -> {
            Stage stage = (Stage) lblLogin.getScene().getWindow();
            Navegacion.cambiarAEscena(stage, "/Vista/Login.fxml");
        });
    }

    @FXML
    private void handleRegistro() {
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        if (!dni.matches("\\d{8}")) {
            mostrarAlerta("Error", "El DNI debe tener 8 dígitos numéricos.");
            return;
        }

        if (!correo.contains("@")) {
            mostrarAlerta("Error", "Formato de correo inválido.");
            return;
        }

        if (registrarUsuario(dni, nombre, apellido, correo, password)) {
            mostrarAlerta("Éxito", "Usuario registrado correctamente.");
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            Navegacion.cambiarAEscena(stage, "/Vista/Login.fxml");
        } else {
            mostrarAlerta("Error", "No se pudo registrar el usuario. El correo o DNI ya existen.");
        }
    }

    private boolean registrarUsuario(String dni, String nombre, String apellido, String correo, String password) {
        String query = "INSERT INTO usuario (nombre, apellido, dni, correo, contraseña, rol, id_sede) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var conn = Conexion.getConnection();
             var stmt = conn.prepareStatement(query)) {

            if (conn == null) return false;

            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, dni);
            stmt.setString(4, correo);
            stmt.setString(5, password);
            stmt.setString(6, "paciente");
            stmt.setInt(7, 1);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al registrar: " + e.getMessage());
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