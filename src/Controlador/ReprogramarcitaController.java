package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import Modelo.Cita;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReprogramarcitaController {

    @FXML
    private HBox fechasreprogramar;
    @FXML
    private FlowPane horasreprogramar;
    @FXML
    private Pane lbFoto;
    @FXML
    private Button btnReprogramar;
    @FXML
    private Button btnCancelar;

    private ActualizarCallback callback;
    
    // Datos de la cita original
    private Cita citaOriginal;
    private int idPacienteActual;

    // Fecha y hora seleccionadas
    private LocalDate fechaSeleccionada;
    private LocalTime horaSeleccionada;
    private int idHorarioSeleccionado;

    // Inicializa con los datos de la cita a reprogramar
    public void inicializar(Cita cita, int idPaciente, ActualizarCallback callback) {
        this.citaOriginal = cita;
        this.idPacienteActual = idPaciente;
        this.callback = callback;
        cargarFechasDisponibles();
    }

    private void cargarFechasDisponibles() {
        fechasreprogramar.getChildren().clear();
        LocalDate hoy = LocalDate.now();

        // Incluir la fecha original como disponible (porque se va a cancelar)
        String query = 
            "SELECT DISTINCT fecha FROM horarios_disponibles " +
            "WHERE id_medico = ? AND fecha >= ? AND estado = 'disponible' " +
            "UNION " +
            "SELECT ? AS fecha " + // Fecha original
            "ORDER BY fecha LIMIT 7";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, citaOriginal.getIdMedico());
            stmt.setDate(2, java.sql.Date.valueOf(hoy));
            stmt.setDate(3, java.sql.Date.valueOf(citaOriginal.getFecha()));

            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

            while (rs.next()) {
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                Button btnFecha = crearBotonFecha(fecha, formatter);
                fechasreprogramar.getChildren().add(btnFecha);
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
            // Reset estilo
            fechasreprogramar.getChildren().forEach(n -> {
                if (n instanceof Button) {
                    n.setStyle("-fx-background-color: #e0f0ff; -fx-border-color: #a0d0ff; -fx-border-radius: 8; -fx-background-radius: 8;");
                }
            });
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
        horasreprogramar.getChildren().clear();
        String query = 
            "SELECT id_horario, hora FROM horarios_disponibles " +
            "WHERE id_medico = ? AND fecha = ? AND estado = 'disponible' ";

        // Si es la fecha original, incluir el horario original
        if (fecha.equals(citaOriginal.getFecha())) {
            query += "UNION SELECT NULL AS id_horario, ? AS hora ";
        }
        query += "ORDER BY hora";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, citaOriginal.getIdMedico());
            stmt.setDate(2, java.sql.Date.valueOf(fecha));

            if (fecha.equals(citaOriginal.getFecha())) {
                stmt.setTime(3, java.sql.Time.valueOf(citaOriginal.getHora()));
            }

            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            while (rs.next()) {
                // Para el horario original, no hay id_horario (es NULL)
                int idHorario = rs.getInt("id_horario");
                LocalTime hora = rs.getTime("hora").toLocalTime();
                Button btnHora = crearBotonHora(hora, horaFormatter, idHorario);
                horasreprogramar.getChildren().add(btnHora);
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
            idHorarioSeleccionado = idHorario; // Será 0 si es el horario original
            // Reset estilo
            horasreprogramar.getChildren().forEach(n -> {
                if (n instanceof Button) {
                    n.setStyle("-fx-background-color: #f0f9ff; -fx-border-color: #23AAFA; -fx-border-radius: 20; -fx-text-fill: #23AAFA;");
                }
            });
            btn.setStyle(
                "-fx-background-color: #23AAFA; " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 20;"
            );
        });
        return btn;
    }

    @FXML
    private void reprogramarCita() {
        if (fechaSeleccionada == null || horaSeleccionada == null) {
            mostrarAlerta("Advertencia", "Selecciona una fecha y hora.");
            return;
        }

        // Si es el mismo horario original, no hacer nada
        if (fechaSeleccionada.equals(citaOriginal.getFecha()) && 
            horaSeleccionada.equals(citaOriginal.getHora())) {
            mostrarAlerta("Advertencia", "Seleccionaste la misma fecha y hora.");
            return;
        }

        boolean exito = reprogramarCitaEnBD();
        if (exito) {
            mostrarAlerta("Éxito", "Cita reprogramada exitosamente.");
            cerrarVentana();
            if (callback != null) {
                callback.onActualizacion();
            }            
        } else {
            mostrarAlerta("Error", "No se pudo reprogramar la cita.");
        }
    }

    private boolean reprogramarCitaEnBD() {
        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            // 1. Cancelar cita original
            String updateCita = "UPDATE citas SET estado = 'cancelada' WHERE id_cita = ?";
            try (PreparedStatement stmt1 = conn.prepareStatement(updateCita)) {
                stmt1.setInt(1, citaOriginal.getIdCita());
                stmt1.executeUpdate();
            }

            // 2. Liberar horario original
            String liberarHorario = 
                "UPDATE horarios_disponibles SET estado = 'disponible' " +
                "WHERE id_medico = ? AND fecha = ? AND hora = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(liberarHorario)) {
                stmt2.setInt(1, citaOriginal.getIdMedico());
                stmt2.setDate(2, java.sql.Date.valueOf(citaOriginal.getFecha()));
                stmt2.setTime(3, java.sql.Time.valueOf(citaOriginal.getHora()));
                stmt2.executeUpdate();
            }

            // 3. Si es un horario nuevo (no el original), reservarlo
            if (idHorarioSeleccionado != 0) {
                String reservarHorario = 
                    "UPDATE horarios_disponibles SET estado = 'reservado' WHERE id_horario = ?";
                try (PreparedStatement stmt3 = conn.prepareStatement(reservarHorario)) {
                    stmt3.setInt(1, idHorarioSeleccionado);
                    stmt3.executeUpdate();
                }
            }

            // 4. Registrar nueva cita
            String insertCita = 
                "INSERT INTO citas (id_paciente, id_medico, id_horario, fecha, hora, estado) " +
                "VALUES (?, ?, ?, ?, ?, 'programada')";
            try (PreparedStatement stmt4 = conn.prepareStatement(insertCita)) {
                stmt4.setInt(1, idPacienteActual);
                stmt4.setInt(2, citaOriginal.getIdMedico());
                stmt4.setObject(3, (idHorarioSeleccionado == 0) ? null : idHorarioSeleccionado);
                stmt4.setDate(4, java.sql.Date.valueOf(fechaSeleccionada));
                stmt4.setTime(5, java.sql.Time.valueOf(horaSeleccionada));
                stmt4.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void Cancelarreprogramacion() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}