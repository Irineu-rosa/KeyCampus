package com.KeyCampus.controller;

import com.KeyCampus.dao.*;
import com.KeyCampus.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class RetiradaController {

    @FXML
    private ComboBox<Usuario> cbUsuarios;

    @FXML
    private ComboBox<Sala> cbSalas;

    @FXML
    private ComboBox<Chave> cbChaves;

    private UsuarioDao usuarioDao = new UsuarioDao();

    private SalaDao salaDao = new SalaDao();

    private ChaveDao chaveDao = new ChaveDao();

    @FXML
    public void initialize(){
        cbUsuarios.getItems().addAll(usuarioDao.listar());
        cbSalas.getItems().addAll(salaDao.listarObjetos());
        cbChaves.getItems().addAll(chaveDao.listarObjetos());
    }

    @FXML
    public void retirar(){
        Usuario usuario = cbUsuarios.getValue();
        Sala sala = cbSalas.getValue();
        Chave chave = cbChaves.getValue();
        System.out.println(usuario.getId());
        System.out.println(sala.getId());
        System.out.println(chave.getId());

        new RetiradaDao().retirar(usuario.getId(), chave.getId());
    }

}