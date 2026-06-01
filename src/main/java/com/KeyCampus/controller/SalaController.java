package com.KeyCampus.controller;

import com.KeyCampus.dao.SalaDao;
import com.KeyCampus.model.Sala;
import com.KeyCampus.model.StatusSala;

import com.KeyCampus.util.ValidadorUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class SalaController {


    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtDescricao;
    @FXML
    private ComboBox<StatusSala> cbStatus;
    @FXML
    private Label lblTitleNovaSala;
    @FXML
    private VBox listaSalas;


    private SalaDao salaDao = new SalaDao();
    private Sala salaEdicao;
    private Stage cadastroSalaStage;
    private Stage salaStage;

    @FXML
    public void initialize() {
        if (listaSalas != null) {
            carregarInformacoes();
        }

        if (cbStatus != null) {
            cbStatus.getItems().addAll(StatusSala.values());
        }
    }

    public void carregarInformacoes(){
        carregarTabela();
    }

    @FXML
    public void salvar(){
        boolean valido = ValidadorUtil.de()
                .campo(txtNome.getText(), "Digite o nome da sala")
                .combo(cbStatus.getValue(), "Selecione o status da sala")
                .validar();

        if(!valido) return;

        Sala sala;
        if(salaEdicao != null){
            sala = salaEdicao;
        }
        else{
            sala = new Sala();
        }
        sala.setNome(txtNome.getText());
        sala.setDescricao(txtDescricao.getText());
        sala.setStatus(cbStatus.getValue());
        if(sala.getId()==null){
            salaDao.salvar(sala);
        }
        else{
            salaDao.atualizar(sala);
        }
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    public void carregarSala(
            Sala sala
    ){
        this.salaEdicao = sala;
        if (!salaEdicao.getId().toString().isEmpty()){
            lblTitleNovaSala.setText("Editar Sala");
        }
        txtNome.setText(sala.getNome());
        txtDescricao.setText(sala.getDescricao());
        cbStatus.setValue(sala.getStatus());
    }

    public void carregarTabela(){

        listaSalas.getChildren().clear();

        for(Sala sala : salaDao.listarObjetos()){
            VBox linha = criarLinhaSala(sala);
            listaSalas.getChildren().add(linha);
        }
    }

    private VBox criarLinhaSala(Sala sala){
        Circle status = new Circle(4);

        switch(sala.getStatus()){
            case DISPONIVEL:
                status.getStyleClass().add("dot-free");
                break;

            case EM_USO:
                status.getStyleClass().add("dot-busy");
                break;

            case EM_LIMPEZA:
                status.getStyleClass().add("dot-clean");
                break;
        }

        Label nome = new Label(sala.getNome());
        nome.getStyleClass().add("room-name");

        Label tag = new Label(sala.getStatus().name());
        tag.getStyleClass().add("room-tag");

        switch (sala.getStatus()) {
            case DISPONIVEL ->
                    tag.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #166534; -fx-background-radius: 6; -fx-padding: 2 8;");
            case EM_USO ->
                    tag.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-background-radius: 6; -fx-padding: 2 8;");
            case EM_LIMPEZA ->
                    tag.setStyle("-fx-background-color: #FEF9C3; -fx-text-fill: #854D0E; -fx-background-radius: 6; -fx-padding: 2 8;");
        }

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        Button btnEditar = new Button("✏");
        Button btnExcluir = new Button("🗑");

        btnEditar.getStyleClass().add("btn-edit");
        btnExcluir.getStyleClass().add("btn-delete");

        btnEditar.setOnAction(e -> editarSala(sala));
        btnExcluir.setOnAction(e -> excluirSala(sala));

        HBox acoes = new HBox(8, btnEditar, btnExcluir);
        HBox linha = new HBox(10, status, nome, espaco, tag, acoes);

        Label descricao = new Label("Descrição: " + (sala.getDescricao().isEmpty()? "Sem descricao" :  sala.getDescricao()));
        descricao.setWrapText(true);
        descricao.setStyle("-fx-text-fill:#94A3B8;" +
                            "-fx-font-size:12px;" +
                            "-fx-padding:0 0 0 14;"
        );

        VBox card = new VBox(5);
        card.getChildren().addAll(linha, descricao);

        card.getStyleClass().add("room-row");

        return card;
    }

    @FXML
    public void abrirCadastroSala() {
        try {
            if (cadastroSalaStage != null && cadastroSalaStage.isShowing()) {
                cadastroSalaStage.toFront();
                cadastroSalaStage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/CadastroSala.fxml")
            );

            cadastroSalaStage = new Stage();
            cadastroSalaStage.setScene(new Scene(loader.load()));
            cadastroSalaStage.setTitle("Nova Sala");
            cadastroSalaStage.setResizable(false);
            cadastroSalaStage.initModality(Modality.APPLICATION_MODAL);
            cadastroSalaStage.initOwner(listaSalas.getScene().getWindow());
            cadastroSalaStage.setOnHidden(event -> {
                carregarInformacoes();
                cadastroSalaStage = null;
            });
            cadastroSalaStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void excluirSala(
            Sala sala
    ){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Excluir");
        alert.setHeaderText("Deseja excluir?");
        alert.setContentText(sala.getNome());
        alert.showAndWait().ifPresent(resposta -> {
            if(resposta == ButtonType.OK)
            {
                salaDao.excluir(sala.getId());
                carregarInformacoes();
            }
        });
    }

    private void editarSala(Sala sala) {
        try {
            if (salaStage != null && salaStage.isShowing()) {
                salaStage.toFront();
                salaStage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CadastroSala.fxml"));
            Scene scene = new Scene(loader.load());

            SalaController controller = loader.getController();
            controller.carregarSala(sala);

            salaStage = new Stage();
            salaStage.setScene(scene);
            salaStage.setTitle("Editar Sala");
            salaStage.setResizable(false);
            salaStage.initModality(Modality.APPLICATION_MODAL);
            salaStage.initOwner(listaSalas.getScene().getWindow());
            salaStage.setOnHidden(e -> {
                carregarInformacoes();
                salaStage = null;
            });
            salaStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}