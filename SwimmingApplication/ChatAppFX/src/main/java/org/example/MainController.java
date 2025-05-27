package org.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.Race;
import org.example.model.User;
import org.example.server.ParticipantRaceService;
import org.example.server.ParticipantService;
import org.example.server.RaceService;
import org.example.server.UserService;
import org.example.service.ApplicationException;
import org.example.service.IAppObserver;
import org.example.service.IAppServices;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements IAppObserver {
    private UserService userService;
    private ParticipantRaceService participantRaceService;
    private ParticipantService participantService;
    private RaceService raceService;
    private LogInController logInController;
    private IAppServices appServicesRpcProxy;

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

    public void setAppServicesRpcProxy(IAppServices appServicesRpcProxy) {
        this.appServicesRpcProxy = appServicesRpcProxy;
    }

    public void setLogInController(LogInController logInController) {
        this.logInController = logInController;
    }

    public void setServices(ParticipantService sp, RaceService sr, ParticipantRaceService sreg, UserService userService) {
        this.userService = userService;
        this.participantService = sp;
        this.raceService = sr;
        this.participantRaceService = sreg;
        //loadAllData();
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
        //columnDistanceRace.setCellValueFactory(cellData-> new SimpleIntegerProperty(cellData.getValue().getDistance()).asObject());
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

        //loadAllData();
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
        //participantRaceService.addPr(registration);

        try {
            appServicesRpcProxy.addRegistration(registration);
            showAlert("Success", "Participant registered to race.");
            // Observer will update the table via registrationMade
        } catch (ApplicationException e) {
            showAlert("Error", "Registration failed: " + e.getMessage());
        }

        loadRegistrations(); // Refresh table
    }

    public void loadAllData() {
        loadParticipants();
        loadRaces();
        loadRegistrations();
    }
    private void loadParticipants() {
        //List<Participant> participants = participantService.findAllParticipants();
        //tableParticipant.setItems(FXCollections.observableArrayList(participants));
        try {
            List<Participant> participants = appServicesRpcProxy.getAllParticipants();
            tableParticipant.getItems().setAll(participants);
            //tableParticipant.setItems(FXCollections.observableArrayList(participants));
        } catch (ApplicationException e) {
            showAlert("Error", "Unable to load participants: " + e.getMessage());
        }
    }
    /*private void loadRaces() {
        //List<Race> races = raceService.findAllRaces();
        //tableRace.setItems(FXCollections.observableArrayList(races));
        try {
            List<Race> races = appServicesRpcProxy.getAllRaces();
            tableRace.setItems(FXCollections.observableArrayList(races));
            //tableRace.getItems().setAll(races);
        } catch (ApplicationException e) {
            showAlert("Error", "Unable to load races: " + e.getMessage());
        }
    }*/


    private void loadRegistrations() {
        //List<ParticipantRace> registrations = participantRaceService.findAllPr();
        //tableRegistration.setItems(FXCollections.observableArrayList(registrations));
        try {
            List<ParticipantRace> registrations = appServicesRpcProxy.getAllRegistrations();
            tableRegistration.setItems(FXCollections.observableArrayList(registrations));
            //tableRegistration.getItems().setAll(registrations);
        } catch (ApplicationException e) {
            showAlert("Error", "Unable to load registrations: " + e.getMessage());
        }
    }

    /*private void loadParticipants() {
        Task<List<Participant>> task = new Task<>() {
            @Override
            protected List<Participant> call() throws Exception {
                return appServicesRpcProxy.getAllParticipants();
            }
        };

        task.setOnSucceeded(event -> {
            List<Participant> participants = task.getValue();
            Platform.runLater(() -> {
                tableParticipant.setItems(FXCollections.observableArrayList(participants));
            });
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            showAlert("Error", "Unable to load participants: " + exception.getMessage());
        });

        new Thread(task).start();
    }*/

    private void loadRaces() {
        /*Task<List<Race>> task = new Task<>() {
            @Override
            protected List<Race> call() throws Exception {
                return appServicesRpcProxy.getAllRaces();
            }
        };

        task.setOnSucceeded(event -> {
            List<Race> races = task.getValue();
            Platform.runLater(() -> {
                tableRace.setItems(FXCollections.observableArrayList(races));
            });
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            showAlert("Error", "Unable to load races: " + exception.getMessage());
        });

        new Thread(task).start();*/

        new Thread(()->{
            try{
                List<Race> races = appServicesRpcProxy.getAllRaces();
                Platform.runLater(()->{
                    tableRace.setItems(FXCollections.observableArrayList(races));
                });
            }
            catch(ApplicationException e) {
                showAlert("Error", "Unable to load races: " + e.getMessage());
            }
        }).start();

    }

   /* private void loadRegistrations() {
        Task<List<ParticipantRace>> task = new Task<>() {
            @Override
            protected List<ParticipantRace> call() throws Exception {
                return appServicesRpcProxy.getAllRegistrations();
            }
        };

        task.setOnSucceeded(event -> {
            List<ParticipantRace> registrations = task.getValue();
            Platform.runLater(() -> {
                tableRegistration.setItems(FXCollections.observableArrayList(registrations));
            });
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            showAlert("Error", "Unable to load registrations: " + exception.getMessage());
        });

        new Thread(task).start();
    }
*/
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

    public static void setNewMainControllerView(LogInController controller, IAppServices service) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/view/main-view.fxml"));
        AnchorPane mainView = loader.load();
        MainController mainController = loader.getController();
        mainController.setLogInController(controller);
        mainController.setAppServicesRpcProxy(service);
        controller.setMainController(mainController);
        controller.setMainView(mainView);
    }

    @Override
    public void userLoggedIn(User user) throws ApplicationException {

    }

    @Override
    public void userLoggedOut(User user) throws ApplicationException {
        appServicesRpcProxy.logout(user);
        try {
            MainController.setNewMainControllerView(logInController, appServicesRpcProxy);
        } catch (IOException _) {

        }
    }

    @Override
    public void registrationMade(ParticipantRace registration) throws ApplicationException {
        Platform.runLater(() -> {
            tableRegistration.getItems().add(registration);
            tableRegistration.refresh();
        });
    }

    @Override
    public void addParticipant(Participant participant) throws ApplicationException {

    }

    @FXML
    public void handleCloseWindow(ActionEvent event) {
        if (logInController != null) {
            userLoggedOut(logInController.getUser());
        }
        buttonLogOut.getScene().getWindow().hide();
    }

}
