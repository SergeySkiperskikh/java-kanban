package com.yandex.app.service;

import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;

import java.util.List;

public interface TaskManager {
    List<? extends Task> getTaskList(TaskType task);

    void removeTasksMap(TaskType taskType);

    int createTask(Task task);

    Task getTask(int identifier);

    void removeTask(int identifier);

    void updateTask(Task task, int identifier);

    void checkEpicStatus(int epicId);

    List<Subtask> getSubtaskList(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean isOverlapping(Task task);
}
