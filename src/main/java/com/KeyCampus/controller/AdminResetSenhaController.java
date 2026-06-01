package com.KeyCampus.controller;

import com.KeyCampus.dao.UsuarioDao;
import com.KeyCampus.model.TipoUsuario;
import com.KeyCampus.model.Usuario;
import com.KeyCampus.service.AuthService;
import com.KeyCampus.session.SessaoUsuario;
import com.KeyCampus.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class AdminResetSenhaController {

    @FXML private ComboBox<Usuario> cbUsuarios;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        if (SessaoUsuario.get() == null
                || SessaoUsuario.get().getTipo() != TipoUsuario.ADMIN) {
            AlertUtil.erro("Acesso negado.");
            return;
        }

        new UsuarioDao().listar().stream()
            .filter(u -> !u.getId().equals(SessaoUsuario.get().getId()))
            .forEach(cbUsuarios.getItems()::add);
    }

    @FXML
    public void resetar() {
        Usuario usuario = cbUsuarios.getValue();

        if (usuario == null) {
            AlertUtil.erro("Selecione um usuário.");
            return;
        }

        authService.resetarSenha(usuario.getId());

        AlertUtil.sucesso(
            "Senha de " + usuario.getNome() + " resetada.\n"
            + "O usuário deverá criar uma nova senha no próximo acesso."
        );

        cbUsuarios.setValue(null);
    }
}
