package com.gestion_stock_it.ChatBot;

import com.gestion_stock_it.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Exécute les requêtes SQL en utilisant une instance de Connection fournie par
 * la classe DatabaseConnection.
 * * NOTE : Cette classe suppose que la méthode DatabaseConnection.connect() a été
 * appelée avant d'utiliser cet executeur. La fermeture de la connexion
 * doit être gérée par l'appelant via DatabaseConnection.disconnect().
 */
public class DatabaseExecutor {

    private final DatabaseConnection dbConnection;
    public DatabaseExecutor(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }


    public List<Map<String, Object>> executeSelect(String sqlQuery) throws SQLException {

        List<Map<String, Object>> results = new ArrayList<>();

        // Récupère la connexion existante (ouverte par l'appelant)
        Connection connection = dbConnection.getConnection();

        if (connection == null || connection.isClosed()) {
            throw new SQLException("La connexion à la base de données n'est pas ouverte ou est fermée.");
        }

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = resultSet.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
        }
        return results;
    }
}
