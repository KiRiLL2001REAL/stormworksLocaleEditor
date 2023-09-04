package ru.example.localetool.model;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private String filename;
    private final String HEADER;
    private List<String> localeStrings;
    private long currentStringIdx;
    private final long startStringIdx;

    public DataModel() {
        filename = null;
        HEADER = "id\tdescription\ten\tlocal";
        localeStrings = new ArrayList<>();
        startStringIdx = 1;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getHeader() {
        return HEADER;
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
