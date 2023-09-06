package ru.example.localetool.model;

import ru.example.localetool.model.exception.UnsupportedFileStructureException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataModelUtility {
    /** Количество подстрок-столбцов, содержащихся в одной строке файла локализации. */
    private static final int SUBSTRING_COUNT = 4;

    /**
     * Считывает файл локализации Stormworks из указанного файла.
     * @param file файл, откуда будут считаны данные.
     * @throws NullPointerException если файл оказался null.
     * @throws FileNotFoundException если файл не найден.
     * @throws UnsupportedFileStructureException если заголовок неправильный, либо файл пустой.
     * @throws IOException если произошла другая ошибка ввода/вывода.
     * @throws SecurityException если нет прав на чтение файла.
     * @return список строк из файла.
     */
    public static List<String> loadLocalization(File file)
            throws NullPointerException, FileNotFoundException, UnsupportedFileStructureException, IOException,
            SecurityException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8));
        ArrayList<String> localeStrings = new ArrayList<>();

        String line = br.readLine();
        if (line == null)
            throw new UnsupportedFileStructureException("File is empty.");
        localeStrings.add(line);
        // При чтении первой строки проверяем, что в файле 4 колонки (разделитель - табуляция)
        splitLocaleString(line);
        // Считываем строки локализации из файла
        while ((line = br.readLine()) != null)
            localeStrings.add(line);
        br.close();

        return localeStrings;
    }

    /**
     * Сохраняет строки локализации Stormworks в указанный файл.
     * @param localeStrings список строк.
     * @param file файл, в который будут сохранены данные.
     * @throws NullPointerException если передан null аргумент.
     * @throws FileNotFoundException если указанный файл является директорией.
     * @throws IOException если произошла другая ошибка ввода/вывода.
     * @throws SecurityException если нет прав на запись в файл.
     */
    public static void storeLocalization(List<String> localeStrings, File file)
            throws NullPointerException, FileNotFoundException, IOException, SecurityException
    {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8));
        for (String line : localeStrings)
            bw.write(line + '\n');
        bw.close();
    }

    /**
     * Разделяет строку локализации Stormworks на 4 части.
     *
     * @param localeString строка локализации.
     *
     * @return 4 подстроки, полученные путём разделения исходной по символу
     * табуляции:
     * <p>id, description, en, local.
     *
     * @throws UnsupportedFileStructureException если количество символов
     * табуляции в строке отличается от 3.
     */
    public static String[] splitLocaleString(String localeString) throws UnsupportedFileStructureException {
        String[] result = new String[SUBSTRING_COUNT];

        byte[] bytes = localeString.getBytes(StandardCharsets.UTF_8);
        int currentIdx = 0;

        int cut = 0;
        int tabIdx = indexOfChar(bytes, '\t', currentIdx, bytes.length);
        while (cut < SUBSTRING_COUNT - 1 && tabIdx != -1) {
            result[cut] = new String(bytes, currentIdx, tabIdx - currentIdx, StandardCharsets.UTF_8);
            cut += 1;
            currentIdx = tabIdx + 1;
            tabIdx = indexOfChar(bytes, '\t', currentIdx, bytes.length);
        }
        result[cut] = new String(bytes, currentIdx, bytes.length - currentIdx, StandardCharsets.UTF_8);
        cut += 1;

        if (tabIdx != -1 || cut != SUBSTRING_COUNT)
            throw new UnsupportedFileStructureException("Number of columns in the localization file differs from 4.");

        return result;
    }

    private static int indexOfChar(byte[] value, int ch, int fromIdx, int max) {
        byte c = (byte) ch;
        for (int i = fromIdx; i < max; i++)
            if (value[i] == c)
                return i;
        return -1;
    }

    public static void setLocaleStrings(DataModel dataModel, List<String> localeStrings)
            throws UnsupportedFileStructureException
    {
        ArrayList<ArrayList<String>> parsedData = new ArrayList<>();
        for (int i = 0; i < SUBSTRING_COUNT; i++)
            parsedData.add(new ArrayList<>());

        for (String localeString : localeStrings) {
            String[] items = splitLocaleString(localeString);
            for (int i = 0; i < SUBSTRING_COUNT; i++)
                parsedData.get(i).add(items[i]);
        }

        dataModel.setIds(parsedData.get(0));
        dataModel.setDescriptions(parsedData.get(1));
        dataModel.setEns(parsedData.get(2));
        dataModel.setLocales(parsedData.get(3));
    }
}
