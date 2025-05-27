package org.example;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import swimming.SwimmingProto;
import swimming.SwimmingProto.*;
import swimming.SwimmingServiceGrpc;

import java.util.List;

public class GrpcRegisterCtrl {
    private SwimmingServiceGrpc.SwimmingServiceBlockingStub stub;

    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField confirmPassword;
    @FXML
    private Button registerBtn;

    @FXML
    private TableView<User> tableUsers;
    @FXML
    private TableColumn<User, Integer> columnId;
    @FXML
    private TableColumn<User, String> columnName;
    @FXML
    private TableColumn<User, String> columnEmail;

    public void setStub(SwimmingServiceGrpc.SwimmingServiceBlockingStub stub) {
        this.stub = stub;
    }

    @FXML
    public void initialize() {
        username.setText("");
        email.setText("");
        password.setText("");
        confirmPassword.setText("");

    }

    @FXML
    public void register(ActionEvent actionEvent) {
        if (username.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty()) {
            showMessage(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (!password.getText().equals(confirmPassword.getText())) {
            showMessage(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
            return;
        }

        User user = User.newBuilder()
                .setName(username.getText())
                .setEmail(email.getText())
                .setPassword(password.getText())
                .build();

        SwimmingProto.UserResponse response = stub.addUser(user);

        if (response.hasUser()) {
            showMessage(Alert.AlertType.INFORMATION, "Success", "Registration successful.");
        } else {
            showMessage(Alert.AlertType.ERROR, "Registration Failed", response.getError());
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
