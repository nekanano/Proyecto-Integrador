package Utilidad;
import Controlador.CitasController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Controlador.MenuprincipalController;
import Modelo.Usuario;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class Navegacion {
    
    public static void cambiarAEscena(Stage stage, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Navegacion.class.getResource(fxmlPath));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }    
    
    public static void cargarEscenaConAnimacion(Stage stage, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Navegacion.class.getResource(fxmlPath));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("SISOL");
            stage.setResizable(false);

            root.setOpacity(0);
            root.setTranslateY(70);

            stage.show();

            FadeTransition fade = new FadeTransition(Duration.millis(400), root);
            fade.setToValue(1.0);

            TranslateTransition slide = new TranslateTransition(Duration.millis(400), root);
            slide.setToY(0);

            fade.play();
            slide.play();

        } catch (IOException e) {
            System.err.println("Error al cargar " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            cambiarAEscena(stage, fxmlPath);
        }
    }
    
    
    public static <T> void cambiarAEscenaConControlador(Stage stage, String fxmlPath, Consumer<T> inicializador) {
        try {
            FXMLLoader loader = new FXMLLoader(Navegacion.class.getResource(fxmlPath));
            Parent root = loader.load();

            @SuppressWarnings("unchecked")
            T controller = loader.getController();
            if (inicializador != null) {
                inicializador.accept(controller);
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }    
}