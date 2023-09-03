package ru.example.localetool.model.exception;

import java.io.IOException;

public class RecentFileNotFoundException extends IOException {
    public RecentFileNotFoundException(String message) {
        super(message);
    }
}
