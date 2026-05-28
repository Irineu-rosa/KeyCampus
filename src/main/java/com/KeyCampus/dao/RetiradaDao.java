package com.KeyCampus.dao;

import com.KeyCampus.database.ConnectionFactory;
import com.KeyCampus.util.AlertUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class RetiradaDao {

    public void retirar(
            Long usuarioId,
            Long chaveId
    ){
        String sql=
        """
        INSERT INTO retiradas
        ( usuario_id, chave_id, retirada_em, status )
        VALUES
        (?, ?, ?, ?)
        """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1, usuarioId);

            stmt.setLong(2, chaveId);

            stmt.setString(3, LocalDateTime.now().toString());

            stmt.setString(4,"ATIVA");

            stmt.execute();

            new ChaveDao().alterarStatus(chaveId,"EM_USO");

            System.out.println("Chave retirada!");
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void devolver(
            Long usuarioId
    ){
        try(Connection conn = ConnectionFactory.getConnection()){
            String busca =
                    """
                    SELECT chave_id
                    FROM retiradas
                    WHERE usuario_id=?
                    AND status='ATIVA'
                    """;
            PreparedStatement ps = conn.prepareStatement(busca);
            ps.setLong(1, usuarioId);
            var rs = ps.executeQuery();
            Long chaveId = null;
            if(rs.next()){
                chaveId = rs.getLong("chave_id");
            }

            if(chaveId == null){
                System.out.println("Nenhuma chave ativa");
                return;
            }

            String sql =
                    """
                    UPDATE retiradas
                    SET
                    devolucao_em=?,
                    status=?
                    WHERE usuario_id=?
                    AND status='ATIVA'
                    """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, java.time.LocalDateTime.now().toString());
            stmt.setString(2, "DEVOLVIDA");
            stmt.setLong(3, usuarioId);

            int linhas = stmt.executeUpdate();

            if(linhas>0){
                AlertUtil.sucesso("Chave devolvida!");
                new ChaveDao().alterarStatus(chaveId, "DISPONIVEL");
                FilaDao filaDao = new FilaDao();
                Long proximo = filaDao.proximoUsuario(chaveId);

                if(proximo != null){
                    new RetiradaDao().retirarAutomatico(proximo, chaveId);
                    filaDao.removerUsuario(proximo);
                    System.out.println("Chave liberada automaticamente para usuário: " + proximo);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void retirarAutomatico(
            Long usuarioId,
            Long chaveId
    ){
        String sql =
                """
                INSERT INTO retiradas(
                usuario_id,
                chave_id,
                retirada_em,
                status
                )
                VALUES(
                ?,?,?,?
                )
                """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1, usuarioId);
            stmt.setLong(2, chaveId);
            stmt.setString(3, java.time.LocalDateTime.now().toString());
            stmt.setString(4, "ATIVA");
            stmt.executeUpdate();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}