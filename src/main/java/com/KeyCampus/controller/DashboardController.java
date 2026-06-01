package com.KeyCampus.controller;

import com.KeyCampus.dao.AgendamentoDao;
import com.KeyCampus.dao.ChaveDao;
import com.KeyCampus.dao.SalaDao;
import com.KeyCampus.model.Agendamento;
import com.KeyCampus.model.Sala;

import com.KeyCampus.model.TipoUsuario;
import com.KeyCampus.model.Usuario;
import com.KeyCampus.session.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML private Label lblData;
    @FXML private Label lblTotalSalas;
    @FXML private Label lblSalasLivres;
    @FXML private Label lblSalasEmUso;
    @FXML private Label lblChavesAtivas;
    @FXML private VBox listaSalas;
    @FXML private VBox listaAgendamento;
    @FXML private Button btnNovaSala;
    @FXML private Button btnAgendamentos;
    @FXML private VBox boxAgendamentos;

    private SalaDao salaDao = new SalaDao();
    private ChaveDao chaveDao = new ChaveDao();
    private AgendamentoDao agendamentoDao = new AgendamentoDao();

    private Stage cadastroSalaStage;
    private Stage cadastroAgendamentoStage;
    private Stage salaStage;

    @FXML
    public void initialize(){
        Usuario usuario = SessaoUsuario.get();
        prepararinterface(usuario);
        carregarInformacoes();
    }

    public void carregarInformacoes(){
        carregarData();
        carregarIndicadores();
        carregarSalas();
        carregarAgendamentos();
    }

    public void prepararinterface(Usuario usuario) {
        if (usuario.getTipo() != TipoUsuario.ADMIN) {
            btnNovaSala.setVisible(false);
            btnNovaSala.setManaged(false);
        }
        if (usuario.getTipo() == TipoUsuario.LIMPEZA){
            btnAgendamentos.setVisible(false);
            btnAgendamentos.setManaged(false);
            boxAgendamentos.setVisible(false);
            boxAgendamentos.setManaged(false);
        }
    }

    private void carregarData(){
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt","BR"));
        lblData.setText(LocalDate.now().format(formatter));
    }

    private void carregarIndicadores(){
        lblTotalSalas.setText(String.valueOf(salaDao.total()));
        lblSalasLivres.setText(String.valueOf(salaDao.totalDisponiveis()));
        lblSalasEmUso.setText(String.valueOf(salaDao.totalSalaEmUso()));
        lblChavesAtivas.setText(String.valueOf(chaveDao.totalchavesAtiva()));
    }

    private void carregarSalas(){
        listaSalas.getChildren().clear();
        for(Sala sala : salaDao.listarObjetos()){
            HBox linha = criarLinhaSala(sala);
            listaSalas.getChildren().add(linha);
        }
    }

    private void carregarAgendamentos() {
        List<Agendamento> lista = new ArrayList<>();;

        listaAgendamento.getChildren().clear();
        Usuario usuario = SessaoUsuario.get();

        if(usuario.getTipo() == TipoUsuario.ADMIN){
            lista = agendamentoDao.listarObjetos();
        } else if (usuario.getTipo() == TipoUsuario.PALESTRANTE) {
            lista = agendamentoDao.buscarPorUsuario(usuario.getId());
        }

        for (Agendamento agendamento : lista) {
            HBox linha = criarLinhaAgendamento(agendamento);
            listaAgendamento.getChildren().add(linha);
        }
    }

    private HBox criarLinhaSala(Sala sala) {
        Usuario usuario = SessaoUsuario.get();

        Circle status = new Circle(4);
        status.getStyleClass().add(switch (sala.getStatus()) {
            case DISPONIVEL -> "dot-free";
            case EM_USO -> "dot-busy";
            case EM_LIMPEZA -> "dot-clean";
        });

        Label nome = new Label(sala.getNome());
        nome.getStyleClass().add("room-name");

        Label tag = new Label(sala.getStatus().name());
        tag.getStyleClass().add("room-tag");
        tag.setStyle(switch (sala.getStatus()) {
            case DISPONIVEL -> "-fx-background-color: #DCFCE7; -fx-text-fill: #166534;";
            case EM_USO -> "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;";
            case EM_LIMPEZA -> "-fx-background-color: #FEF9C3; -fx-text-fill: #854D0E;";
        } + " -fx-background-radius: 6; -fx-padding: 2 8 2 8;");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox linha = new HBox(10, status, nome, espaco, tag);
        linha.getStyleClass().add("room-row");

        if (usuario.getTipo() == TipoUsuario.ADMIN) {
            Button btnEditar = new Button("✏");
            Button btnExcluir = new Button("🗑");
            btnEditar .getStyleClass().add("btn-edit");
            btnExcluir.getStyleClass().add("btn-delete");
            btnEditar .setOnAction(e -> editarSala(sala));
            btnExcluir.setOnAction(e -> excluirSala(sala));
            linha.getChildren().add(new HBox(8, btnEditar, btnExcluir));
        }

        return linha;
    }

    private HBox criarLinhaAgendamento(
            Agendamento agendamento
    ){
        Circle status = new Circle(3);
        status.getStyleClass().add("sched-dot");

        Label hora = new Label(agendamento.getHoraInicio().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm")
        ));

        hora.getStyleClass().add("sched-time");

        Label nomeSala = new Label(agendamento.getSala().getNome());

        nomeSala.getStyleClass().add("sched-name");

        long duracao = java.time.Duration.between(agendamento.getHoraInicio(), agendamento.getHoraFim()).toHours();
        Label info = new Label(agendamento.getUsuario().getNome()
                        + " · "
                        + duracao
                        + "h"
        );

        info.getStyleClass().add("sched-info");
        VBox detalhes = new VBox(nomeSala, info);
        HBox linha = new HBox(12, status, hora, detalhes);
        linha.setAlignment(Pos.TOP_LEFT);
        linha.getStyleClass().add("sched-row");
        return linha;
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

    @FXML
    public void abrirCadastroAgendamento() {
        try {
            if (cadastroAgendamentoStage != null && cadastroAgendamentoStage.isShowing()) {
                cadastroAgendamentoStage.toFront();
                cadastroAgendamentoStage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/CadastroAgendamento.fxml")
            );

            cadastroAgendamentoStage = new Stage();
            cadastroAgendamentoStage.setScene(new Scene(loader.load()));
            cadastroAgendamentoStage.setTitle("Novo Agendamento");
            cadastroAgendamentoStage.setResizable(false);
            cadastroAgendamentoStage.initModality(Modality.APPLICATION_MODAL);
            //cadastroAgendamentoStage.initOwner(listaAgendamentos.getScene().getWindow());
            cadastroAgendamentoStage.setOnHidden(event -> {
                carregarInformacoes();
                cadastroAgendamentoStage = null;
            });
            cadastroAgendamentoStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirRetirada(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/pages/RetiradaPage.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}