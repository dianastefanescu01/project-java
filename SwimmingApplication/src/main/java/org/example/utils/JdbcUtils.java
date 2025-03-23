package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class JdbcUtils {

   /* private Properties jdbcProps;

    private static final Logger logger= LogManager.getLogger();

    public JdbcUtils(Properties props){
        jdbcProps=props;
    }

    private  Connection instance=null;

    private Connection getNewConnection(){
        logger.traceEntry();

        String url=jdbcProps.getProperty("jdbc.url");
        logger.info("trying to connect to database ... {}",url);

        Connection con=null;
        try {
            con= DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error getting connection "+e);
        }
        return con;
    }

    public Connection getConnection(){
        logger.traceEntry();
        try {
            if (instance==null || instance.isClosed())
                instance=getNewConnection();

        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(instance);
        return instance;
    }

    */
   private Properties jdbcProps;
    private static final Logger logger = LogManager.getLogger();
    private Connection instance = null;

    public JdbcUtils(Properties props) {
        this.jdbcProps = props;
    }

    private Connection getNewConnection() {
        logger.traceEntry();

        String url = jdbcProps.getProperty("jdbc.url");
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
                instance = getNewConnection();  // ✅ Assign the connection to `instance`
            }
        } catch (SQLException e) {
            logger.error("Database error: ", e);
            throw new RuntimeException("Error getting database connection", e);
        }
        logger.traceExit(instance);
        return instance;
    }
}