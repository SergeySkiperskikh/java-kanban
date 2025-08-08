package com.yandex.app.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.Exceptions.ManagerSaveException;
import com.yandex.app.Exceptions.RequestFormatException;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import com.yandex.app.service.TaskManager;

public class EpicsHandler extends TasksHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGetTasks(HttpExchange exchange, String[] splitPath) throws RequestFormatException, ManagerSaveException {
        checkRequestValidity(splitPath, 2, 4);
        String response;
        if (splitPath.length == 2) {
            response = taskListToJson();
        } else if (splitPath.length == 3) {
            int taskId = parseTaskId(splitPath[2]);
            response = gson.toJson(taskManager.getTask(taskId));
        } else {
            int taskId = parseTaskId(splitPath[2]);
            response = gson.toJson(taskManager.getSubtaskList(taskId));
        }
        writeResponse(exchange, response, 200);
    }

    @Override
    protected Task taskFromJson(String body) {
        return gson.fromJson(body, Epic.class);
    }

    @Override
    protected String taskListToJson() {
        return gson.toJson(taskManager.getTaskList(TaskType.EPIC));
    }

    @Override
    protected void checkRequestValidity(String[] splitPath, int minLength, int maxLength) throws RequestFormatException {
        super.checkRequestValidity(splitPath, minLength, maxLength);
        if (splitPath.length == 4) {
            if(!splitPath[3].equals("subtasks")) {
                throw new RequestFormatException("Invalid path format");
            }
        }
    }
}
