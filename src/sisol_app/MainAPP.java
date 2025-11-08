package sisol_app;

import Utilidad.Navegacion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainAPP extends Application {

    @Override
    public void start(Stage stage) {
        Navegacion.cargarEscenaConAnimacion(stage, "/Vista/Login.fxml");
    }
    
    

    public static void main(String[] args) {
        launch();
    }
}