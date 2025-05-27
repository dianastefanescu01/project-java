package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    private Properties jdbcProps;
    private static final Logger logger = LogManager.getLogger(JdbcUtils.class);
    private Connection instance = null;

    public JdbcUtils(Properties props) {
        this.jdbcProps = props;
    }

    private Connection getNewConnection() {
        logger.traceEntry();

        String url = jdbcProps.getProperty("swimming.jdbc.url");
        logger.info("Trying to connect to database: {}", url);

        try {
            Connection con = DriverManager.getConnection(url);
            logger.info("Database connection established successfully.");
            return con;
        } catch (SQLException e) {
            logger.error("Error connecting to database: ", e);
            throw new RuntimeException("Database connection failed!", e);
        }
    }

    public Connection getConnection() {
        logger.traceEntry();
        try {
            if (instance == null || instance.isClosed()) {
                instance = getNewConnection();
            }
        } catch (SQLException e) {
            logger.error("Database error: ", e);
            throw new RuntimeException("Error getting database connection", e);
        }
        logger.traceExit(instance);
        return instance;
    }
}
