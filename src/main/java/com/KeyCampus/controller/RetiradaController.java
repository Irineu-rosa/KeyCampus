package com.KeyCampus.controller;

import com.KeyCampus.dao.*;
import com.KeyCampus.model.*;
import com.KeyCampus.service.RetiradaService;
import com.KeyCampus.util.AlertUtil;
import com.KeyCampus.session.SessaoUsuario;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

public class RetiradaController {

    @FXML private ComboBox<Usuario> cbUsuarios;
    @FXML private ComboBox<Sala> cbSalas;
    @FXML private ComboBox<Chave> cbChaves;

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final SalaDao salaDao = new SalaDao();
    private final ChaveDao chaveDao = new ChaveDao();
    private final AgendamentoDao agendamentoDao = new AgendamentoDao();
    private final RetiradaService retiradaService = new RetiradaService();

    @FXML
    public void initialize() {
        cbUsuarios.getItems().addAll(usuarioDao.listar());

        cbUsuarios.valueProperty().addListener((obs, antigo, usuario) -> {
            if (usuario == null) return;
            carregarSalas(usuario);
            carregarChaves(usuario);
        });
    }

    private void carregarSalas(Usuario usuario) {
        cbSalas.getItems().clear();
        List<Sala> salas;
        System.out.println(usuario.getTipo());
        switch (usuario.getTipo()) {
            case ADMIN -> salas = salaDao.listarObjetos();
            case LIMPEZA -> salas = salaDao.listarParaLimpeza();
            case PALESTRANTE -> salas = agendamentoDao.salasDisponiveisParaPalestrante(usuario.getId());
            default -> salas = List.of();
        }

        cbSalas.getItems().addAll(salas);
    }

    private void carregarChaves(Usuario usuario) {
        cbChaves.getItems().clear();
        List<Chave> chaves;
        switch (usuario.getTipo()) {
            case ADMIN -> chaves = chaveDao.listarDisponiveis();
            case LIMPEZA -> chaves = chaveDao.listarDisponiveisPorTipo(TipoChave.LIMPEZA);
            case PALESTRANTE -> chaves = chaveDao.listarDisponiveisPorTipo(TipoChave.FUNCIONARIO);
            default -> chaves = List.of();
        }

        cbChaves.getItems().addAll(chaves);
    }

    @FXML
    public void retirar() {
        Usuario usuario = cbUsuarios.getValue();
        Sala sala = cbSalas.getValue();
        Chave chave = cbChaves.getValue();

        if (usuario == null || sala == null || chave == null) {
            AlertUtil.erro("Preencha todos os campos.");
            return;
        }

        if (usuario.getTipo() == TipoUsuario.PALESTRANTE) {
            boolean agendamentoValido = agendamentoDao.usuarioPossuiAgendamentoAtivo(usuario.getId(), sala.getId());

            if (!agendamentoValido) {
                AlertUtil.erro("Você só pode retirar a chave a partir de 10 minutos antes do seu agendamento.");
                return;
            }
        }
        if (!retiradaService.podeRetirar(usuario.getId(), sala.getId(), chave.getId())) {
            return;
        }
        new RetiradaDao().retirar(usuario.getId(), chave.getId());
        AlertUtil.sucesso("Chave retirada com sucesso!");
    }
}