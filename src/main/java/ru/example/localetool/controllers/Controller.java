package ru.example.localetool.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.example.localetool.logic.BusinessLogic;
import ru.example.localetool.model.config.GlobalConfigHolder;
import ru.example.localetool.model.exception.UnsupportedFileStructureException;
import ru.example.localetool.view.DialogFactory;

import java.io.FileNotFoundException;
import java.net.URL;
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
        mi_file_open.setOnAction(event -> {
            try {
                onFileOpenLogic();
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
        });
        mi_file_openRecent.setOnAction(event -> {
            try {
                onFileRecentOpenLogic();
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
        });
        mi_file_save.setOnAction(event -> {
            try {
                onFileSaveLogic();
            } catch (Exception e) {
                String message = "что-то пошло не так.";
                if (e instanceof SecurityException)
                    message = "нет прав доступа к выбранному файлу.";
                DialogFactory.buildWarningDialog("Произошла ошибка сохранения: " + message).showAndWait();
            }
        });
        mi_file_saveAs.setOnAction(event -> {
            try {
                onFileSaveAsLogic();
            } catch (Exception e) {
                if (e instanceof NullPointerException)
                    return;
                String message = "что-то пошло не так.";
                if (e instanceof FileNotFoundException)
                    message = "файл является директорией.";
                else if (e instanceof SecurityException)
                    message = "нет прав доступа к выбранному файлу.";
                DialogFactory.buildWarningDialog("Произошла ошибка сохранения: " + message).showAndWait();
            }
        });
        mi_file_quit.setOnAction(event -> {
            Stage stage = (Stage) workspace.getScene().getWindow();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
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

    // Forwarding function for use in MainApplication
    public boolean canShutdown() {
        return onExitLogic();
    }
}