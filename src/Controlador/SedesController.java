package Controlador;

import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import Modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Sede;

public class SedesController {

    @FXML
    private TextField txtbuscasede;
    @FXML
    private VBox contenedorSedes;

    private List<Sede> todasLasSedes = new ArrayList<>();
    private Usuario usuario; // 👈

    public void inicializar(Usuario usuario, Stage stage) {
        this.usuario = usuario;
    }

    @FXML
    public void initialize() {
        cargarTodasLasSedes();
        filtrarYMostrarSedes("");
        txtbuscasede.textProperty().addListener((obs, old, nuevo) -> 
            filtrarYMostrarSedes(nuevo.trim())
        );
    }

    private void cargarTodasLasSedes() {
        String query = "SELECT id_sede, nombre, distrito, direccion, horario, tipo FROM sedes ORDER BY distrito, nombre";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) return;
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                todasLasSedes.add(new Sede(
                    rs.getInt("id_sede"),
                    rs.getString("nombre"),
                    rs.getString("distrito"),
                    rs.getString("direccion"),
                    rs.getString("horario"),
                    rs.getString("tipo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filtrarYMostrarSedes(String textoBusqueda) {
        contenedorSedes.getChildren().clear();
        String busqueda = textoBusqueda.toLowerCase();

        for (Sede sede : todasLasSedes) {
            if (sede.getNombre().toLowerCase().contains(busqueda) ||
                sede.getDistrito().toLowerCase().contains(busqueda)) {
                VBox tarjeta = crearTarjetaSede(sede);
                tarjeta.setOnMouseClicked(e -> seleccionarSede(sede));
                contenedorSedes.getChildren().add(tarjeta);
            }
        }

        if (contenedorSedes.getChildren().isEmpty() && !busqueda.isEmpty()) {
            Label lbl = new Label("No se encontraron sedes.");
            lbl.setFont(Font.font("Poppins Italic", 12));
            lbl.setTextFill(Color.web("#999999"));
            VBox mensaje = new VBox(lbl);
            mensaje.setPrefWidth(393);
            mensaje.setPrefHeight(60);
            contenedorSedes.getChildren().add(mensaje);
        }
    }

    private void seleccionarSede(Sede sede) {
        Stage stage = (Stage) contenedorSedes.getScene().getWindow();
        
        Navegacion.cambiarAEscenaConControlador(
            stage,
            "/Vista/Menuprincipal.fxml",
            (MenuprincipalController c) -> {
                c.inicializarDatos(usuario, stage);
                c.actualizarSede(sede.getNombre(), sede.getId());
            }
        );
    }

    private VBox crearTarjetaSede(Sede sede) {
        VBox tarjeta = new VBox(8);
        tarjeta.setPrefWidth(393);
        tarjeta.setPrefHeight(120);
        tarjeta.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 12;"
        );

        Label lblNombre = new Label(sede.getNombre());
        lblNombre.setFont(Font.font("Poppins SemiBold", 14));
        lblNombre.setTextFill(Color.web("#23AAFA"));

        Label lblDistrito = new Label(sede.getDistrito());
        lblDistrito.setFont(Font.font("Poppins Regular", 12));
        lblDistrito.setTextFill(Color.web("#555555"));

        Label lblDireccion = new Label(sede.getDireccion());
        lblDireccion.setWrapText(true);
        lblDireccion.setPrefWidth(370);
        lblDireccion.setFont(Font.font("Poppins Regular", 11));
        lblDireccion.setTextFill(Color.web("#777777"));

        Label lblHorario = new Label(sede.getHorario());
        lblHorario.setFont(Font.font("Poppins Italic", 10));
        lblHorario.setTextFill(Color.web("#999999"));

        tarjeta.getChildren().addAll(lblNombre, lblDistrito, lblDireccion, lblHorario);

        String estiloNormal = tarjeta.getStyle();
        String estiloHover = 
            "-fx-background-color: #f0f9ff; " +
            "-fx-border-color: #23AAFA; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 11;";

        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle(estiloHover));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle(estiloNormal));

        return tarjeta;
    }

    @FunctionalInterface
    public interface SedeSeleccionadaCallback {
        void onSedeSeleccionada(String nombre, int id);
    }
}