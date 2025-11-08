package modelo;

public class Medico {
    private int id;
    private String nombre;
    private int idEspecialidad;
    private int idSede;

    public Medico(int id, String nombre, int idEspecialidad, int idSede) {
        this.id = id;
        this.nombre = nombre;
        this.idEspecialidad = idEspecialidad;
        this.idSede = idSede;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public int getIdSede() {
        return idSede;
    }

    public void setIdSede(int idSede) {
        this.idSede = idSede;
    }

    
}