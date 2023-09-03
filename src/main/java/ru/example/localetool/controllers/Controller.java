package ru.example.localetool.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ru.example.localetool.logic.BusinessLogic;

public class Controller extends BusinessLogic {
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


}