package com.KeyCampus.dao;

import com.KeyCampus.database.ConnectionFactory;
import com.KeyCampus.model.TipoUsuario;
import com.KeyCampus.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao{
    public void salvar(
            Usuario usuario
    ){
        String sql =
                """
                INSERT INTO usuarios
                ( nome, matricula, tipo, ativo )                
                    VALUES
                (?,?,?,?)
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt = conn.prepareStatement(sql);
                ){
                stmt.setString(1, usuario.getNome());
                stmt.setString(2, usuario.getMatricula());
                stmt.setString(3, usuario.getTipo().name());
                stmt.setBoolean(4, usuario.isAtivo());

                stmt.execute();

                System.out.println("Usuário Salvo!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql =
                """
                    SELECT * FROM usuarios
                """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
                )
        {
            var rs = stmt.executeQuery();

            while (rs.next()){
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setMatricula(rs.getString("matricula"));
                usuario.setTipo(TipoUsuario.valueOf(rs.getString("tipo")));
                usuario.setAtivo(rs.getBoolean("ativo"));
                usuarios.add(usuario);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return usuarios;
    }

    public Usuario buscarPorId(
            Long id
    ){
        String sql = """
                SELECT * 
                FROM usuarios
                WHERE id=?
                """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1,id);
            var rs = stmt.executeQuery();
            if(rs.next()){
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setMatricula(rs.getString("matricula"));
                usuario.setTipo(TipoUsuario.valueOf(rs.getString("tipo")));
                return usuario;
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public void atualizar(Usuario usuario) {
        String sql =
                    """
                            UPDATE usuarios SET VALUES
                            nome = ?,
                            matricula = ?,
                            tipo = ?,
                            ativo = ?,
                            WHERE id = ?,
                    """;
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ){
            System.out.println("CHEGOU AQ");
            System.out.println(usuario.getId());
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getMatricula());
            stmt.setString(3, usuario.getTipo().toString());
            stmt.setBoolean(4, usuario.isAtivo());
            stmt.setLong(5, usuario.getId());

            stmt.execute();
            System.out.println("Chave alterada!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql =
                """
                DELETE FROM usuarios WHERE id = ? 
                """;
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ){
            stmt.setLong(1, id);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}