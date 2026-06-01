package com.KeyCampus.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {

        try (
                Connection conn = ConnectionFactory.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS usuarios(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    matricula TEXT UNIQUE,
                    tipo TEXT NOT NULL,
                    ativo INTEGER DEFAULT 1,
                    senha_hash TEXT DEFAULT NULL,
                    primeiro_acesso INTEGER DEFAULT 1
                    );""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS salas(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    descricao TEXT,
                    status TEXT
                    );""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS chaves(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    numero TEXT,
                    tipo TEXT,
                    ativa INTEGER DEFAULT 1,
                    status TEXT,
                    sala_id INTEGER,
                    FOREIGN KEY(sala_id)
                    REFERENCES salas(id)
                    );""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS agendamentos (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          usuario_id INTEGER NOT NULL,
                          sala_id INTEGER NOT NULL,
                          data TEXT NOT NULL,   
                          hora_inicio TEXT NOT NULL,   
                          hora_fim TEXT NOT NULL,  
                          status TEXT NULL,                           
                          FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
                          FOREIGN KEY (sala_id) REFERENCES salas(id)
                        );""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS retiradas(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    usuario_id INTEGER NOT NULL,
                    chave_id INTEGER NOT NULL,
                    retirada_em TEXT NOT NULL,
                    devolucao_em TEXT,      
                    status TEXT NOT NULL,              
                    FOREIGN KEY(usuario_id)
                    REFERENCES usuarios(id),
                    FOREIGN KEY(chave_id)
                    REFERENCES chaves(id)
                    );""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS fila(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    usuario_id INTEGER,
                    sala_id INTEGER,
                    data_hora TEXT,
                    FOREIGN KEY(usuario_id)
                    REFERENCES usuarios(id),
                    FOREIGN KEY(sala_id)
                    REFERENCES salas(id)
                    );""");

            System.out.println(
                    "Banco criado com sucesso!"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}