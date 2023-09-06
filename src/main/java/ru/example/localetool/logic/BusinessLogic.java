package ru.example.localetool.logic;

import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.example.localetool.model.DataModel;
import ru.example.localetool.model.DataModelUtility;
import ru.example.localetool.model.config.GlobalConfigHolder;
import ru.example.localetool.view.DialogFactory;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class BusinessLogic {
    private DataModel data;

    public BusinessLogic() {
        data = new DataModel();
    }

    public DataModel getData() {
        return data;
    }

    /**
     * Функция, загружающая строки из файла, выбираемого пользователем.
     * <p>
     * При успешном открытии файла, в {@link DataModel модель} помещается его содержимое.
     * Так же путь к файлу прописывается в {@link GlobalConfigHolder конфигурационном файле} и в самой
     * {@link DataModel модели}.
     *
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * (Смотри список исключений {@link DataModelUtility#loadLocalization(File)})
     *
     * @see DataModel#getLocaleStrings()
     * @see DataModel#getFilename()
     * @see GlobalConfigHolder#getLastOpenedFile()
     */
    protected void onFileOpenLogic() throws Exception {
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
        String fileAbsolutePath = selectedFile.getAbsolutePath();

        data.setLocaleStrings(DataModelUtility.loadLocalization(selectedFile));
        data.setFilename(fileAbsolutePath);
        GlobalConfigHolder.getInstance().setLastOpenedFile(fileAbsolutePath);
        GlobalConfigHolder.getInstance().storeConfig();
    }

    /**
     * Функция, загружающая строки из последнего редактированного файла.
     * <p>
     * Если файл не найден, удаляет сведения о нём из конфигурационного файла программы.
     *
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link DataModelUtility#loadLocalization(File)})
     */
    protected void onFileRecentOpenLogic() throws Exception {
        File selectedFile = new File(GlobalConfigHolder.getInstance().getLastOpenedFile());
        try {
            data.setLocaleStrings(DataModelUtility.loadLocalization(selectedFile));
            data.setFilename(selectedFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            GlobalConfigHolder.getInstance().setLastOpenedFile("");
            GlobalConfigHolder.getInstance().setLastEditedLine(1);
            GlobalConfigHolder.getInstance().storeConfig();
            throw e;
        }
    }

    /**
     * Функция, сохраняющая строки текущего редактируемого файла на диск.
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link DataModelUtility#storeLocalization(List, File)})
     */
    protected void onFileSaveLogic() throws Exception {
        try {
            File selectedFile = new File(data.getFilename());
            DataModelUtility.storeLocalization(data.getLocaleStrings(), selectedFile);
        } catch (NullPointerException ignore) {
        }
    }

    /**
     * Функция, сохраняющая строки текущего редактируемого файла на диск в выбранный пользователем место.
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link DataModelUtility#storeLocalization(List, File)})
     */
    protected void onFileSaveAsLogic() throws Exception {
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

        DataModelUtility.storeLocalization(data.getLocaleStrings(), selectedFile);
    }

    // TODO: javadoc.
    protected boolean onExitLogic() {
        // TODO: добавить публичный метод isChanged в DataModel, и использовать его здесь для принятия решения
        //  показывать ли диалог-подтверждение.
        Optional<ButtonType> result = DialogFactory.buildConfirmationDialog("Вы уверены, что хотите выйти? " +
                "Все несохранённые изменения будут утеряны.")
                .showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
