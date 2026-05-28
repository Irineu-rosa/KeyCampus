package com.KeyCampus.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    @FXML private Button btnFila;
    @FXML private Button btnRetirar;

    private Map<Button, String> rotas;

    private Button botaoAtivo;

    @FXML
    public void initialize() {
        rotas = Map.of(
                btnDashboard,    "/views/pages/DashboardPage.fxml",
                btnSalas,        "/views/pages/SalasPage.fxml",
                btnChaves,       "/views/pages/ChavesPage.fxml",
                btnUsuarios,     "/views/pages/UsuariosPage.fxml",
                btnAgendamentos, "/views/pages/AgendamentosPage.fxml",
                btnRetirar,      "/views/pages/RetiradaPage.fxml"
        );

        navegarPara(btnDashboard, "/views/pages/DashboardPage.fxml");
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