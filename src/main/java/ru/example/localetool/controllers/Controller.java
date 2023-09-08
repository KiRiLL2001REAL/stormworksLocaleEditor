package ru.example.localetool.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.example.localetool.logic.BusinessLogic;
import ru.example.localetool.model.config.ApplicationConfig;
import ru.example.localetool.model.config.GlobalConfig;
import ru.example.localetool.model.exception.UnsupportedFileStructureException;
import ru.example.localetool.view.DialogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Paths;
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

    public final EventHandler<WindowEvent> onCloseRequestHandler = event -> {
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
        workspace.setDisable(true);
        navbar.setDisable(true);
        mi_file_save.setDisable(true);
        mi_file_saveAs.setDisable(true);

        GlobalConfig config = GlobalConfig.getInstance();
        if (config.getLastOpenedFile().isBlank())
            mi_file_openRecent.setDisable(true);

        initMenuActions();
        initWorkspaceActions();
        initNavBarActions();
    }

    private final ChangeListener<Number> onChangeEditingStringListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
            int idx = (int) newVal;

            String idStr = getData().getId(idx);
            String descriptionStr = getData().getDescription(idx);
            String originalStr = getData().getEn(idx);
            String translatedStr = getData().getLocale(idx);

            if (idStr.isBlank())
                idStr = "<не указано>";
            if (descriptionStr.isBlank())
                descriptionStr = "<не указано>";

            textfield_id.setText(idStr);
            textarea_description.setText(descriptionStr);
            textarea_original.setText(originalStr);
            textarea_translated.setText(translatedStr);
        }
    };

    private void initMenuActions() {
        mi_file_open.setOnAction(event -> onFileOpenPressed());
        mi_file_openRecent.setOnAction(event -> onFileOpenRecentPressed());
        mi_file_save.setOnAction(event -> onFileSavePressed());
        mi_file_saveAs.setOnAction(event -> onFileSaveAsPressed());
        mi_file_quit.setOnAction(event -> onQuitPressed());

        // Если данные не изменены, опция сохранения становится неактивной.
        getData().changedProperty().addListener((observableValue, oldVal, newVal) -> mi_file_save.setDisable(!newVal));

        // При изменении индекса текущей строки, загружаем данные полей из соответствующих компонентов DataModel.
        getData().currentLineProperty().addListener(onChangeEditingStringListener);
    }

    private void initWorkspaceActions() {

    }

    private void initNavBarActions() {

    }

    @FXML
    protected void onFileOpenPressed() {
        if (getData().isChanged() && !checkIfUserWantOpenWithoutSaving())
            return;

        String lastOpenedFile = GlobalConfig.getInstance().getLastOpenedFile();
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
            setupComponentsOnSuccessfulOpenFile();
            changeWindowTitle(selectedFile);
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
        if (getData().isChanged() && !checkIfUserWantOpenWithoutSaving())
            return;

        try {
            File file = new File(GlobalConfig.getInstance().getLastOpenedFile());
            onFileOpenLogic(file);
            setupComponentsOnSuccessfulOpenFile();
            changeWindowTitle(file);
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

    private void setupComponentsOnSuccessfulOpenFile() {
        workspace.setDisable(false);
        navbar.setDisable(false);
        mi_file_save.setDisable(true);
        mi_file_saveAs.setDisable(false);
        mi_file_openRecent.setDisable(false);
        textarea_translated.requestFocus();
    }

    private void changeWindowTitle(File file) {
        String filename = Paths.get(file.getAbsolutePath()).getFileName().toString();
        Stage stage = (Stage) workspace.getScene().getWindow();
        stage.setTitle(ApplicationConfig.APPLICATION_NAME + ": " + filename);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean checkIfUserWantOpenWithoutSaving() {
        Optional<ButtonType> result = DialogFactory.buildConfirmationDialog("В данном файле имеются " +
                "несохранённые изменения. Вы действительно хотите открыть другой файл без сохранения?").showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
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
        String lastOpenedFile = GlobalConfig.getInstance().getLastOpenedFile();
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