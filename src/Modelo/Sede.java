package modelo;

public class Sede {
    private int id;
    private String nombre;
    private String distrito;
    private String direccion;
    private String horario;
    private String tipo;

    public Sede(int id, String nombre, String distrito, String direccion, String horario, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.distrito = distrito;
        this.direccion = direccion;
        this.horario = horario;
        this.tipo = tipo;
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

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}