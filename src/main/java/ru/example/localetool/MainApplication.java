package ru.example.localetool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.example.localetool.model.GlobalConfigHolder;

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

        stage.setOnHidden(windowEvent -> Platform.runLater(() -> {
            GlobalConfigHolder.getInstance().setSceneWidth(stage.getWidth());
            GlobalConfigHolder.getInstance().setSceneHeight(stage.getHeight());
            try {
                GlobalConfigHolder.getInstance().storeConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    //public static boolean checkColumnCount(String line, int count) {
    //    int found = 1;
    //    for (int i = 0; i < line.length(); i++)
    //        if (line.charAt(i) == '\t')
    //            found += 1;
    //    return found == count;
    //}

    public static void main(String[] args) {
        launch();

        //String path = "C:\\Users\\batyr\\AppData\\Roaming\\Stormworks\\data\\languages\\russian_by_mr.yogurt217.tsv";
        //boolean first_read = true;
        //long lineNumber = 1;
        //try (BufferedReader br = new BufferedReader(new InputStreamReader(
        //        new FileInputStream(path), java.nio.charset.StandardCharsets.UTF_8))
        //) {
        //    String line;
        //    while ((line = br.readLine()) != null) {
        //        if (first_read) {
        //            first_read = false;
        //            if (!checkColumnCount(line, 4)) {
        //                throw new RuntimeException("Column count different from 4.");
        //            }
        //            continue;
        //        }
        //        // next handling commands
        //        lineNumber += 1;
        //    }
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //    throw new RuntimeException(e);
        //}
        //System.out.println("Reading ends.");
    }
}