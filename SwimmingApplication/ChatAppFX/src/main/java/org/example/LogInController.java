package org.example;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import org.example.model.User;
import org.example.server.ParticipantRaceService;
import org.example.server.ParticipantService;
import org.example.server.RaceService;
import org.example.server.UserService;
import org.example.service.ApplicationException;
import org.example.service.IAppServices;

import java.io.IOException;

public class LogInController {

    private IAppServices serviceProxy;
    private UserService userService;
    private ParticipantService participantService;
    private RaceService raceService;
    private User user;
    private ParticipantRaceService participantRaceService;
    private MainController mainController;
    private AnchorPane mainView;

    @FXML
    public TextField textFieldEmail;
    @FXML
    public PasswordField textFieldPassword;
    @FXML
    public Button loginBtn;
    @FXML
    public Button registerBtn;

    public void setServiceProxy(IAppServices serviceProxy) {
        this.serviceProxy = serviceProxy;
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void setMainView(AnchorPane mainView) {
        this.mainView = mainView;
    }

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

    /*@FXML
    public void login(ActionEvent event) {
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(Alert.AlertType.ERROR, "Email or Password are empty", "Please enter email and password");
            return;
        }

        User userAttempt = new User(email, password);

        try {
            user = serviceProxy.login(userAttempt, mainController);  // Server returns authenticated user
            mainController.initialize();

            Stage stage = new Stage();
            stage.setTitle("Main Page");
            stage.setWidth(1000);
            stage.setHeight(700);
            stage.setScene(new Scene(mainView));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Button) event.getSource()).getScene().getWindow());

            User finalUser = user;
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    mainController.userLoggedOut(finalUser);
                }
            });



            stage.show();

            // Close login window
            //((Stage) loginBtn.getScene().getWindow()).close();

        } catch (ApplicationException e) {
            showMessage(Alert.AlertType.ERROR, "Invalid login", "Authentication failed: " + e.getMessage());
        }
    }

     */

   /*@FXML
    public void login(ActionEvent event) {
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(Alert.AlertType.ERROR, "Email or Password are empty", "Please enter email and password");
            return;
        }

        User userAttempt = new User(email, password);

        // Use a Task to perform login off the JavaFX thread
        /*Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                return serviceProxy.login(userAttempt, mainController);  // background thread
            }
        };



        loginTask.setOnSucceeded(workerStateEvent -> {
            try {
                user = loginTask.getValue();
                //mainController.initialize();

                Stage stage = new Stage();
                stage.setTitle("Main Page");
                stage.setWidth(1000);
                stage.setHeight(700);
                stage.setScene(new Scene(mainView));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Button) event.getSource()).getScene().getWindow());



                stage.show();

                mainController.loadAllData();

                // Close login window
                //((Stage) loginBtn.getScene().getWindow()).close();

            } catch (Exception e) {
                showMessage(Alert.AlertType.ERROR, "Initialization error", "Could not initialize main window: " + e.getMessage());
            }
        });

        loginTask.setOnFailed(workerStateEvent -> {
            Throwable exception = loginTask.getException();
            showMessage(Alert.AlertType.ERROR, "Invalid login", "Authentication failed: " + exception.getMessage());
        });

        new Thread(loginTask).start();  // very important: start the task in a new thread
    }
    */

    public void login(ActionEvent event){
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(Alert.AlertType.ERROR, "Email or Password are empty", "Please enter email and password");
            return;
        }

        User userAttempt = new User(email, password);
        try{
            user = serviceProxy.login(userAttempt, mainController);
            Stage stage = new Stage();
            stage.setTitle("Main Page");
            stage.setWidth(1000);
            stage.setHeight(700);
            stage.setScene(new Scene(mainView));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Button) event.getSource()).getScene().getWindow());

            stage.show();
            mainController.loadAllData();
        }catch(ApplicationException e){
            showMessage(Alert.AlertType.ERROR, "Invalid login", "Authentication failed: " + e.getMessage());
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
