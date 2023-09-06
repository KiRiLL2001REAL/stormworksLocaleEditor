package ru.example.localetool.logic;

import javafx.scene.control.ButtonType;
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
     * Функция, загружающая строки локализации из указанного файла в {@link DataModel модель}.
     * <p>
     * При успешном открытии файла, в {@link DataModel модель} помещается его содержимое.
     * Также путь к файлу прописывается в {@link GlobalConfigHolder конфигурационном файле} и в самой
     * {@link DataModel модели}.
     *
     * @param file файл локализации Stormworks.
     *
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * (Смотри список исключений {@link DataModelUtility#loadLocalization(File)})
     *
     * @see DataModel#getLocaleStrings()
     * @see DataModel#getFilename()
     * @see GlobalConfigHolder#getLastOpenedFile()
     */
    protected void onFileOpenLogic(File file) throws Exception {
        String fileAbsolutePath = file.getAbsolutePath();
        try {
            data.setLocaleStrings(DataModelUtility.loadLocalization(file));
            data.setFilename(fileAbsolutePath);
            GlobalConfigHolder config = GlobalConfigHolder.getInstance();
            if (!config.getLastOpenedFile().equals(fileAbsolutePath)) {
                GlobalConfigHolder.getInstance().setLastOpenedFile(fileAbsolutePath);
                GlobalConfigHolder.getInstance().store();
            }
        } catch (FileNotFoundException e) {
            GlobalConfigHolder.getInstance().setLastOpenedFile("");
            GlobalConfigHolder.getInstance().setLastEditedLine(1);
            GlobalConfigHolder.getInstance().store();
            throw e;
        }
    }

    /**
     * Функция, записывающая строки локализации на диск в указанный файл.
     *
     * @param file файл, в который будет сохранена локализация Stormworks.
     *
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link DataModelUtility#storeLocalization(List, File)})
     * <p>Примечание: {@link NullPointerException} подавляется.
     */
    protected void onFileSaveLogic(File file) throws Exception {
        try {
            DataModelUtility.storeLocalization(data.getLocaleStrings(), file);
        } catch (NullPointerException ignore) {
        }
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
