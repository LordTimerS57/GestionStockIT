package com.gestion_stock_it;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private Connection conn;
    private static final String URL = System.getenv("BD_URL");
    private static final String USER = System.getenv("BD_USER");
    private static final String PASSWORD = System.getenv("BD_PASSWORD");

    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );
            System.out.println("Connexion PostgreSQL ouverte.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connexion PostgreSQL fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
