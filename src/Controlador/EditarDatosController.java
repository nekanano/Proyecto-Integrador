package Controlador;

import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import Modelo.Usuario;
import java.sql.*;
import javafx.application.Platform;

public class EditarDatosController {

    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtPasswordActual;
    @FXML
    private Label lblMensaje;

    private Usuario usuarioActual;
    private Stage stageActual;

    public void inicializar(Usuario usuario, Stage stage) {
        this.usuarioActual = usuario;
        this.stageActual = stage;

        txtDni.setText(String.valueOf(usuario.getDNI()));
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtCorreo.setText(usuario.getCorreo());
    }

    @FXML
    private void guardarCambios() {
        String dniStr = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim();
        String passwordActual = txtPasswordActual.getText();

        // Validaciones
        if (dniStr.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || passwordActual.isEmpty()) {
            mostrarMensaje("Todos los campos son obligatorios.", "#FF0000");
            return;
        }

        if (!validarDNI(dniStr)) {
            mostrarMensaje("El DNI debe tener entre 8 y 9 caracteres numéricos.", "#FF0000");
            return;
        }

        if (!correo.contains("@") || !correo.contains(".")) {
            mostrarMensaje("El correo no es válido.", "#FF0000");
            return;
        }

        if (!verificarContrasena(passwordActual)) {
            mostrarMensaje("La contraseña actual es incorrecta.", "#FF0000");
            return;
        }

        int dni = Integer.parseInt(dniStr);
        if (actualizarUsuarioEnBD(nombre, apellido, dni, correo)) {
            mostrarMensaje("Datos actualizados correctamente.", "#23AAFA");
            usuarioActual = new Usuario(usuarioActual.getId(), nombre, apellido, correo, dni);
            Platform.runLater(() -> {
                stageActual.getScene().getRoot().setDisable(true);
            });
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    Platform.runLater(() -> {
                        stageActual.getScene().getRoot().setDisable(false); 
                        volverAlPerfil();
                    });
                } catch (InterruptedException e) {
                    Platform.runLater(this::volverAlPerfil);
                }
            }).start();
        } else {
            mostrarMensaje("No se pudieron guardar los cambios. Inténtalo de nuevo.", "#FF0000");
        }
    }

    private boolean validarDNI(String dni) {
        return dni.matches("\\d{8,9}");
    }

    private boolean verificarContrasena(String password) {
        String query = "SELECT COUNT(*) FROM usuario WHERE idUsuario = ? AND contraseña = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioActual.getId());
            stmt.setString(2, password); // ⚠️ En producción, usa hashing
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean actualizarUsuarioEnBD(String nombre, String apellido, int dni, String correo) {
        String query = "UPDATE usuario SET nombre = ?, apellido = ?, dni = ?, correo = ? WHERE idUsuario = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setInt(3, dni);
            stmt.setString(4, correo);
            stmt.setInt(5, usuarioActual.getId()); // idUsuario no cambia

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void mostrarMensaje(String mensaje, String color) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    private void volverAlPerfil() {
        Navegacion.cambiarAEscenaConControlador(
            stageActual,
            "/Vista/Perfil.fxml",
            (PerfilController c) -> {
                c.inicializar(usuarioActual, stageActual, "H.S. Ate", 2);
            }
        );
    }
}