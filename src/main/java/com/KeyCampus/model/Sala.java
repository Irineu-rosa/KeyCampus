package com.KeyCampus.model;

public class Sala {
    private Long id;
    private String nome;
    private String descricao;
    private StatusSala status;

    @Override
    public String toString(){
        return nome;
    }

    public Sala(){
    }
    public Sala(
            String nome,
            String descricao,
            StatusSala status
    ){
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusSala getStatus() {
        return status;
    }

    public void setStatus(StatusSala status) {
        this.status = status;
    }
}