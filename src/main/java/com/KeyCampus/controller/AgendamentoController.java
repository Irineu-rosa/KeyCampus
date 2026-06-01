package com.KeyCampus.controller;

import com.KeyCampus.dao.AgendamentoDao;
import com.KeyCampus.dao.SalaDao;
import com.KeyCampus.dao.UsuarioDao;
import com.KeyCampus.model.*;
import com.KeyCampus.session.SessaoUsuario;
import com.KeyCampus.util.AlertUtil;
import com.KeyCampus.util.MascaraUtil;
import com.KeyCampus.util.ValidadorUtil;
import com.KeyCampus.service.AgendamentoService;
import com.KeyCampus.service.FilaService;

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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class AgendamentoController {

    @FXML private VBox listaAgendamentos;
    @FXML private Label lblTitleNovoAgendamento;
    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private ComboBox<Sala> cbSala;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFim;

    private final AgendamentoDao agendamentoDao = new AgendamentoDao();
    private final AgendamentoService agendamentoService = new AgendamentoService();
    private final FilaService filaService = new FilaService();
    private Agendamento agendamentoEdicao;

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        Usuario usuario = SessaoUsuario.get();
        if (txtHoraFim != null) {
            MascaraUtil.hora(txtHoraInicio);
            MascaraUtil.hora(txtHoraFim);
        }
        if (listaAgendamentos != null) {
            carregarTabela();
        } else {
            if (cbUsuario != null) cbUsuario.getItems().addAll(new UsuarioDao().listar());
            if (cbSala != null) cbSala.getItems().addAll(new SalaDao().listarObjetos());
            if (dpData != null) dpData.setValue(LocalDate.now());
        }
        prepararInterface(usuario);
    }

    private void prepararInterface(Usuario usuario) {
        if (cbUsuario == null || usuario == null) return;
        if (usuario.getTipo() == TipoUsuario.PALESTRANTE) {
            cbUsuario.setValue(usuario);
            cbUsuario.setDisable(true);
        }
    }

    public void carregarTabela() {
        listaAgendamentos.getChildren().clear();
        Usuario usuario = SessaoUsuario.get();

        var agendamentos = usuario.getTipo() == TipoUsuario.ADMIN
                ? agendamentoDao.listarObjetos()
                : agendamentoDao.buscarPrimeiroIdUsuario(usuario.getId());

        for (Agendamento agendamento : agendamentos) {
            listaAgendamentos.getChildren().add(criarLinhaAgendamento(agendamento));
        }
    }

    private HBox criarLinhaAgendamento(Agendamento agendamento) {
        HBox linha = new HBox(0);
        linha.getStyleClass().add("room-row");
        linha.setAlignment(Pos.CENTER_LEFT);

        String nomeUsuario = agendamento.getUsuario() != null ? agendamento.getUsuario().getNome() : "Sem usuário";
        String nomeSala = agendamento.getSala() != null ? agendamento.getSala().getNome() : "Sem sala";
        String data = agendamento.getData() != null ? agendamento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Sem data";
        String horario = (agendamento.getHoraInicio() != null ? agendamento.getHoraInicio().format(HORA_FORMATTER) : "--:--")
                + " – "
                + (agendamento.getHoraFim() != null ? agendamento.getHoraFim().format(HORA_FORMATTER) : "--:--");

        Label lUsuario = new Label(nomeUsuario);
        Label lSala = new Label(nomeSala);
        Label lData = new Label(data);
        Label lHorario = new Label(horario);

        for (Label l : new Label[]{lUsuario, lSala, lData, lHorario}) {
            l.setStyle("-fx-text-fill: #0F172A; -fx-font-size: 13px;");
            HBox.setHgrow(l, Priority.ALWAYS);
            l.setMaxWidth(Double.MAX_VALUE);
        }

        Usuario usuarioLogado = SessaoUsuario.get();
        boolean dono = usuarioLogado.getTipo() == TipoUsuario.ADMIN
                || (agendamento.getUsuario() != null
                && agendamento.getUsuario().getId().equals(usuarioLogado.getId()));

        HBox acoes = new HBox(8);
        acoes.setAlignment(Pos.CENTER_RIGHT);

        if (dono) {
            Button btnEditar = new Button("✏");
            Button btnExcluir = new Button("🗑");
            btnEditar.getStyleClass().add("btn-edit");
            btnExcluir.getStyleClass().add("btn-delete");
            btnEditar.setOnAction(e -> editarAgendamento(agendamento));
            btnExcluir.setOnAction(e -> excluirAgendamento(agendamento));
            acoes.getChildren().addAll(btnEditar, btnExcluir);
        }

        linha.getChildren().addAll(lUsuario, lSala, lData, lHorario, acoes);
        return linha;
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

    @FXML
    public void salvar() {
        boolean valido = ValidadorUtil.de()
                .combo(cbUsuario.getValue(), "Selecione o usuário")
                .combo(cbSala.getValue(), "Selecione a sala")
                .campo(dpData.getValue() != null ? dpData.getValue().toString() : "", "Selecione a data")
                .campo(txtHoraInicio.getText(), "Digite a hora de início (HH:mm)")
                .campo(txtHoraFim.getText(), "Digite a hora de fim (HH:mm)")
                .validar();

        if (!valido) return;

        LocalTime horaInicio;
        LocalTime horaFim;
        try {
            horaInicio = LocalTime.parse(txtHoraInicio.getText().trim(), HORA_FORMATTER);
            horaFim = LocalTime.parse(txtHoraFim.getText().trim(), HORA_FORMATTER);
        } catch (Exception ex) {
            AlertUtil.atencao("Formato de hora inválido. Use HH:mm.");
            return;
        }

        if (!horaFim.isAfter(horaInicio)) {
            AlertUtil.atencao("A hora de fim deve ser posterior à hora de início.");
            return;
        }

        Agendamento agendamento = agendamentoEdicao != null ? agendamentoEdicao : new Agendamento();
        agendamento.setUsuario(cbUsuario.getValue());
        agendamento.setSala(cbSala.getValue());
        agendamento.setData(dpData.getValue());
        agendamento.setHoraInicio(horaInicio);
        agendamento.setHoraFim(horaFim);

        Long usuarioId = cbUsuario.getValue().getId();
        Long salaId = cbSala.getValue().getId();
        String dataStr = dpData.getValue().toString();
        String inicioStr = horaInicio.format(HORA_FORMATTER);
        String fimStr = horaFim.format(HORA_FORMATTER);

        if (!agendamentoService.podeAgendar(usuarioId, salaId, dataStr, inicioStr, fimStr)) {
            AlertUtil.atencao(
                    "Não foi possível agendar:\n" +
                            "• O usuário já possui um agendamento nesse horário, ou\n" +
                            "• A sala já está ocupada nesse período."
            );
            return;
        }

        if (agendamento.getId() == null) {
            agendamentoDao.salvar(agendamento);
            filaService.adicionarFila(usuarioId, salaId);
            AlertUtil.sucesso("Agendamento realizado com sucesso.");
        } else {
            agendamentoDao.atualizar(agendamento);
            AlertUtil.sucesso("Agendamento atualizado com sucesso.");
        }

        ((Stage) cbUsuario.getScene().getWindow()).close();
    }

    @FXML
    public void abrirCadastroAgendamento() {
        abrirModal("Novo Agendamento", null);
    }

    private void editarAgendamento(Agendamento agendamento) {
        abrirModal("Editar Agendamento", ctrl -> ctrl.carregarAgendamento(agendamento));
    }

    private void excluirAgendamento(Agendamento agendamento) {
        String descricao = agendamento.getUsuario().getNome()
                + " – " + agendamento.getSala().getNome()
                + " em " + agendamento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        confirmar("Deseja excluir o agendamento?", descricao, () -> {
            agendamentoDao.excluir(agendamento.getId());
            AlertUtil.sucesso("Agendamento excluído com sucesso.");
            carregarTabela();
        });
    }

    private void abrirModal(String titulo, Consumer<AgendamentoController> configurar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CadastroAgendamento.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(listaAgendamentos.getScene().getWindow());
            stage.setOnHidden(e -> carregarTabela());
            if (configurar != null) configurar.accept(loader.getController());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmar(String header, String conteudo, Runnable onConfirmar) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir");
        alert.setHeaderText(header);
        alert.setContentText(conteudo);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) onConfirmar.run();
        });
    }
}