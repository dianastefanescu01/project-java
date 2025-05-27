package org.example;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import swimming.SwimmingServiceGrpc;

public class GrpcStartClient extends Application {

    private SwimmingServiceGrpc.SwimmingServiceBlockingStub stub;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Step 1: Open gRPC Channel to C# server
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 5000) // Ensure this matches your server config
                .usePlaintext() // disable TLS for dev
                .build();

        stub = SwimmingServiceGrpc.newBlockingStub(channel);

        // Step 2: Load login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
        AnchorPane loginRoot = loader.load();

        // Step 3: Set gRPC stub in controller
        GrpcLoginCtrl loginController = loader.getController();
        loginController.setStub(stub);

        // Step 4: Set up the primary stage
        Scene scene = new Scene(loginRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Swimming App - gRPC Client");
        primaryStage.setWidth(500);
        primaryStage.setHeight(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}