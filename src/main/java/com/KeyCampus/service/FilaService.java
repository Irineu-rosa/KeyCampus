package com.KeyCampus.service;

import com.KeyCampus.dao.FilaDao;

public class FilaService {

    private final FilaDao filaDao =
            new FilaDao();

    public void adicionarFila(
            Long usuarioId,
            Long salaId
    ){
        Long proximo = filaDao.proximoUsuario(salaId);

        if(proximo != null){
            System.out.println( "Já existe fila. Entrando na sequência...");
        }

        filaDao.entrarFila(usuarioId, salaId);

    }

}