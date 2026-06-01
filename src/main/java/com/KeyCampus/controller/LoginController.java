package com.KeyCampus.controller;

import com.KeyCampus.model.Usuario;
import com.KeyCampus.service.AuthService;
import com.KeyCampus.service.AuthService.ResultadoLogin;
import com.KeyCampus.session.SessaoUsuario;
import com.KeyCampus.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField tfNome;
    @FXML private TextField tfMatricula;
    @FXML private PasswordField pfSenha;

    private final AuthService authService = new AuthService();

    private String matriculaAtual;

    @FXML
    public void entrar() {
        String nome      = tfNome.getText().trim();
        String matricula = tfMatricula.getText().trim();
        String senha     = pfSenha.getText();

        if (nome.isEmpty() || matricula.isEmpty()) {
            AlertUtil.erro("Preencha nome e matrícula.");
            return;
        }

        ResultadoLogin resultado = authService.login(nome, matricula);

        switch (resultado) {
            case PRIMEIRO_ACESSO -> {
                matriculaAtual = matricula;
                abrirCriacaoSenha(matricula);
            }
            case SUCESSO -> {
                if (senha.isEmpty()) {
                    AlertUtil.erro("Digite sua senha.");
                    return;
                }
                Usuario usuario = authService.autenticar(matricula, senha);
                if (usuario == null) {
                    AlertUtil.erro("Senha incorreta.");
                    return;
                }
                SessaoUsuario.iniciar(usuario);
                abrirTelaPrincipal();
            }
            case CREDENCIAL_INVALIDA ->
                AlertUtil.erro("Nome ou matrícula inválidos.");
        }
    }

    private void abrirCriacaoSenha(String matricula) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/pages/CriacaoSenha.fxml"));
            Parent root = loader.load();

            CriacaoSenhaController ctrl = loader.getController();
            ctrl.setMatricula(matricula);

            Stage stage = (Stage) tfNome.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaPrincipal() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/views/MainLayout.fxml"));
            Stage stage = (Stage) tfNome.getScene().getWindow();
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
