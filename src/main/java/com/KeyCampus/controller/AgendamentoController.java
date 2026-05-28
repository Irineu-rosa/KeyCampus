package com.KeyCampus.controller;

import com.KeyCampus.dao.AgendamentoDao;
import com.KeyCampus.dao.SalaDao;
import com.KeyCampus.dao.UsuarioDao;
import com.KeyCampus.model.*;
import com.KeyCampus.util.AlertUtil;
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

import com.KeyCampus.service.AgendamentoService;
import com.KeyCampus.service.FilaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AgendamentoController {

    @FXML
    private VBox listaAgendamentos;

    @FXML
    private Label lblTitleNovoAgendamento;
    @FXML
    private ComboBox<Usuario> cbUsuario;
    @FXML
    private ComboBox<Sala> cbSala;
    @FXML
    private DatePicker dpData;
    @FXML
    private TextField txtHoraInicio;
    @FXML
    private TextField txtHoraFim;

    private final AgendamentoDao agendamentoDao = new AgendamentoDao();
    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final FilaService filaService = new FilaService();
    private Stage cadastroAgendamentoStage;
    private Stage agendamentoStage;
    private Agendamento agendamentoEdicao;

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        if (listaAgendamentos != null) {
            carregarInformacoes();
        } else {
            if (cbUsuario != null) cbUsuario.getItems().addAll(new UsuarioDao().listar());
            if (cbSala != null) cbSala.getItems().addAll(new SalaDao().listarObjetos());
            if (dpData != null) dpData.setValue(LocalDate.now());
        }
    }

    private void carregarInformacoes() {
        carregarTabela();
    }

    public void carregarAgendamento(Agendamento agendamento) {
        this.agendamentoEdicao = agendamento;
        if (agendamentoEdicao.getId() != null) {
            lblTitleNovoAgendamento.setText("Editar Agendamento");
        }
        cbUsuario.setValue(agendamento.getUsuario());
        cbSala.setValue(agendamento.getSala());
        dpData.setValue(agendamento.getData());
        txtHoraInicio.setText(agendamento.getHoraInicio().format(HORA_FORMATTER));
        txtHoraFim.setText(agendamento.getHoraFim().format(HORA_FORMATTER));
    }

    public void carregarTabela() {
        listaAgendamentos.getChildren().clear();

        for (Agendamento agendamento : agendamentoDao.listarObjetos()) {
            HBox linha = criarLinhaAgendamento(agendamento);
            listaAgendamentos.getChildren().add(linha);
        }
    }

    private HBox criarLinhaAgendamento(Agendamento agendamento) {
        HBox linha = new HBox(0);
        linha.getStyleClass().add("room-row");
        linha.setAlignment(Pos.CENTER_LEFT);

        String nomeUsuario = (agendamento.getUsuario() != null && agendamento.getUsuario().getNome() != null)
                ? agendamento.getUsuario().getNome() : "Sem usuário";

        String nomeSala = (agendamento.getSala() != null && agendamento.getSala().getNome() != null)
                ? agendamento.getSala().getNome() : "Sem sala";

        String data = (agendamento.getData() != null)
                ? agendamento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Sem data";

        String horaInicio = (agendamento.getHoraInicio() != null)
                ? agendamento.getHoraInicio().format(HORA_FORMATTER) : "--:--";

        String horaFim = (agendamento.getHoraFim() != null)
                ? agendamento.getHoraFim().format(HORA_FORMATTER) : "--:--";

        String horario = horaInicio + " – " + horaFim;

        Label lUsuario = new Label(nomeUsuario);
        Label lSala = new Label(nomeSala);
        Label lData = new Label(data);
        Label lHorario = new Label(horario);
        Button btnEditar = new Button("✏");
        Button btnExcluir = new Button("🗑");

        btnEditar.getStyleClass().add("btn-edit");
        btnExcluir.getStyleClass().add("btn-delete");

        btnEditar.setOnAction(e -> editarAgendamento(agendamento));
        btnExcluir.setOnAction(e -> excluirAgendamento(agendamento));

        for (Label l : new Label[]{lUsuario, lSala, lData, lHorario}) {
            l.setStyle("-fx-text-fill: #0F172A; -fx-font-size: 13px;");
            HBox.setHgrow(l, Priority.ALWAYS);
            l.setMaxWidth(Double.MAX_VALUE);
        }

        HBox acoes = new HBox(8, btnEditar, btnExcluir);
        acoes.setAlignment(Pos.CENTER_RIGHT);

        linha.getChildren().addAll(lUsuario, lSala, lData, lHorario, acoes);
        return linha;
    }

    @FXML
    public void salvar() {
        boolean valido = ValidadorUtil.de()
                .combo(cbUsuario.getValue(), "Selecione o usuário")
                .combo(cbSala.getValue(),  "Selecione a sala")
                .campo(dpData.getValue() != null ? dpData.getValue().toString() : "", "Selecione a data")
                .campo(txtHoraInicio.getText(), "Digite a hora de início (HH:mm)")
                .campo(txtHoraFim.getText(), "Digite a hora de fim (HH:mm)")
                .validar();

        if (!valido) return;

        LocalTime horaInicio;
        LocalTime horaFim;
        try {
            horaInicio = LocalTime.parse(txtHoraInicio.getText().trim(), HORA_FORMATTER);
            horaFim = LocalTime.parse(txtHoraFim.getText().trim(),    HORA_FORMATTER);
        } catch (Exception ex) {
            AlertUtil.atencao("Formato de hora inválido. Use HH:mm.");
            return;
        }

        if (!horaFim.isAfter(horaInicio)) {
            AlertUtil.atencao("A hora de fim deve ser posterior à hora de início.");
            return;
        }

        Agendamento agendamento = (agendamentoEdicao != null) ? agendamentoEdicao : new Agendamento();
        agendamento.setUsuario(cbUsuario.getValue());
        agendamento.setSala(cbSala.getValue());
        agendamento.setData(dpData.getValue());
        agendamento.setHoraInicio(horaInicio);
        agendamento.setHoraFim(horaFim);

        boolean novoAgendamento = agendamento.getId() == null;

        String dataStr = dpData.getValue().toString();
        String inicioStr = horaInicio.format(HORA_FORMATTER);
        String fimStr = horaFim.format(HORA_FORMATTER);

        Long usuarioId = cbUsuario.getValue().getId();
        Long salaId    = cbSala.getValue().getId();

        if (!agendamentoService.podeAgendar(usuarioId, salaId, dataStr, inicioStr, fimStr)) {
            AlertUtil.atencao(
                    "Não foi possível agendar:\n" +
                            "• O usuário já possui um agendamento nesse horário, ou\n" +
                            "• A sala já está ocupada nesse período."
            );
            return;
        }

        if (novoAgendamento) {
            agendamentoDao.salvar(agendamento);
            filaService.adicionarFila(usuarioId, salaId);
            AlertUtil.sucesso("Agendamento realizado com sucesso.");
        } else {
            agendamentoDao.atualizar(agendamento);
            AlertUtil.sucesso("Agendamento atualizado com sucesso.");
        }

        Stage stage = (Stage) cbUsuario.getScene().getWindow();
        stage.close();
    }

    private void excluirAgendamento(Agendamento agendamento) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir");
        alert.setHeaderText("Deseja excluir o agendamento?");
        alert.setContentText(
                agendamento.getUsuario().getNome()
                        + " – " + agendamento.getSala().getNome()
                        + " em " + agendamento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                agendamentoDao.excluir(agendamento.getId());
                AlertUtil.sucesso("Agendamento excluído com sucesso.");
                carregarInformacoes();
            }
        });
    }

    private void editarAgendamento(Agendamento agendamento) {
        try {
            if (agendamentoStage != null && agendamentoStage.isShowing()) {
                agendamentoStage.toFront();
                agendamentoStage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CadastroAgendamento.fxml"));
            Scene scene = new Scene(loader.load());

            AgendamentoController controller = loader.getController();
            controller.carregarAgendamento(agendamento);

            agendamentoStage = new Stage();
            agendamentoStage.setScene(scene);
            agendamentoStage.setTitle("Editar Agendamento");
            agendamentoStage.setResizable(false);
            agendamentoStage.initModality(Modality.APPLICATION_MODAL);
            agendamentoStage.initOwner(listaAgendamentos.getScene().getWindow());
            agendamentoStage.setOnHidden(e -> {
                carregarInformacoes();
                agendamentoStage = null;
            });
            agendamentoStage.showAndWait();

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
            cadastroAgendamentoStage.initOwner(listaAgendamentos.getScene().getWindow());
            cadastroAgendamentoStage.setOnHidden(event -> {
                carregarInformacoes();
                cadastroAgendamentoStage = null;
            });
            cadastroAgendamentoStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
