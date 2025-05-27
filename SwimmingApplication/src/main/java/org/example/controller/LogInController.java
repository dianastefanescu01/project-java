package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.service.ParticipantRaceService;
import org.example.service.ParticipantService;
import org.example.service.RaceService;
import org.example.service.UserService;

import java.io.IOException;

public class LogInController {

    private UserService userService;
    private ParticipantService participantService;
    private RaceService raceService;
    private User user;
    private ParticipantRaceService participantRaceService;

    @FXML
    public TextField textFieldEmail;
    @FXML
    public PasswordField textFieldPassword;
    @FXML
    public Button loginBtn;
    @FXML
    public Button registerBtn;

    public void setService(UserService service, RaceService raceService, ParticipantService participantService, ParticipantRaceService participantRaceService){
        this.userService = service;
        this.raceService = raceService;
        this.participantService = participantService;
        this.participantRaceService = participantRaceService;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

    @FXML
    public void initialize(){
        textFieldEmail.setText("");
        textFieldPassword.setText("");
    }

    public void login(ActionEvent actionEvent) throws IOException {
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText(); // Optional, for now

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(Alert.AlertType.ERROR, "Email or Password are empty", "Please enter email and password");
            return;
        }

        User currentOfficePerson = userService.autentificate(email, password);
        if (currentOfficePerson == null) {
            showMessage(Alert.AlertType.ERROR, "Invalid login", "Authentication failed");
            return;
        }

        try {
            // Load main window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-view.fxml"));
            AnchorPane mainView = loader.load();

            // Get controller and pass services
            MainController mainController = loader.getController();
            mainController.setServices(participantService, raceService, participantRaceService, userService);

            // Show new scene
            Stage mainStage = new Stage();
            mainStage.setTitle("Main Window");
            mainStage.setScene(new Scene(mainView, 700, 800));
            mainStage.show();

            // Close login window
            Stage thisStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            thisStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showMessage(Alert.AlertType.ERROR, "Load Error", "Could not load the main window.");
        }
    }

    public void register(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register-view.fxml"));
        AnchorPane registerView = loader.load();
        RegisterController registerController = loader.getController();
        registerController.setUserService(userService);
        Stage stage = new Stage();
        stage.setTitle("Register");
        stage.setWidth(500);
        stage.setHeight(700);
        stage.setScene(new Scene(registerView));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Button) actionEvent.getSource()).getScene().getWindow());
        stage.show();
    }

    private void showMessage(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
