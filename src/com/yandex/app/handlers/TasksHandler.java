package com.yandex.app.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.Exceptions.ManagerSaveException;
import com.yandex.app.Exceptions.RequestFormatException;
import com.yandex.app.Exceptions.TaskNotFoundException;
import com.yandex.app.Exceptions.TaskOverLappingException;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split(DELIMITER);

        try {
            switch (method) {
                case GET -> handleGetTasks(exchange, splitPath);
                case POST -> handlePostTasks(exchange, splitPath);
                case DELETE -> handleDeleteTasks(exchange, splitPath);
                default -> throw new RequestFormatException("Unknown method");
            }
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (TaskOverLappingException e) {
            writeResponse(exchange, e.getMessage(), 406);
        } catch (RequestFormatException e) {
            writeResponse(exchange, e.getMessage(), 405);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    protected void handleGetTasks(HttpExchange exchange, String[] splitPath) throws RequestFormatException, ManagerSaveException {
        checkRequestValidity(splitPath, 2, 3);
        String response;
        if (splitPath.length == 2) {
            response = taskListToJson();
        } else {
            int taskId = parseTaskId(splitPath[2]);
            response = gson.toJson(taskManager.getTask(taskId));
        }
        writeResponse(exchange, response, 200);
    }

    protected void handlePostTasks(HttpExchange exchange, String[] splitPath) throws ManagerSaveException, RequestFormatException {
        checkRequestValidity(splitPath, 2, 3);
        InputStream inputStream = exchange.getRequestBody();
        String body;
        try {
            body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        Task task = taskFromJson(body);
        String response;

        if (splitPath.length == 2) {
            taskManager.createTask(task);
            response = "The task was created successfully";
        } else {
            int taskId = parseTaskId(splitPath[2]);
            taskManager.updateTask(task, taskId);
            response = "Task updated successfully";
        }
        writeResponse(exchange, response, 201);
    }

    protected void handleDeleteTasks(HttpExchange exchange, String[] splitPath) throws ManagerSaveException, RequestFormatException {
        checkRequestValidity(splitPath, 3, 3);
        int taskId = parseTaskId(splitPath[2]);
        taskManager.removeTask(taskId);
        writeResponse(exchange, "The task was deleted", 200);
    }

    protected void checkRequestValidity(String[] splitPath, int minLength, int maxLength) throws RequestFormatException {
        if (splitPath.length < minLength || splitPath.length > maxLength) {
            throw new RequestFormatException("Invalid path format");
        }
    }
    // Эти два метода сделал для исключения дублирования в наследниках.
    // Изначально думал просто оставить один класс TasksHandler, считывая запрос и передавая нужный Enum.
    // Не знаю как лучше, поэтому разделил на 3 обработчика как написано в ТЗ
    protected Task taskFromJson(String body) {
        return gson.fromJson(body, Task.class);
    }

    protected String taskListToJson() {
        return gson.toJson(taskManager.getTaskList(TaskType.TASK));
    }

    protected int parseTaskId(String id) throws RequestFormatException {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new RequestFormatException("Invalid task ID format");
        }
    }
}
