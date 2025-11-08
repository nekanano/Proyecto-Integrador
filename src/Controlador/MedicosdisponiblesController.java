package Controlador;

import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import modelo.Medico;
import Modelo.Usuario;
import javafx.scene.control.Button;

public class MedicosdisponiblesController {

    @FXML
    private FlowPane contenedorDoctores;
    @FXML
    private Button btnVolver;

    private int idPacienteActual;
    private int idSedeActual;
    private int idEspecialidadActual;
    private Stage stageActual;
    private Usuario usuarioActual;

    public void inicializar(int idSede, int idEspecialidad, int idPaciente, Stage stage, Usuario usuario) {
        this.idSedeActual = idSede;
        this.idEspecialidadActual = idEspecialidad;
        this.idPacienteActual = idPaciente;
        this.stageActual = stage;
        this.usuarioActual = usuario;
        cargarMedicos();
    }

    private void cargarMedicos() {
        contenedorDoctores.getChildren().clear();
        String query = 
            "SELECT id_medico, nombre " +
            "FROM medicos " +
            "WHERE id_sede = ? AND id_especialidad = ? " +
            "ORDER BY nombre";

        List<Medico> medicos = new ArrayList<>();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (conn == null) return;

            stmt.setInt(1, idSedeActual);
            stmt.setInt(2, idEspecialidadActual);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                medicos.add(new Medico(
                    rs.getInt("id_medico"),
                    rs.getString("nombre"),
                    idEspecialidadActual,
                    idSedeActual
                ));
            }

            if (medicos.isEmpty()) {
                Label lbl = new Label("No hay médicos disponibles en esta especialidad.");
                lbl.setFont(Font.font("Poppins Italic", 12));
                lbl.setTextFill(Color.web("#999999"));
                contenedorDoctores.getChildren().add(lbl);
            } else {
                for (Medico medico : medicos) {
                    VBox tarjeta = crearTarjetaMedico(medico.getNombre());
                    
                    String nombreEspecialidad = obtenerNombreEspecialidad(medico.getIdEspecialidad());
                    String nombreSede = obtenerNombreSede(medico.getIdSede());
                    
                    tarjeta.setOnMouseClicked(e -> {
                        abrirDisponibilidadMedico(
                            medico.getId(),
                            medico.getNombre(),
                            nombreEspecialidad,
                            nombreSede,
                            idPacienteActual
                        );
                    });
                    
                    contenedorDoctores.getChildren().add(tarjeta);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label lbl = new Label("Error al cargar médicos.");
            lbl.setFont(Font.font("Poppins Italic", 12));
            lbl.setTextFill(Color.web("#FF0000"));
            contenedorDoctores.getChildren().add(lbl);
        }        
    }

    private String obtenerNombreEspecialidad(int idEspecialidad) {
        String query = "SELECT nombre FROM especialidad WHERE idEspecialidad = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idEspecialidad);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Especialidad desconocida";
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

    private String obtenerNombreSede(int idSede) {
        String query = "SELECT nombre FROM sedes WHERE id_sede = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idSede);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Sede desconocida";
    }

    private VBox crearTarjetaMedico(String nombre) {
        VBox tarjeta = new VBox();
        tarjeta.setPrefSize(190, 120);
        tarjeta.setStyle(
            "-fx-background-color: #f9f9f9; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-alignment: center;"
        );

        VBox espacioFoto = new VBox();
        espacioFoto.setPrefHeight(60);
        espacioFoto.setStyle("-fx-alignment: center;");

        Label lblNombre = new Label(nombre);
        lblNombre.setWrapText(true);
        lblNombre.setMaxWidth(190);
        lblNombre.setFont(Font.font("Poppins SemiBold", 12));
        lblNombre.setTextFill(Color.web("#23AAFA"));
        lblNombre.setAlignment(Pos.CENTER);

        tarjeta.getChildren().addAll(espacioFoto, lblNombre);       
        return tarjeta;     
    }
    
    private void abrirDisponibilidadMedico(int idMedico, String nombre, String especialidad, String sede, int idPaciente) {
        Navegacion.cambiarAEscenaConControlador(
            stageActual,
            "/Vista/Doctorseleccionado.fxml",
            (DoctorseleccionadoController controller) -> 
                controller.inicializar(idMedico, nombre, especialidad, sede, idPaciente, usuarioActual)
        );
    }    
}