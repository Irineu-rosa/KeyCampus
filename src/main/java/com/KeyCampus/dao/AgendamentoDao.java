package com.KeyCampus.dao;

import com.KeyCampus.database.ConnectionFactory;
import com.KeyCampus.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDao {

    public List<Agendamento> listarObjetos() {
        List<Agendamento> agendamentos = new ArrayList<>();

        String sql =
                """
                SELECT a.*,
                       u.nome usuario_nome,
                       s.nome sala_nome
                FROM agendamentos a
                LEFT JOIN usuarios u ON u.id = a.usuario_id
                LEFT JOIN salas    s ON s.id = a.sala_id
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            var rs = stmt.executeQuery();

            while (rs.next()) {
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

                agendamentos.add(agendamento);
            }

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            e.printStackTrace();
        }

        return agendamentos;
    }

    public Agendamento buscarPorId(Long id) {
        String sql =
                """
                SELECT a.*,
                       u.nome usuario_nome,
                       s.nome sala_nome
                FROM agendamentos a
                LEFT JOIN usuarios u ON u.id = a.usuario_id
                LEFT JOIN salas    s ON s.id = a.sala_id
                WHERE a.id = ?
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
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
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public int totalAgendamentosHoje() {
        String sql =
                """
                SELECT COUNT(*) total
                FROM agendamentos
                WHERE data = CURRENT_DATE
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void salvar(Agendamento agendamento) {
        String sql =
                """
                INSERT INTO agendamentos (usuario_id, sala_id, data, hora_inicio, hora_fim)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong  (1, agendamento.getUsuario().getId());
            stmt.setLong  (2, agendamento.getSala().getId());
            stmt.setString(3, agendamento.getData().toString());        // "yyyy-MM-dd"
            stmt.setString(4, agendamento.getHoraInicio().toString());  // "HH:mm:ss"
            stmt.setString(5, agendamento.getHoraFim().toString());     // "HH:mm:ss"

            stmt.execute();
            System.out.println("Agendamento salvo!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Agendamento agendamento) {
        String sql =
                """
                UPDATE agendamentos
                SET usuario_id = ?, sala_id = ?, data = ?, hora_inicio = ?, hora_fim = ?
                WHERE id = ?;
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong  (1, agendamento.getUsuario().getId());
            stmt.setLong  (2, agendamento.getSala().getId());
            stmt.setString(3, agendamento.getData().toString());
            stmt.setString(4, agendamento.getHoraInicio().toString());
            stmt.setString(5, agendamento.getHoraFim().toString());
            stmt.setLong  (6, agendamento.getId());

            stmt.execute();
            System.out.println("Agendamento atualizado!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM agendamentos WHERE id = ?";

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
