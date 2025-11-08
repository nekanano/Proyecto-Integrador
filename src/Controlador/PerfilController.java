package Controlador;

import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import Modelo.Usuario;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PerfilController {

    @FXML
    private Button btnVolver;

    @FXML
    private Label lblNombre;
    @FXML
    private Label lblCorreo;
    @FXML
    private ImageView imgPerfil;

    @FXML
    private Label lblSede;
    @FXML
    private Label lblUltimaCita;
    @FXML
    private Label lblCitasPendientes;

    @FXML
    private Button btnCambiarContrasena;
    @FXML
    private Button btnDatos;
    @FXML
    private Button btnNotificaciones;
    @FXML
    private Button btnCerrarSesion;

    private Usuario usuarioActual;
    private Stage stageActual;
    private int idSedeActual = 2; 
    private String nombreSedeActual = "H.S. Ate";
 
    public void inicializar(Usuario usuario, Stage stage, String nombreSede, int idSede) {
        this.usuarioActual = usuario;
        this.stageActual = stage;
        this.idSedeActual = idSede;
        this.nombreSedeActual = nombreSede;
        cargarDatos();
    }

    private void cargarDatos() {
        lblNombre.setText(usuarioActual.getNombre());
        lblCorreo.setText(usuarioActual.getCorreo());

        imgPerfil.setImage(new Image(getClass().getResourceAsStream("")));

        lblSede.setText(nombreSedeActual);
        cargarUltimaCita();
        cargarCitasPendientes();
    }

    private void cargarUltimaCita() {
        String query = 
            "SELECT c.fecha, e.nombre AS especialidad " +
            "FROM citas c " +
            "JOIN medicos m ON c.id_medico = m.id_medico " +
            "JOIN especialidad e ON m.id_especialidad = e.idEspecialidad " +
            "WHERE c.id_paciente = ? " +
            "ORDER BY c.fecha DESC " +
            "LIMIT 1";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioActual.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                String especialidad = rs.getString("especialidad");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                lblUltimaCita.setText(fecha.format(formatter) + " • " + especialidad);
            } else {
                lblUltimaCita.setText("No hay citas registradas");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblUltimaCita.setText("Error al cargar");
        }
    }

    private void cargarCitasPendientes() {
        String query = "SELECT COUNT(*) AS total FROM citas WHERE id_paciente = ? AND estado = 'pendiente'";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioActual.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                lblCitasPendientes.setText(String.valueOf(total));
            } else {
                lblCitasPendientes.setText("0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblCitasPendientes.setText("0");
        }
    }

    @FXML
    private void volverAlMenu() {
        Navegacion.cambiarAEscenaConControlador(
            stageActual,
            "/Vista/Menuprincipal.fxml",
            (MenuprincipalController c) -> c.inicializarDatos(usuarioActual, stageActual)
        );
    }

    @FXML
    private void cambiarContrasena() {
        Navegacion.cambiarAEscena(stageActual, "/Vista/RecuperarContrasena.fxml");
    }

    @FXML
    private void editarDatos() {
        Navegacion.cambiarAEscenaConControlador(
            stageActual,
            "/Vista/EditarDatos.fxml",
            (EditarDatosController c) -> c.inicializar(usuarioActual, stageActual)
        );
    }

    @FXML
    private void gestionarNotificaciones() {
        // Por ahora, abre una vista simple
        //Navegacion.cambiarAEscena(stageActual, "/Vista/Notificaciones.fxml");
    }

    @FXML
    private void cerrarSesion() {
        javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION,
            "¿Estás seguro de que deseas cerrar sesión?",
            javafx.scene.control.ButtonType.YES,
            javafx.scene.control.ButtonType.NO
        );
        confirmacion.setTitle("Cerrar sesión");
        confirmacion.setHeaderText(null);

        java.util.Optional<javafx.scene.control.ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.YES) {
            // Volver al login
            Navegacion.cambiarAEscena(stageActual, "/Vista/Login.fxml");
        }
    }
}