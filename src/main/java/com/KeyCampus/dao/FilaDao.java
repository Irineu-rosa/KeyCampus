package com.KeyCampus.dao;

import com.KeyCampus.database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class FilaDao {

    public void entrarFila(
            Long usuarioId,
            Long salaId
    ){
        String sql =
               """
               INSERT INTO fila
               (usuario_id, sala_id, data_hora)
               VALUES
               (?,?,?)
               """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){

            stmt.setLong(1, usuarioId);
            stmt.setLong(2, salaId);
            stmt.setString(3, LocalDateTime.now().toString());
            stmt.execute();
            System.out.println("Entrou na fila!");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Long proximoUsuario(
            Long salaId
    ){
        String sql =
               """
               SELECT usuario_id
               FROM fila
               WHERE sala_id=?
               ORDER BY data_hora
               LIMIT 1
               """;

        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1,salaId);
            var rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getLong("usuario_id");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void removerUsuario(
            Long usuarioId
    ){
        String sql =
                """
                DELETE FROM fila
                WHERE usuario_id=?
                """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1, usuarioId);
            stmt.executeUpdate();
        }

        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}