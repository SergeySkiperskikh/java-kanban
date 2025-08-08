package com.yandex.app.Exceptions;

public class TaskNotFoundException extends ManagerSaveException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
