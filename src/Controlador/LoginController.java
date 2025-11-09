package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.regex.Pattern;

// ✅ Google Guava (funcional)
import com.google.common.base.Strings;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

// Logging y Commons (ya los tienes)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import Modelo.Usuario;
import Utilidad.Navegacion;
import DAO.UsuarioDAO;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnregistrar;

    @FXML
    private void handleLogin() {
        String correo = txtCorreo.getText();
        String password = txtPassword.getText();

        try {
 
            if (Strings.isNullOrEmpty(correo) || Strings.isNullOrEmpty(password)) {
                mostrarAlerta("Error", "Correo y contraseña son obligatorios.");
                return;
            }

            if (!EMAIL_PATTERN.matcher(correo.trim()).matches()) {
                mostrarAlerta("Error", "El correo electrónico no es válido.");
                return;
            }

            Preconditions.checkArgument(password.length() >= 6, "La contraseña debe tener al menos 6 caracteres");

            Usuario usuario = autenticarUsuario(correo.trim(), password);
            if (usuario != null) {
                log.info("Inicio de sesión exitoso: {}", correo);
                abrirMenuPrincipal(usuario);
            } else {
                log.warn("Credenciales incorrectas: {}", correo);
                mostrarAlerta("Error", "Credenciales incorrectas.");
            }

        } catch (IllegalArgumentException e) {
            log.warn("Validación fallida: {}", e.getMessage());
            mostrarAlerta("Validación", e.getMessage());
        }
    }

    private Usuario autenticarUsuario(String correo, String password) {
        UsuarioDAO dao = new UsuarioDAO();
        try {
            String storedPassword = dao.obtenerHashPorCorreo(correo);
            if (storedPassword == null) {
                log.debug("Usuario no encontrado: {}", correo);
                return null;
            }

            Usuario usuario = dao.obtenerPorCorreo(correo);
            if (usuario == null) return null;

            if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2y$") || storedPassword.startsWith("$2b$")) {
                if (BCrypt.verifyer().verify(password.toCharArray(), storedPassword).verified) {
                    log.info("Autenticado con BCrypt: {}", correo);
                    return usuario;
                }
            } else if (storedPassword.equals(password)) {
                log.warn("Autenticado en TEXTO PLANO (migrar a BCrypt): {}", correo);
                return usuario;
            }

            log.debug("Contraseña incorrecta para: {}", correo);
            return null;

        } catch (SQLException e) {
            log.error("Error en DAO al autenticar", e);
            return null;
        }
    }

    private void abrirMenuPrincipal(Usuario usuario) {
        Stage stage = (Stage) btnregistrar.getScene().getWindow();
        Navegacion.cambiarAEscenaConControlador(
            stage,
            "/Vista/Menuprincipal.fxml",
            (MenuprincipalController c) -> c.inicializarDatos(usuario, stage)
        );
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
    }
}