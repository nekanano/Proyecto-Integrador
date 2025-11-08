package Controlador;

import Utilidad.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import Controlador.EspecialidadesController;
import Controlador.CitasController;
import Controlador.SedesController;
import Modelo.Usuario;

public class MenuprincipalController {

    @FXML
    private Label txtnombre;
    
    @FXML
    private Label txtsede;   
    
    @FXML
    private Button btnCambiarSede;
    
    @FXML
    private Button btnregistrarcita;
    
    @FXML
    private NavbarController navbarController;
    
    private Usuario usuarioActual;    
    private int idPacienteActual;
    private int idSedeActual = 2;
    private String nombreSedeActual = "H.S. Ate";
    private Stage stagePrincipal;

    public void inicializarDatos(Usuario usuario, Stage stage) {
        this.usuarioActual = usuario;
        this.stagePrincipal = stage;
        this.idPacienteActual = usuario.getId();
        txtnombre.setText(usuario.getNombre());
        txtsede.setText(nombreSedeActual); 
    }
    
    public void inicializarDatos(Usuario usuario, Stage stage, String nombreSede, int idSede) {
        inicializarDatos(usuario, stage);
        actualizarSede(nombreSede, idSede);
    }    
   
    public void actualizarSede(String nombreSede, int idSede) {
        this.nombreSedeActual = nombreSede;
        this.idSedeActual = idSede;
        txtsede.setText(nombreSede);
    }
    
    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setOnHomeClick(this::navegarAHome);
            navbarController.setOnAgendasClick(this::navegarAAgendas);
            navbarController.setOnPreguntasClick(this::navegarAPreguntas);
            navbarController.setOnPerfilClick(this::navegarAPerfil);
        }
    }

    private void navegarAHome() {
    }

    private void navegarAAgendas() {
        abrirMisCitas();
    }

    private void navegarAPreguntas() {
        Navegacion.cambiarAEscenaConControlador(
            stagePrincipal,
            "/Vista/Asistente.fxml",
            (AsistenteController c) -> c.inicializar(usuarioActual, stagePrincipal)
        );
    }

    private void navegarAPerfil() {
        Navegacion.cambiarAEscenaConControlador(
            stagePrincipal,
            "/Vista/Perfil.fxml",
            (PerfilController c) -> c.inicializar(usuarioActual, stagePrincipal, nombreSedeActual, idSedeActual)
        );
    }
    
    @FXML
    private void abrirRegistrarCita() {
        Navegacion.cambiarAEscenaConControlador(
            stagePrincipal,
            "/Vista/Especialidades.fxml",
            (EspecialidadesController c) -> 
                c.inicializarConSede(idSedeActual, idPacienteActual, usuarioActual, stagePrincipal)
        );
    }
    
    @FXML
    private void abrirGestionSedes() {
        Navegacion.cambiarAEscenaConControlador(
            stagePrincipal,
            "/Vista/Sedes.fxml",
            (SedesController c) -> c.inicializar(usuarioActual, stagePrincipal)
        );
    }
    @FXML
    private void abrirMisCitas() {
        Navegacion.cambiarAEscenaConControlador(
            stagePrincipal,
            "/Vista/Citas.fxml",
            (CitasController c) -> c.inicializar(idPacienteActual, usuarioActual, stagePrincipal)
        );
    }    
}