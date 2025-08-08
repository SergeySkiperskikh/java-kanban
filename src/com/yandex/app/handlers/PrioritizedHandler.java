package com.yandex.app.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.Exceptions.RequestFormatException;
import com.yandex.app.service.TaskManager;

public class PrioritizedHandler extends  BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        try {
            if (method.equals(GET)) {
                handleGetPrioritized(exchange);
            } else {
                throw new RequestFormatException("Unknown method");
            }
        } catch (RequestFormatException e) {
            writeResponse(exchange, e.getMessage(), 405);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        writeResponse(exchange, response, 200);
    }
}

