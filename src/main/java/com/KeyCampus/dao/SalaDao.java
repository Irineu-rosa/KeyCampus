package com.KeyCampus.dao;

import com.KeyCampus.database.ConnectionFactory;
import com.KeyCampus.model.Sala;
import com.KeyCampus.model.StatusSala;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class SalaDao {

    public void salvar(Sala sala){
        String sql =
                """
                INSERT INTO salas 
                    ( nome, descricao, status )
                VALUES
                    (?,?,?)
                """;

        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)
        ){
            stmt.setString(1,sala.getNome());
            stmt.setString(2, sala.getDescricao());
            stmt.setString(3, sala.getStatus().name());

            stmt.execute();
            System.out.println("Sala salva!");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int total(){
        String sql = "SELECT COUNT(*) total FROM salas";
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            var rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("total");
            }
        }

        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public int totalDisponiveis(){
        String sql=
                """
                SELECT COUNT(*) total
                FROM salas
                WHERE status='DISPONIVEL'
                """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){

            var rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("total");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<Sala> listarObjetos(){
        List<Sala> salas = new ArrayList<>();

        String sql = "SELECT * FROM salas";

        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            var rs = stmt.executeQuery();


            while(rs.next()){
                Sala sala = new Sala();
                sala.setId(rs.getLong("id"));
                sala.setNome(rs.getString("nome"));
                sala.setDescricao(rs.getString("descricao"));
                sala.setStatus(StatusSala.valueOf(rs.getString("status")));
                salas.add(sala);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return salas;
    }

    public void excluir(
            Long id
    ){
        String sql = "DELETE FROM salas WHERE id=?";
        try(
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1,id);
            stmt.executeUpdate();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void atualizar(
            Sala sala
    ){
        String sql =
            """
            UPDATE salas
            SET nome=?,
                descricao=?,
                status=?
            WHERE id=?
            """;

        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setString(1, sala.getNome());
            stmt.setString(2, sala.getDescricao());
            stmt.setString(3, sala.getStatus().name());
            stmt.setLong(4, sala.getId());
            stmt.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public int totalSalaEmUso() {
        String sql =
                """
                SELECT COUNT(*) total 
                FROM salas
                WHERE STATUS = "EM_USO"                                           
                """;
        try(
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        }catch(Exception e){
                e.printStackTrace();
        }

        return 0;
    }
}