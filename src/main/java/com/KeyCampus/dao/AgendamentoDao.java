package com.KeyCampus.dao;

import com.KeyCampus.database.ConnectionFactory;
import com.KeyCampus.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AgendamentoDao {

    private void prepararParams(PreparedStatement stmt, Object... params) throws Exception {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    private <T> List<T> executarLeitura(String sql, Function<ResultSet, T> mapper, Object... params) {
        List<T> resultado = new ArrayList<>();
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            prepararParams(stmt, params);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultado.add(mapper.apply(rs));
            }
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            e.printStackTrace();
        }
        return resultado;
    }

    private void executarEscrita(String sql, Object... params) {
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            prepararParams(stmt, params);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao executar operação no banco: " + e.getMessage(), e);
        }
    }

    private Agendamento mapearAgendamento(ResultSet rs) {
        try {
            Agendamento agendamento = new Agendamento();
            agendamento.setId(rs.getLong("id"));
            agendamento.setData(LocalDate.parse(rs.getString("data")));
            agendamento.setHoraInicio(LocalTime.parse(rs.getString("hora_inicio")));
            agendamento.setHoraFim(LocalTime.parse(rs.getString("hora_fim")));

            Usuario usuario = new Usuario();
            usuario.setId(rs.getLong("usuario_id"));
            usuario.setNome(rs.getString("usuario_nome"));
            agendamento.setUsuario(usuario);

            Sala sala = new Sala();
            sala.setId(rs.getLong("sala_id"));
            sala.setNome(rs.getString("sala_nome"));
            agendamento.setSala(sala);

            return agendamento;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mapear agendamento: " + e.getMessage(), e);
        }
    }

    private Sala mapearSala(ResultSet rs) {
        try {
            Sala sala = new Sala();
            sala.setId(rs.getLong("id"));
            sala.setNome(rs.getString("nome"));
            return sala;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mapear sala: " + e.getMessage(), e);
        }
    }

    private String[] janelaDeTempoAtual() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime agora = LocalDateTime.now();
        return new String[]{
                agora.format(fmt),
                agora.plusMinutes(10).format(fmt)
        };
    }

    // SQL base
    private static final String SQL_SELECT_AGENDAMENTOS = """
            SELECT a.*,
                   u.nome usuario_nome,
                   s.nome sala_nome
            FROM agendamentos a
            LEFT JOIN usuarios u ON u.id = a.usuario_id
            LEFT JOIN salas  s ON s.id = a.sala_id
            """;

    public List<Agendamento> listarObjetos() {
        return executarLeitura(SQL_SELECT_AGENDAMENTOS, this::mapearAgendamento);
    }

    public List<Agendamento> buscarPorUsuario(Long id) {
        return executarLeitura(SQL_SELECT_AGENDAMENTOS + "WHERE a.usuario_id = ?", this::mapearAgendamento, id);
    }

    public List<Agendamento> buscarPrimeiroIdUsuario(Long id){
        return  executarLeitura(SQL_SELECT_AGENDAMENTOS + " ORDER BY CASE WHEN a.id = ? THEN 0 ELSE 1 END, a.id DESC;", this::mapearAgendamento, id);
    }

    public int totalAgendamentosHoje() {
        String sql = "SELECT COUNT(*) total FROM agendamentos WHERE data = CURRENT_DATE";
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void salvar(Agendamento agendamento) {
        executarEscrita("""
                INSERT INTO agendamentos (usuario_id, sala_id, data, hora_inicio, hora_fim)
                VALUES (?, ?, ?, ?, ?)
                """,
                agendamento.getUsuario().getId(),
                agendamento.getSala().getId(),
                agendamento.getData().toString(),
                agendamento.getHoraInicio().toString(),
                agendamento.getHoraFim().toString()
        );
        System.out.println("Agendamento salvo!");
    }

    public void atualizar(Agendamento agendamento) {
        executarEscrita("""
                UPDATE agendamentos
                SET usuario_id = ?, sala_id = ?, data = ?, hora_inicio = ?, hora_fim = ?
                WHERE id = ?
                """,
                agendamento.getUsuario().getId(),
                agendamento.getSala().getId(),
                agendamento.getData().toString(),
                agendamento.getHoraInicio().toString(),
                agendamento.getHoraFim().toString(),
                agendamento.getId()
        );
        System.out.println("Agendamento atualizado!");
    }

    public void excluir(Long id) {
        executarEscrita("DELETE FROM agendamentos WHERE id = ?", id);
    }

    public List<Sala> salasDisponiveisParaPalestrante(Long usuarioId) {
        String[] janela = janelaDeTempoAtual();
        return executarLeitura("""
                SELECT s.id, s.nome, s.status
                FROM agendamentos a
                JOIN salas s ON s.id = a.sala_id
                WHERE a.usuario_id = ?
                  AND a.hora_inicio <= ?
                  AND a.hora_fim > ?
                """,
                this::mapearSala,
                usuarioId, janela[1], janela[0]
        );
    }

    public boolean usuarioPossuiAgendamentoAtivo(Long usuarioId, Long salaId) {
        String[] janela = janelaDeTempoAtual();
        String sql = """
                SELECT 1 FROM agendamentos
                WHERE usuario_id = ?
                  AND sala_id = ?
                  AND hora_inicio <= ?
                  AND hora_fim > ?
                """;
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            prepararParams(stmt, usuarioId, salaId, janela[1], janela[0]);
            return stmt.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}