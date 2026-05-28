package com.KeyCampus.controller;

import com.KeyCampus.dao.ChaveDao;
import com.KeyCampus.dao.SalaDao;
import com.KeyCampus.model.*;

import com.KeyCampus.util.ValidadorUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChaveController {
    @FXML
    private VBox listaChaves ;
    @FXML
    private Label lblTitleNovaChave;
    @FXML
    private TextField txtNumero;
    @FXML
    private ComboBox<TipoChave> cbTipo;
    @FXML
    private ComboBox<Sala> cbSala;
    @FXML
    private ComboBox<StatusChave> cbStatus;
    @FXML
    private CheckBox chkAtiva;

    private ChaveDao chaveDao = new ChaveDao();
    private Stage cadastroChaveStage;
    private Stage chaveStage;
    private Chave chaveEdicao;

    @FXML
    public void initialize() {
        if (listaChaves != null) {
            carregarInformacoes();
        }
        else {
           if (cbStatus != null) cbStatus.getItems().addAll(StatusChave.values());
           if (cbTipo   != null) cbTipo.getItems().addAll(TipoChave.values());
           if (cbSala   != null) cbSala.getItems().addAll(new SalaDao().listarObjetos());
        }
    }

    private void carregarInformacoes(){
        carregarTabela();
    }

    public void carregarChave(Chave chave){
        this.chaveEdicao = chave;
        if (!chaveEdicao.getId().toString().isEmpty()){
            lblTitleNovaChave.setText("Editar Chave");
        }
        txtNumero.setText(chave.getNumero());
        cbTipo.setValue(chave.getTipo());
        cbSala.setValue(chave.getSala());
        cbStatus.setValue(chave.getStatus());
        chkAtiva.setSelected(chave.getAtiva());
    }

    public void carregarTabela(){
        listaChaves.getChildren().clear();

        for(Chave chave : chaveDao.listarObjetos()){
            HBox linha = criarLinhaChave(chave);
            listaChaves.getChildren().add(linha);
        }
    }

    private HBox criarLinhaChave(Chave chave) {
        HBox linha = new HBox(0);
        linha.getStyleClass().add("room-row");
        linha.setAlignment(Pos.CENTER_LEFT);

        String nomeSala = (chave.getSala() != null && chave.getSala().getNome() != null)
                ? chave.getSala().getNome() : "Sem sala";

        String numero = (chave.getNumero() != null && !chave.getNumero().isBlank())
                ? chave.getNumero() : "Sem número";

        String tipo = (chave.getTipo() != null)
                ? chave.getTipo().toString() : "Sem tipo";

        String ativa = (chave.getAtiva() != null)
                ? (chave.getAtiva() ? "Ativa" : "Inativa") : "Sem status de ativação";

        String statusStr = (chave.getStatus() != null)
                ? chave.getStatus().name() : null;

        Label lNumero = new Label(numero);
        Label lNome   = new Label(nomeSala);
        Label lTipo   = new Label(tipo);
        Label lAtiva  = new Label(ativa);
        Button btnEditar = new Button("✏");
        Button btnExcluir = new Button("🗑");

        btnEditar.getStyleClass().add("btn-edit");
        btnExcluir.getStyleClass().add("btn-delete");

        btnEditar.setOnAction(e -> editarChave(chave));
        btnExcluir.setOnAction(e -> excluirChave(chave));

        HBox acoes = new HBox(8, btnEditar, btnExcluir);

        for (Label l : new Label[]{lNumero, lNome, lTipo, lAtiva}) {
            l.setStyle("-fx-text-fill: #0F172A; -fx-font-size: 13px;");
            HBox.setHgrow(l, Priority.ALWAYS);
            l.setMaxWidth(Double.MAX_VALUE);
        }

        Label lStatus = new Label(statusStr != null ? statusStr : "Sem status");
        lStatus.getStyleClass().add("room-tag");
        if (statusStr == null) {
            lStatus.getStyleClass().add("tag-clean");
        } else {
            switch (chave.getStatus()) {
                case EM_USO     -> lStatus.getStyleClass().add("tag-busy");
                case DISPONIVEL -> lStatus.getStyleClass().add("tag-free");
                default         -> lStatus.getStyleClass().add("tag-clean");
            }
        }
        HBox.setHgrow(lStatus, Priority.NEVER);

        linha.getChildren().addAll(lNumero, lNome, lTipo, lAtiva, lStatus, acoes);
        return linha;
    }

    public void salvar(){
        boolean valido = ValidadorUtil.de()
                .campo(txtNumero.getText(), "Digite o número da chave")
                .combo(cbTipo.getValue(),   "Selecione o tipo da chave")
                .combo(cbSala.getValue(),   "Selecione a sala")
                .validar();

        if (!valido) return;

        Chave chave;

        if (chaveEdicao != null){
            chave = chaveEdicao;
        }
        else {
            chave = new Chave();
        }
        chave.setNumero(txtNumero.getText());
        chave.setTipo(cbTipo.getValue());
        chave.setSala(cbSala.getValue());
        chave.setStatus(cbStatus.getValue());
        chave.setAtiva(chkAtiva.isSelected());
        if(chave.getId() == null){
            chaveDao.salvar(chave);
        }
        else{
            chaveDao.atualizar(chave);
        }
        Stage stage = (Stage) txtNumero.getScene().getWindow();
        stage.close();
    }

    private void excluirChave(Chave chave){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Excluir");
        alert.setHeaderText("Deseja excluir?");
        alert.setContentText(chave.getNumero() + " - " + chave.getSala().getNome());
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK){
                chaveDao.excluir(chave.getId());
                carregarInformacoes();
            }
        });
    }

    private void editarChave(Chave chave){
        try{
            if (chaveStage != null && chaveStage.isShowing()){
                chaveStage.toFront();
                chaveStage.requestFocus();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CadastroChave.fxml"));
            Scene scene = new Scene(loader.load());

            ChaveController controller = loader.getController();
            controller.carregarChave(chave);

            chaveStage = new Stage();
            chaveStage.setScene(scene);
            chaveStage.setTitle("Editar Chave");
            chaveStage.setResizable(false);
            chaveStage.initModality(Modality.APPLICATION_MODAL);
            chaveStage.initOwner(listaChaves.getScene().getWindow());
            chaveStage.setOnHidden(e -> {
                carregarInformacoes();
                chaveStage = null;
            });
            chaveStage.showAndWait();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    public void abrirCadastroChave(){
        try {
            if (cadastroChaveStage != null && cadastroChaveStage.isShowing()) {
                cadastroChaveStage.toFront();
                cadastroChaveStage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/CadastroChave.fxml")
            );

            cadastroChaveStage = new Stage();
            cadastroChaveStage.setScene(new Scene(loader.load()));
            cadastroChaveStage.setTitle("Nova Chave");
            cadastroChaveStage.setResizable(false);
            cadastroChaveStage.initModality(Modality.APPLICATION_MODAL);
            cadastroChaveStage.initOwner(listaChaves.getScene().getWindow());
            cadastroChaveStage.setOnHidden(event -> {
                carregarInformacoes();
                cadastroChaveStage = null;
            });
            cadastroChaveStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}