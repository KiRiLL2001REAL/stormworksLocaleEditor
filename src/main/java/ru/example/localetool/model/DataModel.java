package ru.example.localetool.model;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private String filename;
    private List<String> localeStrings;
    private long currentStringIdx;
    private final long startStringIdx;

    public DataModel() {
        filename = null;
        localeStrings = null;
        startStringIdx = 1;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getHeader() {
        if (localeStrings == null || localeStrings.size() == 0)
            return null;
        return localeStrings.get(0);
    }

    public List<String> getLocaleStrings() {
        return localeStrings;
    }

    public void setLocaleStrings(List<String> localeStrings) {
        this.localeStrings = localeStrings;
    }

    public long getCurrentStringIdx() {
        return currentStringIdx;
    }

    public void setCurrentStringIdx(long current_line) {
        this.currentStringIdx = current_line;
    }

    public long getStartStringIdx() {
        return startStringIdx;
    }

    public long getLinesTotal() {
        return localeStrings.size();
    }
}
