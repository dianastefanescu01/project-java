package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repository.ParticipantRaceRepository;
import org.example.repository.ParticipantRepository;
import org.example.repository.RaceRepository;
import org.example.repository.UserRepository;
import org.example.server.AppServicesImpl;
import org.example.service.IAppServices;
import org.example.utils.AbstractServer;
import org.example.utils.AppRpcConcurrentServer;
import org.example.utils.ServerException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;
    private static Logger logger = LogManager.getLogger(StartRpcServer.class);
    public static void main(String[] args) {
        // UserRepository userRepo=new UserRepositoryMock();
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/swimmingserver.properties"));
            logger.info("Server properties set {}",serverProps);
            // serverProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find swimmingserver.properties "+e);
            logger.debug("Looking for file in "+(new File(".")).getAbsolutePath());
            return;
        }
        RaceRepository race = new RaceRepository(serverProps);
        UserRepository user=new UserRepository(serverProps);
        ParticipantRaceRepository registration=new ParticipantRaceRepository(serverProps);
        ParticipantRepository participant=new ParticipantRepository(serverProps);
        IAppServices chatServerImpl=new AppServicesImpl(race,user,registration,participant);
        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("swimming.server.port"));
        }catch (NumberFormatException nef){
            logger.error("Wrong  Port Number"+nef.getMessage());
            logger.debug("Using default port "+defaultPort);
        }
        logger.debug("Starting server on port: "+chatServerPort);
        AbstractServer server = new AppRpcConcurrentServer(chatServerPort, chatServerImpl);
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                logger.error("Error stopping server "+e.getMessage());
            }
        }
    }
}
