package Controlador;

import Modelo.Usuario;
import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AsistenteController {

    @FXML
    private VBox contenedorMensajes;
    
    @FXML
    private Button btnVolver;
    
    @FXML
    private TextField txtMensaje;

    private Usuario usuarioActual;
    private Stage stageActual;

    public void inicializar(Usuario usuario, Stage stage) {
        this.usuarioActual = usuario;
        this.stageActual = stage;
    }

    @FXML
    private void initialize() {
        mostrarMensajeAsistente("¡Hola! 👋 Soy SISOCIO, tu asistente de SISOL.\n\nPuedo ayudarte con:\n• Programar citas\n• Cambiar de sede\n• Recuperar tu cuenta\n• Ver tus citas\n\n¿En qué necesitas ayuda?");
    }

    @FXML
    private void enviarMensaje() {
        String pregunta = txtMensaje.getText().trim();
        if (pregunta.isEmpty()) return;

        mostrarMensajeUsuario(pregunta);
        txtMensaje.clear();

        String respuesta = procesarPregunta(pregunta);
        mostrarMensajeAsistente(respuesta);
    }

    private String procesarPregunta(String pregunta) {
        String p = pregunta.toLowerCase();
        if (p.contains("hola") || p.contains("buenos")) {
            return "¡Hola! ¿En qué puedo ayudarte?";
        }
        if (p.contains("cita") || p.contains("agendar") || p.contains("programar") || p.contains("reservar")) {
            return "Para programar una cita:\n1. Ve al Menú Principal\n2. Toca 'Registrar Cita'\n3. Elige una especialidad y un médico\n4. Selecciona una fecha y hora disponible";
        }
        if (p.contains("sede") || p.contains("cambiar") || p.contains("hospital")) {
            return "Puedes cambiar de sede desde el Menú Principal. Toca el nombre de tu sede actual (ej. 'H.S. Ate') y elige otra de la lista.";
        }
        if (p.contains("contraseña") || p.contains("olvidé") || p.contains("recuperar")) {
            return "En la pantalla de inicio de sesión, toca '¿Olvidaste tu contraseña?' y sigue las instrucciones.";
        }
        if (p.contains("perfil") || p.contains("datos") || p.contains("editar")) {
            return "Tus datos personales los puedes editar en 'Mi Perfil' > 'Editar datos'.";
        }
        return "Lo siento, no entendí tu pregunta. Puedes preguntarme sobre:\n• Cómo agendar citas\n• Cambiar de sede\n• Recuperar contraseña";
    }

    private void mostrarMensajeUsuario(String mensaje) {
        Label label = new Label(mensaje);
        label.setWrapText(true);
        label.setMaxWidth(300);
        label.setFont(Font.font("Poppins Regular", 14));
        label.setTextFill(Color.WHITE);
        label.setBackground(new Background(new BackgroundFill(Color.web("#23AAFA"), new CornerRadii(12), null)));
        label.setPadding(new Insets(12));
        VBox contenedor = new VBox(label);
        contenedor.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        contenedorMensajes.getChildren().add(contenedor);
    }

    private void mostrarMensajeAsistente(String mensaje) {
        Label label = new Label(mensaje);
        label.setWrapText(true);
        label.setMaxWidth(300);
        label.setFont(Font.font("Poppins Regular", 14));
        label.setTextFill(Color.web("#2C3E50"));
        label.setBackground(new Background(new BackgroundFill(Color.web("#F0F9FF"), new CornerRadii(12), null)));
        label.setPadding(new Insets(12));
        VBox contenedor = new VBox(label);
        contenedor.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        contenedorMensajes.getChildren().add(contenedor);
    }

    @FXML
    private void volverAlMenu() {
        Navegacion.cambiarAEscenaConControlador(
            stageActual, 
            "/Vista/Menuprincipal.fxml",
            (MenuprincipalController c) -> c.inicializarDatos(usuarioActual, stageActual)
        );
    }
}