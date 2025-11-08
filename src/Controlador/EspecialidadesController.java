package Controlador;

import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Especialidad;
import javafx.geometry.Pos;
import Modelo.Usuario;
import javafx.scene.control.Button;

public class EspecialidadesController {

    @FXML
    private TextField txtbuscaespecialidad;
    @FXML
    private FlowPane contenedorEspecialidades;
    @FXML
    private Button btnVolver;
    
    private int idPacienteActual;
    private int idSedeActual = -1;
    private Usuario usuarioActual;
    private Stage stageActual;
    private List<Especialidad> todasLasEspecialidades = new ArrayList<>();

    public void inicializarConSede(int idSede, int idPaciente, Usuario usuario, Stage stage) {
        this.idSedeActual = idSede;
        this.idPacienteActual = idPaciente;
        this.usuarioActual = usuario;
        this.stageActual = stage;
        cargarEspecialidadesDeSede();
        
        txtbuscaespecialidad.textProperty().addListener((obs, old, nuevo) -> {
            filtrarEspecialidades(nuevo.trim());
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

    private void cargarEspecialidadesDeSede() {
        todasLasEspecialidades.clear();
        String query = 
            "SELECT e.idEspecialidad, e.nombre " +
            "FROM especialidad e " +
            "JOIN sede_especialidad se ON e.idEspecialidad = se.id_especialidad " +
            "WHERE se.id_sede = ? " +
            "ORDER BY e.nombre";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (conn == null) return;

            stmt.setInt(1, idSedeActual);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                todasLasEspecialidades.add(new Especialidad(
                    rs.getInt("idEspecialidad"),
                    rs.getString("nombre")
                ));
            }

            mostrarEspecialidades(todasLasEspecialidades);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filtrarEspecialidades(String texto) {
        List<Especialidad> filtradas = new ArrayList<>();
        String busqueda = texto.toLowerCase();

        for (Especialidad esp : todasLasEspecialidades) {
            if (esp.getNombre().toLowerCase().contains(busqueda)) {
                filtradas.add(esp);
            }
        }

        mostrarEspecialidades(filtradas);
    }

    private void mostrarEspecialidades(List<Especialidad> lista) {
        contenedorEspecialidades.getChildren().clear();

        if (lista.isEmpty()) {
            Label lbl = new Label("No hay especialidades disponibles.");
            lbl.setFont(Font.font("Poppins Italic", 12));
            lbl.setTextFill(Color.web("#999999"));
            contenedorEspecialidades.getChildren().add(lbl);
            return;
        }

        for (Especialidad esp : lista) {
            VBox tarjeta = crearTarjetaEspecialidad(esp.getNombre());
            tarjeta.setOnMouseClicked(e -> {
                abrirMedicosDisponibles(idSedeActual, esp.getId());
            });
            contenedorEspecialidades.getChildren().add(tarjeta);
        }
    }

    private VBox crearTarjetaEspecialidad(String nombre) {
        VBox tarjeta = new VBox();
        tarjeta.setPrefSize(190, 120); 
        tarjeta.setStyle(
            "-fx-background-color: #f9f9f9; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-alignment: center;"
        );

        VBox espacioImagen = new VBox();
        espacioImagen.setPrefHeight(60);
        espacioImagen.setStyle("-fx-alignment: center;");

        Label lblNombre = new Label(nombre);
        lblNombre.setWrapText(true);
        lblNombre.setMaxWidth(190);
        lblNombre.setFont(Font.font("Poppins SemiBold", 12));
        lblNombre.setTextFill(Color.web("#23AAFA"));
        lblNombre.setAlignment(Pos.CENTER);

        tarjeta.getChildren().addAll(espacioImagen, lblNombre);
        return tarjeta; 
    }
    
    private void abrirMedicosDisponibles(int idSede, int idEspecialidad) {
        Navegacion.cambiarAEscenaConControlador(
            stageActual,
            "/Vista/Medicosdisponibles.fxml",
            (MedicosdisponiblesController controller) -> 
                controller.inicializar(idSede, idEspecialidad, idPacienteActual, stageActual, usuarioActual)
        );
    }
}