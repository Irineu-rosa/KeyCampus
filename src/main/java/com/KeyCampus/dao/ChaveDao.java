package com.KeyCampus.dao;

import com.KeyCampus.database.ConnectionFactory;
import com.KeyCampus.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class ChaveDao {

    public void alterarStatus(
            Long chaveId,
            String status
    ){
        String sql =
        """
        UPDATE chaves
        SET status=?
        WHERE id=?
        """;

        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setString(1, status);
            stmt.setLong(2, chaveId);
            stmt.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<Chave> listarObjetos() {
        List<Chave> chaves = new ArrayList<>();

        String sql =
                """
                SELECT c.*, s.nome sala_nome  FROM chaves c
                LEFT JOIN salas s ON s.id = c.sala_id 
                """;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            var rs = stmt.executeQuery();

            while (rs.next()) {
                Chave chave = mapear(rs);
                chave.getSala().setNome(rs.getString("sala_nome"));

                chaves.add(chave);
            }

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            e.printStackTrace();
        }

        return chaves;
    }

    public Chave buscarPorId(
            Long id
    ){
        String sql =
                """
                SELECT *
                FROM chaves
                WHERE id=?
                """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) return mapear(rs);
        }
        catch(Exception e){
        throw new RuntimeException(e);
        }
            return null;
        }

    public int totalchavesAtiva(){
        String sql =
                """
                SELECT COUNT(*) total
                FROM chaves
                WHERE ativa = 1
                """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
                )
        {
            var rs = stmt.executeQuery();

            if (rs.next()){
                return rs.getInt("total");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void excluir(Long id){
        String sql = "DELETE FROM chaves WHERE id = ?";

        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ){
            stmt.setLong(1,id);
            stmt.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void salvar(Chave chave) {
        String sql =
                """
                        INSERT INTO chaves (numero, tipo, sala_id, status, ativa)
                        VALUES (?, ?, ?, ?, ?);
                """;
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setString(1, chave.getNumero());
            stmt.setString(2, chave.getTipo().name());
            stmt.setLong  (3, chave.getSala().getId());
            stmt.setString(4, chave.getStatus().name());
            stmt.setBoolean(5, chave.getAtiva());

            stmt.execute();
            System.out.println("Chave salva!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Chave chave) {
        String sql =
                """
                UPDATE chaves
                SET numero = ?, tipo = ?, sala_id = ?, status = ?, ativa = ?
                WHERE id = ?;
                """;
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setString (1, chave.getNumero());
            stmt.setString (2, chave.getTipo().name());
            stmt.setLong   (3, chave.getSala().getId());
            stmt.setString (4, chave.getStatus().name());
            stmt.setBoolean(5, chave.getAtiva());
            stmt.setLong   (6, chave.getId());

            stmt.execute();
            System.out.println("Chave atualizada!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Chave> listarDisponiveisPorTipo(TipoChave tipo) {
        String sql = """
                SELECT * FROM chaves
                WHERE status = 'DISPONIVEL'
                AND tipo   = ?
            """;

        List<Chave> lista = new ArrayList<>();
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, tipo.name());
            var rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Chave> listarDisponiveis() {
        String sql = "SELECT * FROM chaves WHERE status = 'DISPONIVEL'";
        List<Chave> lista = new ArrayList<>();
        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Chave mapear(java.sql.ResultSet rs) throws java.sql.SQLException {
        Chave chave = new Chave();
        chave.setId(rs.getLong("id"));
        chave.setNumero(rs.getString("numero"));
        chave.setTipo(TipoChave.valueOf(rs.getString("tipo")));
        chave.setAtiva(rs.getBoolean("ativa"));
        chave.setStatus(StatusChave.valueOf(rs.getString("status")));

        Sala sala = new Sala();
        sala.setId(rs.getLong("sala_id"));
        chave.setSala(sala);

        return chave;
    }
}