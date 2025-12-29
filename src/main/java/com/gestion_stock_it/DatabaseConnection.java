package com.gestion_stock_it;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
    private static final String URL = System.getenv("BD_URL");
    private static final String USER = System.getenv("BD_USER");
    private static final String PASSWORD = System.getenv("BD_PASSWORD");

    public static final DatabaseConnection INSTANCE = new DatabaseConnection();
    
    private DatabaseConnection() {
	}
    
    public Connection getConnection() throws SQLException {
    	 try {
	        Class.forName("org.postgresql.Driver"); // charge explicitement le driver
    	 } catch (ClassNotFoundException e) {
	        throw new RuntimeException("Driver PostgreSQL non trouvé", e);
    	 }
    	 return DriverManager.getConnection(
                 URL,
                 USER,
                 PASSWORD
         );
    }
    
    public static DatabaseConnection getInstance() {
		return INSTANCE;
	}
   
}
