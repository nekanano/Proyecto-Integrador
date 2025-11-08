package Controlador;

import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import Modelo.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import javafx.stage.Stage;
import Modelo.Usuario;

public class DetallecitaController {

    @FXML
    private Label lbDoctor;
    @FXML
    private Label lbEspecialidad;
    @FXML
    private Label lbhora;
    @FXML
    private Label lbFecha;
    @FXML
    private Label lbdescripcioncita;
    @FXML
    private Button btnRepgrogramar; 
    @FXML
    private Button brnCancelar;    
    @FXML
    private Pane lbFoto;
    @FXML
    private Button btnVolver;    

    private Cita citaActual;
    private ActualizarCallback callback;
    private int idPacienteActual;
    private Stage stageActual; // 👈 referencia al Stage
    private Usuario usuarioActual;

    public void inicializar(Cita cita, int idPaciente, Usuario usuario, Stage stage, ActualizarCallback callback) {
        this.citaActual = cita;
        this.idPacienteActual = idPaciente;
        this.usuarioActual = usuario; // 👈 ya lo tienes
        this.stageActual = stage;
        this.callback = callback;
        cargarDatos();
    }

    private void cargarDatos() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy");

        lbDoctor.setText(citaActual.getNombreMedico());
        lbEspecialidad.setText(citaActual.getEspecialidad());
        lbhora.setText("Hora: " + citaActual.getHora().format(timeFormat));
        lbFecha.setText("Fecha: " + citaActual.getFecha().format(dateFormat));
        
        String descripcion = String.format(
            "Tienes una cita programada con el Dr./Dra. %s en la especialidad de %s.\n\n" +
            "Sede: %s\n" +
            "Estado: %s",
            citaActual.getNombreMedico(),
            citaActual.getEspecialidad(),
            citaActual.getSede(),
            citaActual.getEstado()
        );
        lbdescripcioncita.setText(descripcion);
    }
    
    @FXML
    private void volverAlMenu() {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        Navegacion.cambiarAEscenaConControlador(
            stage,
            "/Vista/Menuprincipal.fxml",
            (MenuprincipalController c) -> c.inicializarDatos(usuarioActual, stage)
        );
    }    

    private boolean cancelarCitaEnBD(int idCita, LocalDate fecha, LocalTime hora) {
        String updateCita = "UPDATE citas SET estado = 'cancelada' WHERE id_cita = ?";
        String updateHorario = 
            "UPDATE horarios_disponibles SET estado = 'disponible' " +
            "WHERE id_medico = ? AND fecha = ? AND hora = ?";

        try (Connection conn = Conexion.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateCita)) {
                stmt1.setInt(1, idCita);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(updateHorario)) {
                stmt2.setInt(1, citaActual.getIdMedico());
                stmt2.setDate(2, java.sql.Date.valueOf(fecha));
                stmt2.setTime(3, java.sql.Time.valueOf(hora));
                stmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = Conexion.getConnection()) {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }    

    @FXML
    private void AbrirReprogramar() {
        Navegacion.cambiarAEscenaConControlador(
            stageActual,
            "/Vista/Reprogramarcita.fxml",
            (ReprogramarcitaController controller) -> 
                controller.inicializar(citaActual, idPacienteActual, callback)
        );
    }

    @FXML
    private void Cancelarcita() {
        javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION,
            "¿Estás seguro de que deseas cancelar esta cita?",
            javafx.scene.control.ButtonType.YES,
            javafx.scene.control.ButtonType.NO
        );
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText(null);

        java.util.Optional<javafx.scene.control.ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.YES) {
            boolean exito = cancelarCitaEnBD(citaActual.getIdCita(), citaActual.getFecha(), citaActual.getHora());

            if (exito) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("La cita ha sido cancelada exitosamente.");
                alert.showAndWait();

                // ✅ Ejecutamos el callback para recargar Citas
                if (callback != null) {
                    callback.onActualizacion();
                }

                // ✅ Y navegamos de vuelta a Citas
                Navegacion.cambiarAEscenaConControlador(
                    stageActual,
                    "/Vista/Citas.fxml",
                    (CitasController c) -> 
                        c.inicializar(idPacienteActual, /*usuario*/ null, stageActual)
                );

            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo cancelar la cita. Inténtalo de nuevo.");
                alert.showAndWait();
            }
        }
    }
}