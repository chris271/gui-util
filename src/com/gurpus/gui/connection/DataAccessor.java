package com.gurpus.gui.connection;

import com.gurpus.gui.util.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataAccessor {

    private static Connection connection;
    private static String schema;
    public static final String LOCAL_TEST = "SCHEMA1";
    public static final String LOCAL_DEV = "SCHEMA2";
    public static final String PROD = "SCHEMA3";

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(String newSchema) {
        schema = newSchema;
    }

    private static void connect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Logger.severe("Could Not Close Old Connection");
                Logger.severe(e.toString());
            }
        }
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            Logger.severe("Where is your Oracle JDBC Driver?");
            Logger.severe(e.toString());
        }
        try {
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@<CONNECTION_URL>", schema, schema + "data");
        } catch (SQLException e) {
            Logger.severe("Connection Failed...");
            Logger.severe(e.toString());
        }
        if (connection != null) {
            Logger.info("Connection successful!");
        } else {
            Logger.severe("Failed To Make Connection To Database => " + "jdbc:oracle:thin:@<CONNECTION_URL>" + " Schema => " + schema);
        }

    }

    public static String getSchema() {
        return schema;
    }
}
