package com.KeyCampus.service;

import com.KeyCampus.dao.ChaveDao;
import com.KeyCampus.dao.UsuarioDao;
import com.KeyCampus.database.ConnectionFactory;
import com.KeyCampus.model.Chave;
import com.KeyCampus.model.TipoChave;
import com.KeyCampus.model.TipoUsuario;
import com.KeyCampus.model.Usuario;
import com.KeyCampus.util.AlertUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RetiradaService {

    public boolean usuarioPossuiChave(
                Long usuarioId
    ){
        String sql = """
                SELECT * FROM retiradas
                where usuario_id = ?
                and devolucao_em IS NULL""";

        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setLong(1, usuarioId);
            var rs = stmt.executeQuery();

            return rs.next();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean podeRetirar(
            Long usuarioId,
            Long salaId,
            Long chaveId
    ){
        Usuario usuario = new UsuarioDao().buscarPorId(usuarioId);
        Chave chave = new ChaveDao().buscarPorId(chaveId);
        if(!tipoPodeRetirar(usuario, chave)
        ){
            System.out.println("Tipo de chave inválido");
            return false;
        }

        if(usuarioPossuiChave(usuarioId)){
            AlertUtil.erro("Você precisa devolver a chave atual");
            return false;
        }
        return true;
    }

    public boolean salaEmLimpeza(
            Long salaId
    ){
        String sql=
                """
                SELECT *
                FROM salas
                WHERE id=?
                AND status='EM_LIMPEZA'
                """;

        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1, salaId);
            var rs = stmt.executeQuery();
            return rs.next();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean tipoPodeRetirar(
            Usuario usuario,
            Chave chave
    ){
        if(usuario.getTipo() == TipoUsuario.ADMIN){
            return true;
        }

        if(usuario.getTipo() == TipoUsuario.LIMPEZA && chave.getTipo() == TipoChave.LIMPEZA){
            return true;
        }

        if(usuario.getTipo() == TipoUsuario.PALESTRANTE && chave.getTipo() == TipoChave.FUNCIONARIO
        ){
            return true;
        }
        return false;
    }


}