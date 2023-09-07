package ru.example.localetool.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.example.localetool.logic.BusinessLogic;
import ru.example.localetool.model.config.GlobalConfigHolder;
import ru.example.localetool.model.exception.UnsupportedFileStructureException;
import ru.example.localetool.view.DialogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller extends BusinessLogic implements Initializable {
    // Menu FILE
    @FXML
    private MenuItem mi_file_open;
    @FXML
    private MenuItem mi_file_openRecent;
    @FXML
    private MenuItem mi_file_save;
    @FXML
    private MenuItem mi_file_saveAs;
    @FXML
    private MenuItem mi_file_quit;

    // Menu HELP
    @FXML
    private MenuItem mi_help_about;

    // Main workspace
    @FXML
    private AnchorPane workspace;
    @FXML
    private TextField textfield_id;
    @FXML
    private TextArea textarea_description;
    @FXML
    private TextArea textarea_original;
    @FXML
    private TextArea textarea_translated;

    // Navigation bar
    @FXML
    private AnchorPane navbar;
    @FXML
    private Button button_backForce;
    @FXML
    private Button button_back;
    @FXML
    private TextField textfield_currentLine;
    @FXML
    private Button button_forward;
    @FXML
    private Button button_forwardForce;
    @FXML
    private Button button_confirmAndToNext;

    public EventHandler<WindowEvent> onCloseRequestHandler = event -> {
        if (!getData().isChanged())
            return;
        Optional<ButtonType> result = DialogFactory.buildConfirmationDialog(
                "Вы уверены, что хотите выйти? Все несохранённые изменения будут утеряны.")
                .showAndWait();
        boolean userIsAgree = result.isPresent() && result.get() == ButtonType.OK;
        if (!userIsAgree) {
            event.consume();
            return;
        }
        Stage stage = (Stage) workspace.getScene().getWindow();
        saveWindowConfiguration(stage.getWidth(), stage.getHeight());
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setWorkspaceEnable(false);
        setMenuSaveEnable(false);

        if (GlobalConfigHolder.getInstance().getLastOpenedFile().isBlank())
            mi_file_openRecent.setDisable(true);

        initMenuActions();
        initWorkspaceActions();
        initNavBarActions();
    }

    private void initMenuActions() {
        mi_file_open.setOnAction(event -> onFileOpenPressed());
        mi_file_openRecent.setOnAction(event -> onFileOpenRecentPressed());
        mi_file_save.setOnAction(event -> onFileSavePressed());
        mi_file_saveAs.setOnAction(event -> onFileSaveAsPressed());
        mi_file_quit.setOnAction(event -> onQuitPressed());
    }

    private void initWorkspaceActions() {

    }

    private void initNavBarActions() {

    }

    private void setWorkspaceEnable(boolean enable) {
        workspace.setDisable(!enable);
        navbar.setDisable(!enable);
    }

    private void setMenuSaveEnable(boolean enable) {
        mi_file_save.setDisable(!enable);
        mi_file_saveAs.setDisable(!enable);
    }

    @FXML
    protected void onFileOpenPressed() {
        String lastOpenedFile = GlobalConfigHolder.getInstance().getLastOpenedFile();
        File initialDirectory = lastOpenedFile.isBlank() ?
                new File(System.getProperty("user.dir"))
                : new File(lastOpenedFile.substring(0, lastOpenedFile.lastIndexOf('\\')));

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл локализации...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tab Separated Values", "*.tsv"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        fileChooser.setInitialDirectory(initialDirectory);

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        try {
            onFileOpenLogic(selectedFile);
            setWorkspaceEnable(true);
            setMenuSaveEnable(true);
            mi_file_openRecent.setDisable(false);
            textarea_translated.requestFocus();
        } catch (Exception e) {
            if (e instanceof NullPointerException)
                DialogFactory.buildWarningDialog("Файл локализации не был выбран.").showAndWait();
            else if (e instanceof SecurityException)
                DialogFactory.buildWarningDialog("Нет прав доступа к выбранному файлу.").showAndWait();
            else if (e instanceof UnsupportedFileStructureException)
                DialogFactory.buildWarningDialog("Выбранный файл имеет неподдерживаемую структуру.")
                        .showAndWait();
        }
    }

    @FXML
    protected void onFileOpenRecentPressed() {
        try {
            File file = new File(GlobalConfigHolder.getInstance().getLastOpenedFile());

            onFileOpenLogic(file);
            setWorkspaceEnable(true);
            setMenuSaveEnable(true);
            textarea_translated.requestFocus();
        } catch (Exception e) {
            mi_file_openRecent.setDisable(true);
            if (e instanceof FileNotFoundException) {
                textfield_currentLine.setText("");
                DialogFactory.buildWarningDialog("Файл локализации не найден.").showAndWait();
            }
            else if (e instanceof SecurityException)
                DialogFactory.buildWarningDialog("Нет прав доступа к выбранному файлу.").showAndWait();
        }
    }

    @FXML
    protected void onFileSavePressed() {
        try {
            File file = new File(getData().getFilename());
            onFileSaveLogic(file);
        } catch (Exception e) {
            String message = "что-то пошло не так.";
            if (e instanceof SecurityException)
                message = "нет прав доступа к выбранному файлу.";
            DialogFactory.buildWarningDialog("Произошла ошибка сохранения: " + message).showAndWait();
        }
    }

    @FXML
    protected void onFileSaveAsPressed() {
        // Устанавливаем начальную директорию
        String lastOpenedFile = GlobalConfigHolder.getInstance().getLastOpenedFile();
        File initialDirectory = lastOpenedFile.isBlank() ?
                new File(System.getProperty("user.dir"))
                : new File(lastOpenedFile.substring(0, lastOpenedFile.lastIndexOf('\\')));

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл локализации...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tab Separated Values", "*.tsv"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        fileChooser.setInitialDirectory(initialDirectory);

        File selectedFile = fileChooser.showSaveDialog(new Stage());

        try {
            onFileSaveLogic(selectedFile);
        } catch (Exception e) {
            String message = "что-то пошло не так.";
            if (e instanceof FileNotFoundException)
                message = "файл является директорией.";
            else if (e instanceof SecurityException)
                message = "нет прав доступа к выбранному файлу.";
            DialogFactory.buildWarningDialog("Произошла ошибка сохранения: " + message).showAndWait();
        }
    }

    @FXML
    protected void onQuitPressed() {
        Stage stage = (Stage) workspace.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}