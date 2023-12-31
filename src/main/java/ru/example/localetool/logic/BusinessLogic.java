package ru.example.localetool.logic;

import ru.example.localetool.model.DataModel;
import ru.example.localetool.model.DataModelUtility;
import ru.example.localetool.model.config.GlobalConfig;

import java.io.*;
import java.util.List;

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
     * Также путь к файлу прописывается в {@link GlobalConfig конфигурационном файле} и в самой
     * {@link DataModel модели}.
     *
     * @param file файл локализации Stormworks.
     *
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * (Смотри список исключений {@link DataModelUtility#loadLocalization(File)})
     *
     * @see DataModel#getLocaleStrings()
     * @see DataModel#getFilename()
     * @see GlobalConfig#getLastOpenedFile()
     */
    protected void onFileOpenLogic(File file) throws Exception {
        GlobalConfig config = GlobalConfig.getInstance();

        String fileAbsolutePath = file.getAbsolutePath();
        try {
            List<String> localeStrings = DataModelUtility.loadLocalization(file);
            DataModelUtility.setLocaleStrings(data, localeStrings);
            data.setFilename(fileAbsolutePath);
            if (!config.getLastOpenedFile().equals(fileAbsolutePath)) {
                config.setLastOpenedFile(fileAbsolutePath);
                config.setLastEditedLine(1);
                config.store();
            }
            data.setCurrentLine(config.getLastEditedLine());
        } catch (FileNotFoundException e) {
            config.setLastOpenedFile("");
            config.setLastEditedLine(1);
            config.store();
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
            getData().flushChanged();
        } catch (NullPointerException ignore) {
        }
    }

    /**
     * Сохраняет некоторые параметры окна в {@link GlobalConfig конфигурационный файл}.
     *
     * @param sceneWidth ширина окна.
     * @param sceneHeight высота окна.
     */
    protected void saveWindowConfiguration(double sceneWidth, double sceneHeight) {
        GlobalConfig config = GlobalConfig.getInstance();
        config.setSceneWidth(sceneWidth);
        config.setSceneHeight(sceneHeight);
        config.store();
    }
}
