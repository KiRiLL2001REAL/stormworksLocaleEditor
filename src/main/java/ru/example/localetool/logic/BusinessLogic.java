package ru.example.localetool.logic;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.example.localetool.model.DataModel;
import ru.example.localetool.model.config.GlobalConfigHolder;
import ru.example.localetool.model.exception.UnsupportedFileStructureException;

import java.io.*;
import java.util.ArrayList;

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
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link BusinessLogic#loadLocalizationFile})
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
        loadLocalizationFileWrapper(selectedFile);
    }

    /**
     * Функция, загружающая строки из последнего редактированного файла.
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link BusinessLogic#loadLocalizationFile})
     */
    protected void onFileRecentOpenLogic() throws Exception {
        File selectedFile = new File(GlobalConfigHolder.getInstance().getLastOpenedFile());
        try {
            loadLocalizationFileWrapper(selectedFile);
        } catch (FileNotFoundException e) {
            GlobalConfigHolder.getInstance().setLastOpenedFile("");
            GlobalConfigHolder.getInstance().setLastEditedLine(1);
            GlobalConfigHolder.getInstance().storeConfig();
            throw e;
        }
    }

    /**
     * Обёртка для чтения файла.
     * <p>Если не возникает ошибки, то обновляются данные в {@link DataModel} и конфигурационном файле программы.
     * @param file Файл, откуда будет считаны строки локализации.
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link BusinessLogic#loadLocalizationFile})
     */
    private void loadLocalizationFileWrapper(File file) throws Exception {
        // Если загрузка файла удалась
        ArrayList<String> localeStrings = loadLocalizationFile(file);
        // Записываем строки локализации в DataModel
        data.setLocaleStrings(localeStrings);
        // Записываем путь до него в DataModel и в конфигурационный файл
        data.setFilename(file.getAbsolutePath());
        GlobalConfigHolder.getInstance().setLastOpenedFile(data.getFilename());
        GlobalConfigHolder.getInstance().storeConfig();
    }

    /**
     * Считывает файл локализации Stormworks.
     * @param file Файл, откуда будут считаны данные.
     * @throws NullPointerException Если файл оказался null.
     * @throws FileNotFoundException Если файл не найден.
     * @throws SecurityException Если нет прав на чтение файла.
     * @throws UnsupportedFileStructureException Если количество столбцов файла не равно 4, либо заголовок пустой.
     * @return {@link ArrayList} типа {@link String}, содержащий строки файла локализации без заголовка.
     */
    private ArrayList<String> loadLocalizationFile(File file)
            throws NullPointerException, FileNotFoundException, SecurityException, UnsupportedFileStructureException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8));
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException ignored) {
        }
        if (line == null)
            throw new UnsupportedFileStructureException("File is empty.");
        // При чтении первой строки проверяем, что в файле 4 колонки (разделитель - табуляция)
        int columnCount = 1;
        for (int i = 0; i < line.length(); i++)
            if (line.charAt(i) == '\t')
                columnCount += 1;
        if (columnCount != 4)
            throw new UnsupportedFileStructureException("Number of columns in the localization file differs from 4.");
        // Считываем строки локализации из файла
        ArrayList<String> localeStrings = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null)
                localeStrings.add(line);
            br.close();
        } catch (IOException ignored) {
        }
        return localeStrings;
    }
}
