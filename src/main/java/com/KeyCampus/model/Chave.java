package com.KeyCampus.model;

public class Chave {
    private Long id;
    private String numero;
    private TipoChave tipo;
    private Boolean ativa;
    private StatusChave status;
    private Sala sala;

    @Override
    public String toString(){
        return tipo.name();
    }

    public Chave(){
    }

    public Chave(
            String numero,
            TipoChave tipo,
            Sala sala
    ){
        this.numero = numero;
        this.tipo = tipo;
        this.sala = sala;
        this.ativa = true;
        this.status = StatusChave.valueOf("DISPONIVEL");
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoChave getTipo() {
        return tipo;
    }

    public void setTipo(TipoChave tipo) {
        this.tipo = tipo;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public StatusChave getStatus() {
        return status;
    }

    public void setStatus(StatusChave status) {
        this.status = status;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }
}