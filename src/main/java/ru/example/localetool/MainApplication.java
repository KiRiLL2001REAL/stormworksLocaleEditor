package ru.example.localetool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import ru.example.localetool.controllers.Controller;
import ru.example.localetool.model.config.GlobalConfig;

import java.io.*;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("LocaleTool");
        stage.setScene(scene);
        stage.setMinWidth(640);
        stage.setMinHeight(480);

        Pair<Double, Double> windowConfiguration = loadWindowConfiguration();
        stage.setWidth(windowConfiguration.getKey());
        stage.setHeight(windowConfiguration.getValue());

        stage.show();

        Controller controller = fxmlLoader.getController();
        scene.getWindow().setOnCloseRequest(controller.onCloseRequestHandler);
    }

    public static void main(String[] args) {
        launch();
    }

    public static Pair<Double, Double> loadWindowConfiguration() {
        GlobalConfig config = GlobalConfig.getInstance();
        return new Pair<>(config.getSceneWidth(), config.getSceneHeight());
    }
}