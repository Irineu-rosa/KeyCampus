package com.KeyCampus.model;

import java.time.LocalTime;
import java.time.LocalDate;

public class Agendamento{
    private Long id;
    private Usuario usuario;
    private Sala sala;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Agendamento(){}

    public Agendamento(
            Usuario usuario,
            Sala sala,
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim
    ){
        this.usuario = usuario;
        this.sala = sala;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario(){
        return usuario;
    }

    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
}