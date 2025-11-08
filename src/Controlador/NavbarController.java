package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NavbarController {

    @FXML
    private Button btnHome;

    @FXML
    private Button btnAgendas;

    @FXML
    private Button btnpreguntas;

    @FXML
    private Button btnPerfil;

    private Runnable onHomeClick;
    private Runnable onAgendasClick;
    private Runnable onPreguntasClick;
    private Runnable onPerfilClick;

    @FXML
    private void initialize() {
        btnHome.setOnAction(e -> {
            if (onHomeClick != null) onHomeClick.run();
        });
        btnAgendas.setOnAction(e -> {
            if (onAgendasClick != null) onAgendasClick.run();
        });
        btnpreguntas.setOnAction(e -> {
            if (onPreguntasClick != null) onPreguntasClick.run();
        });
        btnPerfil.setOnAction(e -> {
            if (onPerfilClick != null) onPerfilClick.run();
        });
    }

    // Métodos para establecer los callbacks
    public void setOnHomeClick(Runnable callback) {
        this.onHomeClick = callback;
    }

    public void setOnAgendasClick(Runnable callback) {
        this.onAgendasClick = callback;
    }

    public void setOnPreguntasClick(Runnable callback) {
        this.onPreguntasClick = callback;
    }

    public void setOnPerfilClick(Runnable callback) {
        this.onPerfilClick = callback;
    }
}