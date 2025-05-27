package org.example;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.service.ApplicationException;

import swimming.SwimmingProto;
import swimming.SwimmingProto.*;
import swimming.SwimmingServiceGrpc;

import java.io.IOException;
import java.util.List;

public class GrpcMainCtrl {
    private SwimmingServiceGrpc.SwimmingServiceBlockingStub stub;
    private User loggedUser;

    @FXML
    private TableView<Participant> tableParticipant;
    @FXML
    private TableColumn<Participant, Integer> columnIdParticipant;
    @FXML
    private TableColumn<Participant, String> columnNameParticipant;
    @FXML
    private TableColumn<Participant, Integer> columnAgeParticipant;

    @FXML
    private TableView<Race> tableRace;
    @FXML
    private TableColumn<Race, Integer> columnIdRace;
    @FXML
    private TableColumn<Race, Integer> columnDistanceRace;
    @FXML
    private TableColumn<Race, String> columnStyleRace;

    @FXML
    private TableView<ParticipantRace> tableRegistration;
    @FXML
    private TableColumn<ParticipantRace, String> columnRegParticipant;
    @FXML
    private TableColumn<ParticipantRace, String> columnRegStyle;
    @FXML
    private TableColumn<ParticipantRace, Integer> columnRegDistance;

    @FXML
    private TextField textFieldParticipant;
    @FXML
    private TextField textFieldStyle;
    @FXML
    private TextField textFieldDistance;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonLogOut;

    public void initialize() {
        columnIdParticipant.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnNameParticipant.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAgeParticipant.setCellValueFactory(new PropertyValueFactory<>("age"));

        columnIdRace.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnDistanceRace.setCellValueFactory(new PropertyValueFactory<>("distance"));
        columnStyleRace.setCellValueFactory(new PropertyValueFactory<>("style"));

        columnRegParticipant.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getParticipant().getName()));
        columnRegStyle.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRace().getStyle()));
        columnRegDistance.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getRace().getDistance()).asObject());

        tableParticipant.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateTextFields());
        tableRace.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateTextFields());
    }

    public void setStub(SwimmingServiceGrpc.SwimmingServiceBlockingStub stub) {
        this.stub = stub;
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public void loadAllData() {
        try {
            ParticipantList participantsProto = stub.getAllParticipants(SwimmingProto.Empty.newBuilder().build());
            List<Participant> participants = participantsProto.getParticipantsList();
            tableParticipant.setItems(FXCollections.observableList(participants));

            RaceList racesProto = stub.getAllRaces(SwimmingProto.Empty.newBuilder().build());
            List<Race> races = racesProto.getRacesList();
            tableRace.setItems(FXCollections.observableList(races));

            RegistrationList registrations = stub.getAllRegistrations(SwimmingProto.Empty.newBuilder().build());
            List<ParticipantRace> registrationsList = registrations.getRegistrationsList();
            tableRegistration.setItems(FXCollections.observableArrayList(registrationsList));
        } catch (ApplicationException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    public void handleAddRegistration(ActionEvent event) {
        Participant selectedParticipant = tableParticipant.getSelectionModel().getSelectedItem();
        Race selectedRace = tableRace.getSelectionModel().getSelectedItem();
        ParticipantRace registration = ParticipantRace.newBuilder().setId(0).setParticipant(selectedParticipant).setRace(selectedRace).build();
        if (selectedParticipant == null || selectedRace == null) {
            showAlert("Missing Selection", "Please select both a participant and a race.");
            return;
        }

        try {
            stub.addRegistration(registration);
            loadAllData();
            showAlert("Success", "Registration added successfully.");
        } catch (ApplicationException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    public void handleLogOut(ActionEvent event) {
        Stage stage = (Stage) buttonLogOut.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
            AnchorPane loginView = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(loginView, 500, 700));
            loginStage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load login window.");
        }
    }

    private void updateTextFields() {
        Participant participant = tableParticipant.getSelectionModel().getSelectedItem();
        Race race = tableRace.getSelectionModel().getSelectedItem();

        textFieldParticipant.setText(participant != null ? participant.getName() : "");
        textFieldStyle.setText(race != null ? race.getStyle() : "");
        textFieldDistance.setText(race != null ? String.valueOf(race.getDistance()) : "");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
