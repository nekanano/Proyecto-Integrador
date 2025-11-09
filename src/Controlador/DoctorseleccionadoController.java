package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import Modelo.HorarioDisponible;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import Modelo.Usuario;
import Utilidad.Navegacion;
import javafx.stage.Stage;

public class DoctorseleccionadoController {

    @FXML
    private Button btnVolver;
    @FXML
    private Pane contenedorDatos;
    @FXML
    private HBox contenedorFechas;
    @FXML
    private FlowPane contenedorHoras;
    @FXML
    private Button btnRegistra;

    private int idPacienteActual;
    private int idMedico;
    private String nombreMedico;
    private String nombreEspecialidad;
    private String nombreSede;
    private Usuario usuarioActual;

    private LocalDate fechaSeleccionada;
    private LocalTime horaSeleccionada;
    private int idHorarioSeleccionado;

    public void inicializar(int idMedico, String nombreMedico, String especialidad, String sede, int idPaciente, Usuario usuario) {
        this.idMedico = idMedico;
        this.nombreMedico = nombreMedico;
        this.nombreEspecialidad = especialidad;
        this.nombreSede = sede;
        this.idPacienteActual = idPaciente;
        this.usuarioActual = usuario;

        mostrarDatosMedico();
        cargarFechasDisponibles();
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

    private void mostrarDatosMedico() {
        contenedorDatos.getChildren().clear();
        contenedorDatos.setStyle("-fx-background-color: #f0f9ff; -fx-padding: 10; -fx-border-radius: 8;");

        Label lblNombre = new Label(nombreMedico);
        lblNombre.setFont(Font.font("Poppins Bold", 14));
        lblNombre.setTextFill(Color.web("#23AAFA"));

        Label lblEspecialidad = new Label(nombreEspecialidad);
        lblEspecialidad.setFont(Font.font("Poppins Regular", 12));
        lblEspecialidad.setTextFill(Color.web("#555555"));

        Label lblSede = new Label(nombreSede);
        lblSede.setFont(Font.font("Poppins Italic", 11));
        lblSede.setTextFill(Color.web("#777777"));

        VBox vbox = new VBox(4);
        vbox.getChildren().addAll(lblNombre, lblEspecialidad, lblSede);
        contenedorDatos.getChildren().add(vbox);
    }

    private void cargarFechasDisponibles() {
        contenedorFechas.getChildren().clear();
        LocalDate hoy = LocalDate.now();

        String queryFechas = 
            "SELECT DISTINCT fecha FROM horarios_disponibles " +
            "WHERE id_medico = ? AND fecha >= ? AND estado = 'disponible' " +
            "ORDER BY fecha LIMIT 7";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryFechas)) {

            stmt.setInt(1, idMedico);
            stmt.setDate(2, java.sql.Date.valueOf(hoy));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                Button btnFecha = crearBotonFecha(fecha, formatter);
                contenedorFechas.getChildren().add(btnFecha);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Button crearBotonFecha(LocalDate fecha, DateTimeFormatter formatter) {
        Button btn = new Button(fecha.format(formatter));
        btn.setPrefSize(80, 50);
        btn.setStyle(
            "-fx-background-color: #e0f0ff; " +
            "-fx-border-color: #a0d0ff; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );
        btn.setOnAction(e -> {
            fechaSeleccionada = fecha;
            cargarHorasDisponibles(fecha);
            for (javafx.scene.Node n : contenedorFechas.getChildren()) {
                if (n instanceof Button) {
                    n.setStyle("-fx-background-color: #e0f0ff; -fx-border-color: #a0d0ff; -fx-border-radius: 8; -fx-background-radius: 8;");
                }
            }
            btn.setStyle(
                "-fx-background-color: #23AAFA; " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;"
            );
        });
        return btn;
    }

    private void cargarHorasDisponibles(LocalDate fecha) {
        contenedorHoras.getChildren().clear();
        String queryHoras = 
            "SELECT id_horario, hora FROM horarios_disponibles " +
            "WHERE id_medico = ? AND fecha = ? AND estado = 'disponible' " +
            "ORDER BY hora";

        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryHoras)) {

            stmt.setInt(1, idMedico);
            stmt.setDate(2, java.sql.Date.valueOf(fecha));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idHorario = rs.getInt("id_horario");
                LocalTime hora = rs.getTime("hora").toLocalTime();
                Button btnHora = crearBotonHora(hora, horaFormatter, idHorario);
                contenedorHoras.getChildren().add(btnHora);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Button crearBotonHora(LocalTime hora, DateTimeFormatter formatter, int idHorario) {
        Button btn = new Button(hora.format(formatter));
        btn.setPrefSize(100, 40);
        btn.setStyle(
            "-fx-background-color: #f0f9ff; " +
            "-fx-border-color: #23AAFA; " +
            "-fx-border-radius: 20; " +
            "-fx-text-fill: #23AAFA;"
        );
        btn.setOnAction(e -> {
            horaSeleccionada = hora;
            idHorarioSeleccionado = idHorario;
            for (javafx.scene.Node n : contenedorHoras.getChildren()) {
                if (n instanceof Button) {
                    n.setStyle("-fx-background-color: #f0f9ff; -fx-border-color: #23AAFA; -fx-border-radius: 20; -fx-text-fill: #23AAFA;");
                }
            }
            btn.setStyle(
                "-fx-background-color: #23AAFA; " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 20;"
            );
        });
        return btn;
    }

    @FXML
    private void registrarCita() {
        if (fechaSeleccionada == null || horaSeleccionada == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("Por favor selecciona una fecha y hora.");
            alert.showAndWait();
            return;
        }

        String insertCita = "INSERT INTO citas (id_paciente, id_medico, id_horario, fecha, hora) VALUES (?, ?, ?, ?, ?)";
        String update = "UPDATE horarios_disponibles SET estado = 'reservado' WHERE id_horario = ?";

        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(update)) {
                stmt1.setInt(1, idHorarioSeleccionado);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(insertCita)) {
                stmt2.setInt(1, idPacienteActual);
                stmt2.setInt(2, idMedico);
                stmt2.setInt(3, idHorarioSeleccionado);
                stmt2.setDate(4, java.sql.Date.valueOf(fechaSeleccionada));
                stmt2.setTime(5, java.sql.Time.valueOf(horaSeleccionada));
                stmt2.executeUpdate();
            }

            conn.commit();

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("¡Cita registrada con éxito!");
            alert.showAndWait();

            javafx.stage.Stage stage = (javafx.stage.Stage) btnRegistra.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = Conexion.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error al registrar la cita. Inténtalo de nuevo.");
            alert.showAndWait();
        }
    }
}