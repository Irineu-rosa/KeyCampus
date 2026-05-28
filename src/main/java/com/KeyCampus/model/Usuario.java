package com.KeyCampus.model;

import javafx.fxml.FXML;

public class Usuario{
    private Long id;
    private String nome;
    private String matricula;
    private TipoUsuario tipo;
    private boolean ativo;

    @Override
    public String toString(){
      return nome;
    }

    public Usuario (){
    }

    public Usuario(
            String nome,
            String matricula,
            TipoUsuario tipo
    ){
        this.nome=nome;
        this.matricula=matricula;
        this.tipo=tipo;
        this.ativo=true;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getMatricula(){
        return matricula;
    }

    public void setMatricula(String matricula){
        this.matricula = matricula;
    }

    public TipoUsuario getTipo(){
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}