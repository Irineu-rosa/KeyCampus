package com.KeyCampus.service;

import com.KeyCampus.database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AgendamentoService {

    public boolean possuiConflito(
            Long usuarioId,
            String data,
            String horaInicio,
            String horaFim
    ){

        String sql =
                """
                SELECT *
                FROM agendamentos
                WHERE usuario_id=?
                AND data=?
                AND(
                (? < hora_fim)
                AND
                (? > hora_inicio)
                )
                """;
        try(
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1, usuarioId);

            stmt.setString(2, data);

            stmt.setString(3, horaInicio);

            stmt.setString(4, horaFim);
            var rs = stmt.executeQuery();
            return rs.next();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean salaPossuiConflito(
        Long salaId,
        String data,
        String horaInicio,
        String horaFim
    ){
        String sql="""
            SELECT *
            FROM agendamentos
            WHERE sala_id=?
            AND data=?
            AND((? < hora_fim) AND (? > hora_inicio))
            """;

        try(
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setLong(1, salaId);

            stmt.setString(2, data);

            stmt.setString(3, horaInicio);

            stmt.setString(4, horaFim);

            var rs = stmt.executeQuery();
            return rs.next();
        }

        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean podeAgendar(
        Long usuarioId,
        Long salaId,
        String data,
        String inicio,
        String fim
    ){
        if(
            possuiConflito(
                    usuarioId,
                    data,
                    inicio,
                    fim )
        ){
            System.out.println(
                    "Usuário possui outro agendamento nesse horário"
            );
            return false;
        }
        if(
            salaPossuiConflito(
                    salaId,
                    data,
                    inicio,
                    fim)
        ){
            System.out.println("Sala ocupada nesse horário");
            return false;
        }
        return true;
    }
}