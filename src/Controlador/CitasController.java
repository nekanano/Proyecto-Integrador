package Controlador;

import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import Modelo.Cita;
import Modelo.Usuario;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CitasController {

    @FXML
    private Button btnVolver;
    @FXML
    private TextField txtbuscacita;   
    @FXML
    private VBox contenedorCitas;
    @FXML
    private Label numcitas;    

    private Usuario usuarioActual;
    private int idPacienteActual;
    private Stage stageActual; 

    public void inicializar(int idPaciente, Usuario usuario, Stage stage) {
        this.idPacienteActual = idPaciente;
        this.usuarioActual = usuario;
        this.stageActual = stage;
        cargarCitas();
        
        txtbuscacita.textProperty().addListener((obs, old, nuevo) -> {
            filtrarCitas(nuevo.trim());
        });
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

    private void cargarCitas() {
        contenedorCitas.getChildren().clear();
        String query = 
            "SELECT c.id_cita, c.id_medico, c.fecha, c.hora, c.estado, " +
            "       m.nombre AS nombre_medico, " +
            "       e.nombre AS especialidad, " +
            "       s.nombre AS sede " +
            "FROM citas c " +
            "JOIN medicos m ON c.id_medico = m.id_medico " +
            "JOIN especialidad e ON m.id_especialidad = e.idEspecialidad " +
            "JOIN sedes s ON m.id_sede = s.id_sede " +
            "WHERE c.id_paciente = ? " +
            "ORDER BY c.fecha DESC, c.hora DESC";

        List<Cita> citas = new ArrayList<>();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idPacienteActual);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                citas.add(new Cita(
                    rs.getInt("id_cita"),
                    rs.getInt("id_medico"),
                    rs.getString("nombre_medico"),
                    rs.getString("especialidad"),
                    rs.getString("sede"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getTime("hora").toLocalTime(),
                    rs.getString("estado")
                ));
            }

            mostrarCitas(citas);
            actualizarContador(citas.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filtrarCitas(String texto) {
        cargarCitas();
    }

    private void mostrarCitas(List<Cita> citas) {
        contenedorCitas.getChildren().clear();

        if (citas.isEmpty()) {
            Label lbl = new Label("No tienes citas programadas.");
            lbl.setFont(Font.font("Poppins Italic", 12));
            lbl.setTextFill(Color.web("#999999"));
            contenedorCitas.getChildren().add(lbl);
            return;
        }

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

        for (Cita cita : citas) {
            VBox tarjeta = crearTarjetaCita(cita, dateFormat, timeFormat);
            contenedorCitas.getChildren().add(tarjeta);
        }
    }

    private VBox crearTarjetaCita(Cita cita, DateTimeFormatter dateFormat, DateTimeFormatter timeFormat) {
        VBox tarjeta = new VBox(8);
        tarjeta.setPrefWidth(382);
        tarjeta.setPrefHeight(120);
        tarjeta.setStyle(
            "-fx-background-color: #f9f9f9; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 12;"
        );

        Label lblMedico = new Label(cita.getNombreMedico() + " • " + cita.getEspecialidad());
        lblMedico.setFont(Font.font("Poppins SemiBold", 13));
        lblMedico.setTextFill(Color.web("#23AAFA"));

        Label lblSede = new Label(cita.getSede());
        lblSede.setFont(Font.font("Poppins Regular", 11));
        lblSede.setTextFill(Color.web("#555555"));

        Label lblFechaHora = new Label(
            cita.getFecha().format(dateFormat) + " • " + 
            cita.getHora().format(timeFormat)
        );
        lblFechaHora.setFont(Font.font("Poppins Regular", 12));
        lblFechaHora.setTextFill(Color.web("#777777"));

        Label lblEstado = new Label("Estado: " + cita.getEstado());
        lblEstado.setFont(Font.font("Poppins Italic", 10));
        lblEstado.setTextFill(Color.web("#999999"));

        javafx.scene.control.Button btnDetalles = new javafx.scene.control.Button("Ver detalles");
        btnDetalles.setFont(Font.font("Poppins Regular", 11));
        btnDetalles.setStyle(
            "-fx-background-color: #23AAFA; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 12; " +
            "-fx-pref-height: 24;"
        );
        btnDetalles.setOnAction(e -> abrirDetallesCita(cita));

        tarjeta.getChildren().addAll(lblMedico, lblSede, lblFechaHora, lblEstado, btnDetalles);
        return tarjeta;
    }

    private void abrirDetallesCita(Cita cita) {
        Navegacion.cambiarAEscenaConControlador(
            stageActual,
            "/Vista/Detallecita.fxml",
            (DetallecitaController c) -> 
                c.inicializar(cita, idPacienteActual, usuarioActual, stageActual, this::cargarCitas)
        );
    }

    private void actualizarContador(int cantidad) {
        numcitas.setText(String.valueOf(cantidad));
    }
}