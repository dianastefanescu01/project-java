package org.example.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.Race;
import org.example.service.ParticipantRaceService;
import org.example.service.ParticipantService;
import org.example.service.RaceService;
import org.example.service.UserService;
import java.io.IOException;
import java.util.List;

public class MainController {
    private UserService userService;
    private ParticipantRaceService participantRaceService;
    private ParticipantService participantService;
    private RaceService raceService;
    private LogInController logInController;

    @FXML
    public TableView<Participant> tableParticipant;
    @FXML
    public TableColumn<Participant, Integer> columnIdParticipant;
    @FXML
    public TableColumn<Participant, String> columnNameParticipant;
    @FXML
    public TableColumn<Participant, Integer> columnAgeParticipant;
    @FXML
    public TableView<Race> tableRace;
    @FXML
    public TableColumn<Race, Integer> columnIdRace;
    @FXML
    public TableColumn<Race, Integer> columnDistanceRace;
    @FXML
    public TableColumn<Race, String> columnStyleRace;
    @FXML
    public TableView<ParticipantRace> tableRegistration;
    @FXML
    public TableColumn<ParticipantRace, String> columnRegParticipant;
    @FXML
    public TableColumn<ParticipantRace, String> columnRegStyle;
    @FXML
    public TableColumn<ParticipantRace, Integer> columnRegDistance;
    @FXML
    public TextField textFieldParticipant;
    @FXML
    public TextField textFieldStyle;
    @FXML
    public TextField textFieldDistance;
    @FXML
    public Button buttonAdd;
    @FXML
    public Button buttonLogOut;

    public void setServices(ParticipantService sp, RaceService sr, ParticipantRaceService sreg, UserService userService) {
        this.userService = userService;
        this.participantService = sp;
        this.raceService = sr;
        this.participantRaceService = sreg;
        loadAllData();
    }

    @FXML
    public void initialize() {
        // Participants table
        columnIdParticipant.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnNameParticipant.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAgeParticipant.setCellValueFactory(new PropertyValueFactory<>("age"));

        // Races table
        columnIdRace.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnDistanceRace.setCellValueFactory(new PropertyValueFactory<>("distance"));
        columnStyleRace.setCellValueFactory(new PropertyValueFactory<>("style"));

        // Registrations table
        columnRegParticipant.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParticipantId().getName()));
        columnRegStyle.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRaceId().getStyle()));
        columnRegDistance.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRaceId().getDistance()).asObject());

        // Listeners to update text fields
        tableParticipant.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateTextFields());
        tableRace.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateTextFields());
    }


    @FXML
    public void handleAddRegistration(ActionEvent actionEvent) {
        if (participantService == null || raceService == null || participantRaceService == null) {
            showAlert("Error", "Services are not initialized!");
            return;
        }

        Participant selectedParticipant = tableParticipant.getSelectionModel().getSelectedItem();
        Race selectedRace = tableRace.getSelectionModel().getSelectedItem();

        if (selectedParticipant == null || selectedRace == null) {
            showAlert("Missing Selection", "Please select a participant and a race before adding.");
            return;
        }

        Integer participantID = selectedParticipant.getId();
        Integer raceID = selectedRace.getId();

        Participant participant = participantService.findParticipant(participantID);
        Race race = raceService.findRace(raceID);
        // Add to registration
        ParticipantRace registration = new ParticipantRace(participant, race);
        participantRaceService.addPr(registration);

        loadRegistrations(); // Refresh table
        showAlert("Success", "Participant registered to race.");
    }

    private void loadAllData() {
        loadParticipants();
        loadRaces();
        loadRegistrations();
    }
    private void loadParticipants() {
        List<Participant> participants = participantService.findAllParticipants();
        tableParticipant.setItems(FXCollections.observableArrayList(participants));
    }
    private void loadRaces() {
        List<Race> races = raceService.findAllRaces();
        tableRace.setItems(FXCollections.observableArrayList(races));
    }
    private void loadRegistrations() {
        List<ParticipantRace> registrations = participantRaceService.findAllPr();
        tableRegistration.setItems(FXCollections.observableArrayList(registrations));
    }

    private void updateTextFields() {
        Participant participant = tableParticipant.getSelectionModel().getSelectedItem();
        Race race = tableRace.getSelectionModel().getSelectedItem();

        textFieldParticipant.setText(participant != null ? participant.getName() : "");
        textFieldStyle.setText(race != null ? race.getStyle() : "");
        textFieldDistance.setText(race != null ? String.valueOf(race.getDistance()) : "");
    }


    @FXML
    public void handleLogOut(ActionEvent actionEvent) {
        Stage currentStage = (Stage) buttonLogOut.getScene().getWindow();
        currentStage.close();

        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
            Parent loginRoot = loader.load();

            LogInController loginController = loader.getController();
            loginController.setService(userService, raceService, participantService, participantRaceService);
            // Create and show the login window
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(loginRoot, 500, 700)); // Adjust the size accordingly
            loginStage.show();
        } catch (IOException e) {
            // Show error if the login window cannot be loaded
            showAlert("Error", "An error occurred while opening the login window.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
