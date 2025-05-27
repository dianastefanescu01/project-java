package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repository.*;
import org.example.repository.hibernate.*;
import org.example.server.AppServicesImpl;
import org.example.service.IAppServices;
import org.example.utils.AbstractServer;
import org.example.utils.AppRpcConcurrentServer;
import org.example.utils.ServerException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartHibernateServer {
    private static final int defaultPort = 55556;
    private static final Logger logger = LogManager.getLogger(StartHibernateServer.class);

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartHibernateServer.class.getResourceAsStream("/swimmingserver.properties"));
            logger.info("Server properties loaded: {}", serverProps);
        } catch (IOException e) {
            logger.error("Cannot find swimmingserver.properties: " + e);
            logger.debug("Looking for file in " + (new File(".")).getAbsolutePath());
            return;
        }

        // Initialize Repositories
        IUserRepository userRepository = new UserRepositoryHibernate(serverProps);
        IRaceRepository raceRepository = new RaceRepositoryHibernate(serverProps);
        IParticipantRepository participantRepository = new ParticipantRepositoryHibernate(serverProps);
        IParticipantRaceRepository registrationRepository = new ParticipantRaceRepositoryHibernate(serverProps);

        // Initialize service layer
        IAppServices service = new AppServicesImpl(raceRepository, userRepository, registrationRepository, participantRepository);

        int port = defaultPort;
        try {
            port = Integer.parseInt(serverProps.getProperty("swimming.server.port"));
        } catch (NumberFormatException e) {
            logger.error("Invalid port number: {}", e.getMessage());
            logger.debug("Using default port: {}", defaultPort);
        }

        AbstractServer server = new AppRpcConcurrentServer(port, service);
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Error starting the server: {}", e.getMessage());
        } finally {
            try {
                server.stop();
                HibernateUtils.closeSessionFactory();
            } catch (ServerException e) {
                logger.error("Error stopping the server: {}", e.getMessage());
            }
        }
    }
}
