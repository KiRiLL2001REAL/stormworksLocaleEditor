package ru.example.localetool.model;

import ru.example.localetool.model.exception.UnsupportedFileStructureException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataModelUtility {
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
        int columnCount = 1;
        for (int i = 0; i < line.length(); i++)
            if (line.charAt(i) == '\t')
                columnCount += 1;
        if (columnCount != 4)
            throw new UnsupportedFileStructureException("Number of columns in the localization file differs from 4.");
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


}
