package ru.example.localetool.model;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private String filename;
    private List<String> lines;
    private long currentLine;
    private long startLine;
    private long finishLine;

    public DataModel() {
        filename = null;
        lines = new ArrayList<>();
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public long getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(long current_line) {
        this.currentLine = current_line;
    }

    public long getStartLine() {
        return startLine;
    }

    public void setStartLine(long startLine) {
        this.startLine = startLine;
    }

    public long getFinishLine() {
        return finishLine;
    }

    public void setFinishLine(long finishLine) {
        this.finishLine = finishLine;
    }
}
