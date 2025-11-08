package modelo;

public class Paciente {
    private int id;
    private String nombre;
    private String dni;
    private String correo;
    private String contrasena;

    public Paciente(int id, String nombre, String dni, String correo, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDni() { return dni; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
}