package ru.example.localetool.logic;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.example.localetool.model.DataModel;
import ru.example.localetool.model.config.GlobalConfigHolder;
import ru.example.localetool.model.exception.UnsupportedFileStructureException;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
     * <p>Если файл не найден, удаляет сведения о нём из конфигурационного файла программы.
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link BusinessLogic#loadLocalizationFile(File)})
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
     * @param file файл, откуда будет считаны строки локализации.
     * @throws Exception исключение является обобщением ошибки чтения файла.
     * <p>(Смотри список исключений {@link BusinessLogic#loadLocalizationFile(File)})
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
     * @param file файл, откуда будут считаны данные.
     * @throws NullPointerException если файл оказался null.
     * @throws FileNotFoundException если файл не найден.
     * @throws SecurityException если нет прав на чтение файла.
     * @throws UnsupportedFileStructureException если количество столбцов файла не равно 4, либо заголовок пустой.
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
        } catch (IOException ignored) {}
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
        } catch (IOException ignored) {}
        return localeStrings;
    }

    /**
     * Сохраняет файл локализации по пути, который содержится в классе {@link DataModel}.
     * @throws Exception исключение является обобщением ошибки записи файла.
     * <p>(Смотри список исключений {@link BusinessLogic#saveLocalizationFile(File)})
     */
    protected void onFileSaveLogic() throws Exception {
        try {
            saveLocalizationFile(new File(data.getFilename()));
        } catch (NullPointerException ignore) {
        }
    }

    /**
     * Сохраняет файл локализации в выбранный файл.
     * @throws Exception исключение является обобщением ошибки записи файла.
     * <p>(Смотри список исключений {@link BusinessLogic#saveLocalizationFile(File)})
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
        saveLocalizationFile(selectedFile);
    }

    /**
     * Сохраняет локализацию в указанный файл.
     * @param file файл, в который будут сохранены данные.
     * @throws NullPointerException если передан null аргумент.
     * @throws FileNotFoundException если указанный файл является директорией.
     * @throws SecurityException если нет прав на запись в файл.
     */
    private void saveLocalizationFile(File file)
            throws NullPointerException, FileNotFoundException, SecurityException
    {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            bw.write(data.getHeader() + '\n');
            for (String line : data.getLocaleStrings())
                bw.write(line + '\n');
            bw.close();
        } catch (IOException ignored) {}
    }
}
