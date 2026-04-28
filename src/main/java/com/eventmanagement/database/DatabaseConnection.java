package com.eventmanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections using singleton pattern with thread safety.
 * Provides connection pooling-ready architecture for production use.
 */
public class DatabaseConnection {
    // Database configuration - should be moved to properties file in production
    private static final String URL = "jdbc:mysql://localhost:3306/event_management_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; // TODO: Move to environment variable
    private static final String PASSWORD = "password"; // TODO: Move to environment variable
    
    // Thread-safe singleton using volatile and synchronized
    private static volatile Connection connection = null;
    
    // Private constructor prevents instantiation
    private DatabaseConnection() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Gets a database connection. Creates one if it doesn't exist or is closed.
     * Thread-safe implementation using double-checked locking.
     * 
     * @return Active database connection
     * @throws SQLException if connection cannot be established
     */
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                
                // Set connection properties for better performance
                connection.setAutoCommit(true);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Ensure mysql-connector-java is in classpath.", e);
        } catch (SQLException e) {
            throw new SQLException("Failed to establish database connection. Check URL, username, and password.", e);
        }
        return connection;
    }
    
    /**
     * Closes the current database connection if open.
     * Should be called during application shutdown.
     */
    public static synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Tests if the database connection is valid.
     * 
     * @return true if connection is active, false otherwise
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
