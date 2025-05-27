package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repository.ParticipantRaceRepository;
import org.example.repository.ParticipantRepository;
import org.example.repository.RaceRepository;
import org.example.repository.UserRepository;
import org.example.rpcprotocol.AppServicesRpcProxy;
import org.example.server.ParticipantRaceService;
import org.example.server.ParticipantService;
import org.example.server.RaceService;
import org.example.server.UserService;
import org.example.service.IAppServices;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartRpcClientFX extends Application {
    private Stage primaryStage;
    private static int defaultChatPort = 55556;
    private static String defaultServer = "localhost";
    private static Logger logger = LogManager.getLogger(StartRpcClientFX.class);

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Starting RPC client for FX");
        Properties props = new Properties();
        try{
            props.load(StartRpcClientFX.class.getResourceAsStream("/swimming.properties"));
            logger.info("Client properties set {}",props);
        }catch (IOException e){
            logger.error("Cannot find swimmingClient.properties"+e);
            logger.debug("Looking for swimmingClient.properties into {}",(new File(".").getAbsolutePath()));
            return;
        }
        RaceRepository race = new RaceRepository(props);
        UserRepository user=new UserRepository(props);
        ParticipantRaceRepository registration=new ParticipantRaceRepository(props);
        ParticipantRepository participant=new ParticipantRepository(props);
        ParticipantService participantService=new ParticipantService(participant);
        ParticipantRaceService registrationService=new ParticipantRaceService(registration);
        RaceService raceService = new RaceService(race);
        UserService userService=new UserService(user);
        String serverIP=props.getProperty("swimming.server.host",defaultServer);
        int serverPort=defaultChatPort;
        try{
            serverPort=Integer.parseInt(props.getProperty("swimming.server.port"));
        }catch(NumberFormatException e){
            logger.error("Cannot find swimming.server.port"+e);
        }
        logger.info("Server port: {}",serverPort);
        logger.info("Server IP: {}",serverIP);
        IAppServices services=new AppServicesRpcProxy(serverIP,serverPort);

        /*FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
        stage.setTitle("Login");
        stage.setWidth(500);
        stage.setHeight(700);
        AnchorPane userLayout = fxmlLoader.load();
        stage.setScene(new Scene(userLayout));
        LogInController controller = fxmlLoader.getController();
        controller.setService(userService, raceService, participantService,registrationService);
        controller.setServiceProxy(services);

        MainController.setNewMainControllerView(controller, services);

        stage.show();

         */
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
        AnchorPane loginRoot = loginLoader.load();

        LogInController loginController = loginLoader.getController();
        loginController.setService(userService, raceService, participantService, registrationService);
        loginController.setServiceProxy(services);

        // Load the main-view.fxml for the main controller
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/view/main-view.fxml"));
        AnchorPane mainView = mainLoader.load();
        MainController mainController = mainLoader.getController();
        mainController.setServices(participantService, raceService,  registrationService,userService);
        mainController.setAppServicesRpcProxy(services);

        // Pass the mainView to the login controller
        loginController.setMainView(mainView);
        loginController.setMainController(mainController);

        stage.setTitle("Login");
        stage.setScene(new Scene(loginRoot, 500, 700)); // Login scene
        stage.show();
    }
}
