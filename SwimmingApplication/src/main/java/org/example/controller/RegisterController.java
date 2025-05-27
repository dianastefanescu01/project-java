package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.model.User;
import org.example.service.ParticipantRaceService;
import org.example.service.ParticipantService;
import org.example.service.RaceService;
import org.example.service.UserService;

public class RegisterController {
    private UserService userService;
    private ParticipantService participantService;
    private RaceService raceService;
    private User user;
    private ParticipantRaceService participantRaceService;
    @FXML
    public TextField username;
    @FXML
    public TextField email;
    @FXML
    public TextField password;
    @FXML
    public TextField confirmPassword;
    @FXML
    public Button registerBtn;

    public void setUserService(UserService service){
        this.userService = service;
    }

    public void setServices(UserService userService, ParticipantService participantService, RaceService raceService, ParticipantRaceService participantRaceService){
        this.userService = userService;
        this.participantService = participantService;
        this.raceService = raceService;
        this.participantRaceService = participantRaceService;
    }

    @FXML
    public void initialize(){
        username.setText("");
        email.setText("");
        password.setText("");
        confirmPassword.setText("");
    }

    @FXML
    public void register(ActionEvent actionEvent) {
        if(username.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty()) {

            showMessage(Alert.AlertType.ERROR, "Error", "Please fill all the fields");
        }
        else {
            if(password.getText().equals(confirmPassword.getText())) {
                User user = new User(username.getText(),email.getText(),password.getText());
                userService.addUser(user);

                showMessage(Alert.AlertType.CONFIRMATION, "Success", "Registration succeeded");
                registerBtn.getScene().getWindow();
            }
            else{
                showMessage(Alert.AlertType.ERROR, "Error", "Registration failed. Passwords do not match");
            }
        }
    }

    private void showMessage(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
