package com.yandex.app.service;

import com.yandex.app.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class TaskManager {
    private static final Map<Integer, Task> tasks = new HashMap<>();
    private static final Map<Integer, Subtask> subtasks = new HashMap<>();
    private static final Map<Integer, Epic> epics = new HashMap<>();

    private static TaskManager instance;

    private static int id = 0;

    private TaskManager() {
    }

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    protected List<? extends Task> getTaskList(TaskType task) {
        return switch (task) {
            case EPIC -> new ArrayList<>(epics.values());
            case SUBTASK -> new ArrayList<>(subtasks.values());
            case TASK -> new ArrayList<>(tasks.values());
        };
    }

    protected void removeTasksMap(TaskType taskType) { // проверить
        switch (taskType) {
            case EPIC -> {
                epics.clear();
                subtasks.clear();
            }
            case SUBTASK -> {
                subtasks.clear();
                for (Epic epic : epics.values()) {
                    epic.getSubtasksList().clear();
                    checkEpicStatus(epic.getId());
                }

            }
            case TASK -> tasks.clear();
        }
    }

    protected void createTask(Task task, TaskType taskType) {
        task.setId(++id);
        switch (taskType) {
            case EPIC -> epics.put(id, (Epic) task);
            case TASK -> tasks.put(id, task);
            case SUBTASK -> {
                subtasks.put(id, (Subtask) task);
                epics.get(((Subtask) task).getEpicId()).addSubtask((Subtask) task);
                checkEpicStatus(((Subtask) task).getEpicId());
            }

        }
    }

    protected Task getTask(TaskType taskType, int identifier) {
        return switch (taskType) {
            case EPIC -> epics.get(identifier);
            case TASK -> tasks.get(identifier);
            case SUBTASK -> subtasks.get(identifier);
        };
    }

    protected void removeTask(TaskType taskType, int identifier) {
        switch (taskType) {
            case EPIC -> {
                for (Subtask sub : epics.get(id).getSubtasksList()) {
                    subtasks.remove(sub);
                }
                epics.remove(identifier);
            }
            case TASK -> tasks.remove(identifier);
            case SUBTASK -> {
                int epicId = subtasks.get(identifier).getEpicId();
                subtasks.remove(identifier);
                checkEpicStatus(epicId);
            }
        }
    }

    protected void updateTask(Task task, TaskType taskType, int identifier) {
        task.setId(identifier);
        switch (taskType) {
            case EPIC -> {
                ((Epic) task).setSubtasksList(epics.get(identifier).getSubtasksList());
                // По другому я не знаю как сделать, сохранив условие что задача должна передваться в метод
                epics.put(identifier, (Epic) task);
                checkEpicStatus(identifier);
            }
            case TASK -> {
                tasks.put(identifier, task);
            }
            case SUBTASK -> { // здесь, кажется, тоже что то намудрил
                Subtask oldTask = subtasks.get(identifier);
                List<Subtask> subtaskList = epics.get(oldTask.getEpicId()).getSubtasksList();
                int subtaskListIndex = subtaskList.indexOf(oldTask);
                ((Subtask) task).setEpicId(oldTask.getEpicId());
                subtasks.put(identifier, (Subtask) task);
                epics.get(((Subtask) task).getEpicId()).getSubtasksList().set(subtaskListIndex, (Subtask) task);
                checkEpicStatus(((Subtask) task).getEpicId());
            }

        }
    }


    protected void checkEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubtasksList().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        Status epicStatus = Status.IN_PROGRESS;
        boolean epicStatusIsDone = false;
        boolean epicStatusIsNew = false;

        for (Subtask sub : epic.getSubtasksList()) {
            if (sub.getStatus().equals(Status.DONE)) {
                epicStatusIsDone = true;
            }
            if (sub.getStatus().equals(Status.NEW)) {
                epicStatusIsNew = true;
            }
        }
        if (epicStatusIsDone && !epicStatusIsNew) {
            epicStatus = Status.DONE;
        }

        if (!epicStatusIsDone && epicStatusIsNew) {
            epicStatus = Status.NEW;
        }

        epic.setStatus(epicStatus);
    }

    protected List<Subtask> getSubtaskList(int epicId) {
        return epics.get(epicId).getSubtasksList();
    }
}
