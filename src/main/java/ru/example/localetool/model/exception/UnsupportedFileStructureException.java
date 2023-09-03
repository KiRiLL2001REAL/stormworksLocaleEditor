package ru.example.localetool.model.exception;

import java.io.IOException;

public class UnsupportedFileStructureException extends IOException {
    public UnsupportedFileStructureException(String message) {
        super(message);
    }
}
