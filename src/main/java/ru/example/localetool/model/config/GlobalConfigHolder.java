package ru.example.localetool.model.config;

import javafx.util.Pair;
import org.ini4j.Ini;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GlobalConfigHolder {
    private final String configFilename = "globalConfig.ini";
    private final Pair<String, String> SCENE_WIDTH = new Pair<>("scene", "width");
    private final Pair<String, String> SCENE_HEIGHT = new Pair<>("scene", "height");
    private final Pair<String, String> LAST_OPENED_FILE = new Pair<>("editor", "lastOpenedFile");
    private final Pair<String, String> LAST_EDITED_LINE = new Pair<>("editor", "lastEditedLine");

    private double sceneWidth;
    private double sceneHeight;
    private String lastOpenedFile;
    private long lastEditedLine;


    private static final class InstanceHolder {
        private static final GlobalConfigHolder INSTANCE = new GlobalConfigHolder();
    }

    public static GlobalConfigHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private GlobalConfigHolder() {
        boolean success = false;
        //   В начале пытаемся прочитать конфигурационный файл.
        //   Если возникла ошибка вида FileNotFound, то файл ещё не создан и его необходимо проинициализировать
        // начальными значениями, в противном случае - кидается исключение.
        try (FileInputStream fis = new FileInputStream(configFilename)) {
            loadConfig(new Ini(fis));
            success = true;
        } catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) {
                throw new RuntimeException(e);
            }
        }
        if (!success) {
            // create a new configuration file
            try {
                createDefaultConfig();
                try (FileInputStream fis = new FileInputStream(configFilename)) {
                    loadConfig(new Ini(fis));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadConfig(Ini iniFile) {
        sceneWidth = iniFile.get(SCENE_WIDTH.getKey(), SCENE_WIDTH.getValue(), double.class);
        sceneHeight = iniFile.get(SCENE_HEIGHT.getKey(), SCENE_HEIGHT.getValue(), double.class);
        lastOpenedFile = iniFile.get(LAST_OPENED_FILE.getKey(), LAST_OPENED_FILE.getValue(), String.class);
        lastEditedLine = iniFile.get(LAST_EDITED_LINE.getKey(), LAST_EDITED_LINE.getValue(), long.class);
    }

    public boolean storeConfig() {
        try (FileOutputStream fos = new FileOutputStream(configFilename)) {
            Ini storedConfig = new Ini();
            storedConfig.put(SCENE_WIDTH.getKey(), SCENE_WIDTH.getValue(), sceneWidth);
            storedConfig.put(SCENE_HEIGHT.getKey(), SCENE_HEIGHT.getValue(), sceneHeight);
            storedConfig.put(LAST_OPENED_FILE.getKey(), LAST_OPENED_FILE.getValue(), lastOpenedFile);
            storedConfig.put(LAST_EDITED_LINE.getKey(), LAST_EDITED_LINE.getValue(), lastEditedLine);
            storedConfig.store(fos);
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    private void createDefaultConfig() throws IOException {
        sceneWidth = 640f;
        sceneHeight = 480f;
        lastOpenedFile = "";
        lastEditedLine = 1;
        storeConfig();
    }

    public double getSceneWidth() {
        return sceneWidth;
    }

    public void setSceneWidth(double sceneWidth) {
        this.sceneWidth = sceneWidth;
    }

    public double getSceneHeight() {
        return sceneHeight;
    }

    public void setSceneHeight(double sceneHeight) {
        this.sceneHeight = sceneHeight;
    }

    public String getLastOpenedFile() {
        return lastOpenedFile;
    }

    public void setLastOpenedFile(String lastOpenedFile) {
        this.lastOpenedFile = lastOpenedFile;
    }

    public long getLastEditedLine() {
        return lastEditedLine;
    }

    public void setLastEditedLine(long lastEditedLine) {
        this.lastEditedLine = lastEditedLine;
    }
}
