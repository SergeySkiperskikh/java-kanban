package com.yandex.app.handlers;

import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import com.yandex.app.service.TaskManager;

public class SubtasksHandler extends TasksHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected Task taskFromJson(String body) {
        return gson.fromJson(body, Subtask.class);
    }

    @Override
    protected String taskListToJson() {
        return gson.toJson(taskManager.getTaskList(TaskType.SUBTASK));
    }

}
