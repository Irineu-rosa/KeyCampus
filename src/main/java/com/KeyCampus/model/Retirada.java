package com.KeyCampus.model;

import java.time.LocalDateTime;

public class Retirada {

    private Long id;

    private Long usuarioId;

    private Long chaveId;

    private LocalDateTime retiradaEm;

    private LocalDateTime devolucaoEm;

    private StatusRetirada status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id=id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId=usuarioId;
    }

    public Long getChaveId() {
        return chaveId;
    }

    public void setChaveId(Long chaveId) {
        this.chaveId=chaveId;
    }

    public LocalDateTime getRetiradaEm() {
        return retiradaEm;
    }

    public void setRetiradaEm(
            LocalDateTime retiradaEm
    ){
        this.retiradaEm=retiradaEm;
    }

    public LocalDateTime getDevolucaoEm() {
        return devolucaoEm;
    }

    public void setDevolucaoEm(
            LocalDateTime devolucaoEm
    ){
        this.devolucaoEm=devolucaoEm;
    }

    public StatusRetirada getStatus() {
        return status;
    }

    public void setStatus(
            StatusRetirada status
    ){
        this.status=status;
    }

}