package com.yandex.app.Exceptions;

public class TaskOverLappingException extends ManagerSaveException {
    public TaskOverLappingException(String message) {
        super(message);
    }
}
