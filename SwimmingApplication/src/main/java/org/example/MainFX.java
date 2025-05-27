package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.controller.LogInController;
import org.example.repository.*;
import org.example.service.ParticipantRaceService;
import org.example.service.ParticipantService;
import org.example.service.RaceService;
import org.example.service.UserService;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) throws Exception{
        Properties props = new Properties();
        try{
            props.load(new FileReader("D:\\sem4\\project PA\\SwimmingApplication\\src\\main\\resources\\db.config"));
        }catch (IOException e){
            System.out.println("Cannot find db.config: " + e);
            return;
        }

        IParticipantRepository participantRepository = new ParticipantRepository(props);
        ParticipantService participantService = new ParticipantService(participantRepository);
        IRaceRepository raceRepository = new RaceRepository(props);
        RaceService raceService = new RaceService(raceRepository);
        IParticipantRaceRepository participantRaceRepository = new ParticipantRaceRepository(props);
        ParticipantRaceService participantRaceService = new ParticipantRaceService(participantRaceRepository);
        IUserRepository userRepository = new UserRepository(props);
        UserService userService = new UserService(userRepository);

       /* FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
        stage.setTitle("Log in");
        stage.setWidth(500);
        stage.setHeight(700);
        AnchorPane userLayout = fxmlLoader.load();
        stage.setScene(new Scene(userLayout));
        LogInController logInController = fxmlLoader.getController();
        logInController.setService(userService, raceService, participantService, participantRaceService);
        stage.show();
    */

       // Load login-view.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
        AnchorPane loginLayout = fxmlLoader.load();

        // Get controller and inject services
        LogInController loginController = fxmlLoader.getController();
        loginController.setService(userService, raceService, participantService, participantRaceService);

        // Setup scene and show stage
        stage.setTitle("Login");
        stage.setScene(new Scene(loginLayout, 500, 700));
        stage.show();


    }
}
