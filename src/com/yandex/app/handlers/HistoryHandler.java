package com.yandex.app.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.Exceptions.RequestFormatException;
import com.yandex.app.service.TaskManager;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        try {
            if (method.equals(GET)) {
                handleGetHistory(exchange);
            } else {
                throw new RequestFormatException("Unknown method");
            }
        } catch (RequestFormatException e) {
            writeResponse(exchange, e.getMessage(), 405);
        }
    }

    private void handleGetHistory(HttpExchange exchange) {
        String response = gson.toJson(taskManager.getHistory());
        writeResponse(exchange, response, 200);
    }
}
