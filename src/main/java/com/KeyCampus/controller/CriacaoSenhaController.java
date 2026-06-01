package com.KeyCampus.controller;

import com.KeyCampus.model.Usuario;
import com.KeyCampus.service.AuthService;
import com.KeyCampus.session.SessaoUsuario;
import com.KeyCampus.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class CriacaoSenhaController {

    @FXML private PasswordField pfNovaSenha;
    @FXML private PasswordField pfConfirmar;

    private final AuthService authService = new AuthService();
    private String matricula;

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @FXML
    public void salvar() {
        String nova      = pfNovaSenha.getText();
        String confirmar = pfConfirmar.getText();

        if (nova.isBlank()) {
            AlertUtil.erro("Digite uma senha.");
            return;
        }
        if (nova.length() < 6) {
            AlertUtil.erro("A senha deve ter pelo menos 6 caracteres.");
            return;
        }
        if (!nova.equals(confirmar)) {
            AlertUtil.erro("As senhas não coincidem.");
            return;
        }

        authService.definirSenha(matricula, nova);

        Usuario usuario = authService.autenticar(matricula, nova);
        SessaoUsuario.iniciar(usuario);

        AlertUtil.sucesso("Senha criada com sucesso!");
        abrirTelaPrincipal();
    }

    private void abrirTelaPrincipal() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/views/MainLayout.fxml"));
            Stage stage = (Stage) pfNovaSenha.getScene().getWindow();
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
