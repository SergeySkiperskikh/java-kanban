package com.yandex.app.service;

import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;

import java.util.List;

public interface TaskManager {
    List<? extends Task> getTaskList(TaskType task);

    void removeTasksMap(TaskType taskType);

    int createTask(Task task);

    Task getTask(TaskType taskType, int identifier);

    void removeTask(TaskType taskType, int identifier);

    void updateTask(Task task, TaskType taskType, int identifier);

    void checkEpicStatus(int epicId);

    List<Subtask> getSubtaskList(int epicId);

    List<Task> getHistory();


}
