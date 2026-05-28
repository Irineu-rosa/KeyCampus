package com.KeyCampus.controller;

import com.KeyCampus.dao.UsuarioDao;
import com.KeyCampus.model.TipoUsuario;
import com.KeyCampus.model.Usuario;
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

public class UsuarioController {

    @FXML
    private VBox listaUsuarios;

    @FXML
    private Label lblTitleNovoUsuario;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtMatricula;
    @FXML
    private ComboBox<TipoUsuario> cbTipo;
    @FXML
    private CheckBox chkAtivo;

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private Stage cadastroUsuarioStage;
    private Stage usuarioStage;
    private Usuario usuarioEdicao;

    @FXML
    public void initialize() {
        if (listaUsuarios != null) {
            carregarInformacoes();
        } else {
            if (cbTipo != null) cbTipo.getItems().addAll(TipoUsuario.values());
        }
    }

    private void carregarInformacoes() {
        carregarTabela();
    }

    public void carregarTabela() {
        listaUsuarios.getChildren().clear();

        for (Usuario usuario : usuarioDao.listar()) {
            listaUsuarios.getChildren().add(criarLinhaUsuario(usuario));
        }
    }

    private HBox criarLinhaUsuario(Usuario usuario) {
        HBox linha = new HBox(0);
        linha.getStyleClass().add("room-row");
        linha.setAlignment(Pos.CENTER_LEFT);

        String nome = (usuario.getNome() != null && !usuario.getNome().isBlank()) ? usuario.getNome() : "Sem nome";
        String matricula = (usuario.getMatricula() != null && !usuario.getMatricula().isBlank()) ? usuario.getMatricula() : "Sem matrícula";
        String tipo = (usuario.getTipo() != null) ? usuario.getTipo().toString() : "Sem tipo";
        String ativoStr = usuario.isAtivo() ? "Ativo" : "Inativo";

        Label lNome = new Label(nome);
        Label lMatricula = new Label(matricula);
        Label lTipo = new Label(tipo);

        for (Label l : new Label[]{lNome, lMatricula, lTipo}) {
            l.setStyle("-fx-text-fill: #0F172A; -fx-font-size: 13px;");
            HBox.setHgrow(l, Priority.ALWAYS);
            l.setMaxWidth(Double.MAX_VALUE);
        }

        Label lStatus = new Label(ativoStr);
        lStatus.getStyleClass().add("room-tag");
        lStatus.getStyleClass().add(usuario.isAtivo() ? "tag-free" : "tag-busy");
        HBox.setHgrow(lStatus, Priority.NEVER);

        Button btnEditar  = new Button("✏");
        Button btnExcluir = new Button("🗑");
        btnEditar.getStyleClass().add("btn-edit");
        btnExcluir.getStyleClass().add("btn-delete");

        btnEditar.setOnAction(e -> editarUsuario(usuario));
        btnExcluir.setOnAction(e -> excluirUsuario(usuario));

        HBox acoes = new HBox(8, btnEditar, btnExcluir);
        acoes.setAlignment(Pos.CENTER_LEFT);

        linha.getChildren().addAll(lNome, lMatricula, lTipo, lStatus, acoes);
        return linha;
    }


    public void carregarUsuario(Usuario usuario) {
        this.usuarioEdicao = usuario;
        if (usuarioEdicao.getId() != null) {
            lblTitleNovoUsuario.setText("Editar Usuário");
        }
        txtNome.setText(usuario.getNome());
        txtMatricula.setText(usuario.getMatricula());
        cbTipo.setValue(usuario.getTipo());
        System.out.println(usuario.getTipo());
        chkAtivo.setSelected(usuario.isAtivo());
    }

    @FXML
    public void salvar() {
        boolean valido = ValidadorUtil.de()
                .campo(txtNome.getText(),      "Digite o nome do usuário")
                .campo(txtMatricula.getText(),  "Digite a matrícula")
                .combo(cbTipo.getValue(),       "Selecione o tipo de usuário")
                .validar();

        if (!valido) return;

        Usuario usuario = (usuarioEdicao != null) ? usuarioEdicao : new Usuario();
        usuario.setNome(txtNome.getText());
        usuario.setMatricula(txtMatricula.getText());
        usuario.setTipo(cbTipo.getValue());
        usuario.setAtivo(chkAtivo.isSelected());

        if (usuario.getId() == null) {
            usuarioDao.salvar(usuario);
        } else {
            usuarioDao.atualizar(usuario);
        }

        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private void excluirUsuario(Usuario usuario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir");
        alert.setHeaderText("Deseja excluir?");
        alert.setContentText(usuario.getNome() + " - " + usuario.getMatricula());
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                usuarioDao.excluir(usuario.getId());
                carregarInformacoes();
            }
        });
    }

    private void editarUsuario(Usuario usuario) {
        try {
            if (usuarioStage != null && usuarioStage.isShowing()) {
                usuarioStage.toFront();
                usuarioStage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader( getClass().getResource("/views/CadastroUsuario.fxml")
            );
            Scene scene = new Scene(loader.load());

            UsuarioController controller = loader.getController();
            controller.carregarUsuario(usuario);

            usuarioStage = new Stage();
            usuarioStage.setScene(scene);
            usuarioStage.setTitle("Editar Usuário");
            usuarioStage.setResizable(false);
            usuarioStage.initModality(Modality.APPLICATION_MODAL);
            usuarioStage.initOwner(listaUsuarios.getScene().getWindow());
            usuarioStage.setOnHidden(e -> {
                carregarInformacoes();
                usuarioStage = null;
            });
            usuarioStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirCadastroUsuario() {
        try {
            if (cadastroUsuarioStage != null && cadastroUsuarioStage.isShowing()) {
                cadastroUsuarioStage.toFront();
                cadastroUsuarioStage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/CadastroUsuario.fxml")
            );
            cadastroUsuarioStage = new Stage();
            cadastroUsuarioStage.setScene(new Scene(loader.load()));
            cadastroUsuarioStage.setTitle("Novo Usuário");
            cadastroUsuarioStage.setResizable(false);
            cadastroUsuarioStage.initModality(Modality.APPLICATION_MODAL);
            cadastroUsuarioStage.initOwner(listaUsuarios.getScene().getWindow());
            cadastroUsuarioStage.setOnHidden(event -> {
                carregarInformacoes();
                cadastroUsuarioStage = null;
            });
            cadastroUsuarioStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
