package com.KeyCampus.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {

    private static final String URL = "jdbc:sqlite:keycampus.db";

    public static Connection getConnection() {

        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);

        } catch (Exception e) {
            throw new RuntimeException("Erro conexão banco: "+ e.getMessage());
        }

    }

}