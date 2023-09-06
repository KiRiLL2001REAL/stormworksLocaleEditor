package ru.example.localetool.model;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private String filename;
    private List<String> ids;
    private List<String> descriptions;
    private List<String> ens;
    private List<String> locales;
    private long currentStringIdx;
    private final long startStringIdx;

    public DataModel() {
        filename = null;

        ids = null;
        descriptions = null;
        ens = null;
        locales = null;

        startStringIdx = 1;
    }



    // GETTERS AND SETTERS SECTION

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public List<String> getEns() {
        return ens;
    }

    public void setEns(List<String> ens) {
        this.ens = ens;
    }

    public List<String> getLocales() {
        return locales;
    }

    public void setLocales(List<String> locales) {
        this.locales = locales;
    }

    public long getCurrentStringIdx() {
        return currentStringIdx;
    }

    public void setCurrentStringIdx(long currentLine) {
        this.currentStringIdx = currentLine;
    }

    public long getStartStringIdx() {
        return startStringIdx;
    }

    public long getLinesTotal() {
        return ids.size();
    }



    // OTHER METHODS SECTION

    public String getHeader() {
        if (ids == null || ids.size() == 0)
            return null;
        return combineLocaleString(0);
    }

    public List<String> getLocaleStrings() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++)
            result.add(combineLocaleString(i));
        return result;
    }

    private String combineLocaleString(int idx) {
        return ids.get(idx) + '\t' + descriptions.get(idx) + '\t' + ens.get(idx) + '\t' + locales.get(idx);
    }
}
