package com.KeyCampus.dao;

import com.KeyCampus.database.ConnectionFactory;
import com.KeyCampus.model.TipoUsuario;
import com.KeyCampus.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    // ------------------------------------------------------------------ salvar

    public void salvar(Usuario usuario) {

        String sql =
                """
                INSERT INTO usuarios
                (nome, matricula, senha_hash, tipo, ativo, primeiro_acesso)
                VALUES
                (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getMatricula());
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setString(4, usuario.getTipo().name());
            stmt.setBoolean(5, usuario.isAtivo());
            stmt.setBoolean(6, usuario.isPrimeiroAcesso());

            stmt.execute();

            System.out.println("Usuário salvo!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------ buscas

    public Usuario buscarPorMatricula(String matricula) {

        String sql =
                """
                SELECT *
                FROM usuarios
                WHERE matricula = ?
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, matricula);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Usuario buscarPorId(Long id) {

        String sql =
                """
                SELECT *
                FROM usuarios
                WHERE id = ?
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Usuario> listar() {

        List<Usuario> usuarios = new ArrayList<>();

        String sql =
                """
                SELECT *
                FROM usuarios
                ORDER BY nome
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    public void atualizar(Usuario usuario) {

        String sql =
                """
                UPDATE usuarios
                SET
                    nome = ?,
                    matricula = ?,
                    tipo = ?,
                    ativo = ?,
                    senha_hash = ?,
                    primeiro_acesso = ?
                WHERE id = ?
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getMatricula());
            stmt.setString(3, usuario.getTipo().name());
            stmt.setBoolean(4, usuario.isAtivo());
            stmt.setString(5, usuario.getSenhaHash());
            stmt.setBoolean(6, usuario.isPrimeiroAcesso());
            stmt.setLong(7, usuario.getId());

            stmt.executeUpdate();

            System.out.println("Usuário atualizado!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizarSenha(Long usuarioId, String senhaHash) {

        String sql =
                """
                UPDATE usuarios
                SET
                    senha_hash = ?,
                    primeiro_acesso = 0
                WHERE id = ?
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, senhaHash);
            stmt.setLong(2, usuarioId);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetarSenha(Long usuarioId) {

        String sql =
                """
                UPDATE usuarios
                SET
                    senha_hash = NULL,
                    primeiro_acesso = 1
                WHERE id = ?
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, usuarioId);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {

        String sql =
                """
                DELETE FROM usuarios
                WHERE id = ?
                """;

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

    private Usuario mapear(ResultSet rs) throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setMatricula(rs.getString("matricula"));
        usuario.setSenhaHash(rs.getString("senha_hash"));
        usuario.setTipo(TipoUsuario.valueOf(rs.getString("tipo")));
        usuario.setAtivo(rs.getBoolean("ativo"));
        usuario.setPrimeiroAcesso(rs.getBoolean("primeiro_acesso"));

        return usuario;
    }
}