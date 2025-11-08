package Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioDisponible {
    private int id;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;

    public HorarioDisponible(int id, int idMedico, LocalDate fecha, LocalTime hora, String estado) {
        this.id = id;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    


}