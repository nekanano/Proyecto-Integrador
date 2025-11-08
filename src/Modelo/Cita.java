package Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private int idCita;
    private int idMedico;
    private String nombreMedico;
    private String especialidad;
    private String sede;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;

    public Cita(int idCita, int idMedico, String nombreMedico, String especialidad, 
                String sede, LocalDate fecha, LocalTime hora, String estado) {
        this.idCita = idCita;
        this.idMedico = idMedico;
        this.nombreMedico = nombreMedico;
        this.especialidad = especialidad;
        this.sede = sede;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
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