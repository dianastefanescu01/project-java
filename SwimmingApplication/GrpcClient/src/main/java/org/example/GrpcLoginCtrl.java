package org.example;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import swimming.*;
import java.awt.*;
import java.io.IOException;

public class GrpcLoginCtrl {

    @FXML
    public TextField textFieldEmail;

    @FXML
    public PasswordField textFieldPassword;

    @FXML
    public Button loginBtn;

    @FXML
    public Button registerBtn;

    private SwimmingServiceGrpc.SwimmingServiceBlockingStub stub;

    public void setStub(SwimmingServiceGrpc.SwimmingServiceBlockingStub stub) {
        this.stub = stub;
    }

    @FXML
    public void login(ActionEvent event) {
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(Alert.AlertType.ERROR, "Missing Fields", "Please enter both email and password.");
            return;
        }

        SwimmingProto.LoginRequest request = SwimmingProto.LoginRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();

        try {
            SwimmingProto.UserResponse response = stub.login(request);

            if (!response.getError().isEmpty()) {
                showMessage(Alert.AlertType.ERROR, "Login Failed", response.getError());
                return;
            }

            SwimmingProto.User user = response.getUser();

            // Load main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-view.fxml"));
            AnchorPane mainRoot = loader.load();

            GrpcMainCtrl mainCtrl = loader.getController();
            mainCtrl.setStub(stub);
            mainCtrl.setLoggedUser(user);
            mainCtrl.loadAllData();

            Stage stage = new Stage();
            stage.setTitle("Main Dashboard");
            stage.setScene(new Scene(mainRoot));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Button) event.getSource()).getScene().getWindow());

            stage.show();

            // Close login window
            //((Stage) loginBtn.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(Alert.AlertType.ERROR, "Error", "Failed to login: " + e.getMessage());
        }
    }

    @FXML
    public void register(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register-view.fxml"));
        AnchorPane registerView = loader.load();

// Load the new gRPC-based controller
        GrpcRegisterCtrl registerController = loader.getController();
        registerController.setStub(stub);  // Inject the gRPC stub

        Stage stage = new Stage();
        stage.setTitle("Register");
        stage.setWidth(500);
        stage.setHeight(700);
        stage.setScene(new Scene(registerView));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Button) event.getSource()).getScene().getWindow());
        stage.show();
    }

    private void showMessage(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
