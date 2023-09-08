package ru.example.localetool.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private final SimpleBooleanProperty changed;
    private final SimpleIntegerProperty currentLine;
    private String filename;
    private List<String> ids;
    private List<String> descriptions;
    private List<String> ens;
    private List<String> locales;

    public DataModel() {
        changed = new SimpleBooleanProperty(false);
        currentLine = new SimpleIntegerProperty(0);

        filename = null;

        ids = null;
        descriptions = null;
        ens = null;
        locales = null;
    }



    // PROPERTIES

    public SimpleBooleanProperty changedProperty() {
        return changed;
    }

    public boolean isChanged() {
        return changed.get();
    }

    public void flushChanged() {
        changed.set(false);
    }

    public SimpleIntegerProperty currentLineProperty() {
        return currentLine;
    }

    public int getCurrentLine() {
        return currentLine.get();
    }

    public void setCurrentLine(int idx) {
        if (idx < 0)
            idx = 0;
        else if (idx >= ids.size())
            idx = ids.size() - 1;
        currentLine.set(idx);
    }



    // GETTERS AND SETTERS SECTION

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId(int idx) {
        if (ids == null || idx < 0 || idx >= ids.size())
            return null;
        return ids.get(idx);
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getDescription(int idx) {
        if (descriptions == null || idx < 0 || idx >= descriptions.size())
            return null;
        return descriptions.get(idx);
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public String getEn(int idx) {
        if (ens == null || idx < 0 || idx >= ens.size())
            return null;
        return ens.get(idx);
    }

    public void setEns(List<String> ens) {
        this.ens = ens;
    }

    public String getLocale(int idx) {
        if (locales == null || idx < 0 || idx >= locales.size())
            return null;
        return locales.get(idx);
    }

    public void setLocales(List<String> locales) {
        this.locales = locales;
    }

    public void setLocale(int idx, String locale) {
        if (locales == null || idx < 0 || idx >= locales.size()) {
            System.out.println("setLocale by incorrect idx, or localization is not loaded yet.");
            return;
        }
        changed.set(true);
        locales.set(idx, locale);
    }



    // OTHER METHODS SECTION

    public String getHeader() {
        if (ids == null || ids.size() == 0)
            return null;
        return combineLocaleString(0);
    }

    public long getLinesTotal() {
        return ids.size();
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
