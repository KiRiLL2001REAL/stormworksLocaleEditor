package ru.example.localetool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.example.localetool.controllers.Controller;
import ru.example.localetool.model.config.GlobalConfigHolder;
import ru.example.localetool.view.DialogFactory;

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
        stage.setWidth(GlobalConfigHolder.getInstance().getSceneWidth());
        stage.setHeight(GlobalConfigHolder.getInstance().getSceneHeight());
        stage.show();

        Controller controller = fxmlLoader.getController();
        scene.getWindow().setOnCloseRequest(wEvent -> {
            if (controller.canShutdown()) {
                GlobalConfigHolder.getInstance().setSceneWidth(stage.getWidth());
                GlobalConfigHolder.getInstance().setSceneHeight(stage.getHeight());
                if (!GlobalConfigHolder.getInstance().storeConfig())
                    DialogFactory.buildWarningDialog("Произошла ошибка сохранения конфигурационного файла.")
                            .showAndWait();
            } else
                wEvent.consume();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}