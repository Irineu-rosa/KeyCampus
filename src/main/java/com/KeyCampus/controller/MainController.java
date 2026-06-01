package com.KeyCampus.controller;

import com.KeyCampus.model.TipoUsuario;
import com.KeyCampus.model.Usuario;
import com.KeyCampus.session.SessaoUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Map;

public class MainController {

    @FXML private StackPane contentArea;

    @FXML private Button btnDashboard;
    @FXML private Button btnSalas;
    @FXML private Button btnChaves;
    @FXML private Button btnUsuarios;
    @FXML private Button btnAgendamentos;
    @FXML private Button btnRetirar;
    @FXML private Label lblnameuser;
    @FXML private Label lbltipouser;
    @FXML private Label lblAvatar;

    private Map<Button, String> rotas;

    private Button botaoAtivo;

    @FXML
    public void initialize() {
        Usuario usuario = SessaoUsuario.get();
        prepararinterface(usuario);
        if (usuario != null) {
            lblnameuser.setText(usuario.getNome());
            lbltipouser.setText(usuario.getTipo().name());
        }

        if (usuario != null) {
            lblnameuser.setText(usuario.getNome());
            lbltipouser.setText(usuario.getTipo().name());

            // Iniciais do nome para o avatar
            String[] partes = usuario.getNome().split(" ");
            String iniciais = partes.length >= 2
                    ? "" + partes[0].charAt(0) + partes[1].charAt(0)
                    : "" + partes[0].charAt(0);
            lblAvatar.setText(iniciais.toUpperCase());
        }

        rotas = Map.of(
                btnDashboard, "/views/pages/DashboardPage.fxml",
                btnSalas, "/views/pages/SalasPage.fxml",
                btnChaves, "/views/pages/ChavesPage.fxml",
                btnUsuarios, "/views/pages/UsuariosPage.fxml",
                btnAgendamentos,"/views/pages/AgendamentosPage.fxml",
                btnRetirar, "/views/pages/RetiradaPage.fxml"
        );

        navegarPara(btnDashboard, "/views/pages/DashboardPage.fxml");
    }

    @FXML
    public void sair() {
        SessaoUsuario.encerrar();
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/views/pages/Login.fxml")));
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepararinterface(Usuario usuario) {
        if (usuario.getTipo() != TipoUsuario.ADMIN) {
            btnChaves.setVisible(false);
            btnChaves.setManaged(false);
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);
        }
        if (usuario.getTipo() == TipoUsuario.LIMPEZA){
            btnAgendamentos.setVisible(false);
            btnAgendamentos.setManaged(false);
        }
    }

    @FXML
    public void navegar(ActionEvent event) {
        Button botaoClicado = (Button) event.getSource();
        String fxml = rotas.get(botaoClicado);
        if (fxml != null) {
            navegarPara(botaoClicado, fxml);
        }
    }

    private void navegarPara(Button botao, String fxmlPath) {
        try {
            if (botaoAtivo != null) {
                botaoAtivo.getStyleClass().remove("active");
            }

            botao.getStyleClass().add("active");
            botaoAtivo = botao;

            Node pagina = FXMLLoader.load(getClass().getResource(fxmlPath));

            contentArea.getChildren().setAll(pagina);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}