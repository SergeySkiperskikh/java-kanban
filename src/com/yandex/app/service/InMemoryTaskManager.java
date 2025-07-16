package com.yandex.app.service;

import com.yandex.app.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager history;

    protected static int id = 0;

    public InMemoryTaskManager() {
        history = Managers.getDefaultHistory();
    }


    @Override
    public List<? extends Task> getTaskList(TaskType task) {
        return switch (task) {
            case EPIC -> new ArrayList<>(epics.values());
            case SUBTASK -> new ArrayList<>(subtasks.values());
            case TASK -> new ArrayList<>(tasks.values());
        };
    }

    @Override
    public void removeTasksMap(TaskType taskType) { // проверить
        switch (taskType) {
            case EPIC -> {
                epics.clear();
                subtasks.clear();
            }
            case SUBTASK -> {
                subtasks.clear();
                for (Epic epic : epics.values()) {
                    epic.getSubtasksId().clear();
                    checkEpicStatus(epic.getId());
                }

            }
            case TASK -> tasks.clear();
        }
    }

    @Override
    public int createTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        task.setId(++id);
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            epics.put(id, epic);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            Epic epic = epics.get(subtask.getEpicId());
            if (epic == null) {
                throw new IllegalArgumentException("Epic not found");
            }
            subtasks.put(id, subtask);
            epic.addSubtask(id);
            checkEpicStatus(epic.getId());
        } else {
            tasks.put(id, task);
        }
        return id;
    }

    @Override
    public Task getTask(TaskType taskType, int identifier) {
        Task task = switch (taskType) {
            case EPIC -> epics.get(identifier);
            case TASK -> tasks.get(identifier);
            case SUBTASK -> subtasks.get(identifier);
        };
        history.add(task);
        return task;
    }

    @Override
    public void removeTask(TaskType taskType, int identifier) {
        switch (taskType) {
            case EPIC -> {
                for (Integer subId : epics.get(id).getSubtasksId()) {
                    subtasks.remove(subId);
                }
                epics.remove(identifier);
            }
            case TASK -> tasks.remove(identifier);
            case SUBTASK -> {
                int epicId = subtasks.get(identifier).getEpicId();
                subtasks.remove(identifier);
                epics.get(epicId).removeSubtask(identifier);
                checkEpicStatus(epicId);
            }
        }
        history.remove(identifier);

    }

    @Override
    public void updateTask(Task task, TaskType taskType, int identifier) {
        task.setId(identifier);
        switch (taskType) {
            case EPIC -> {
                ((Epic) task).setSubtasksId(epics.get(identifier).getSubtasksId());
                epics.put(identifier, (Epic) task);
                checkEpicStatus(identifier);
            }
            case TASK -> {
                tasks.put(identifier, task);
            }
            case SUBTASK -> {
                Subtask oldTask = subtasks.get(identifier);
                ((Subtask) task).setEpicId(oldTask.getEpicId());
                subtasks.put(identifier, (Subtask) task);
                checkEpicStatus(((Subtask) task).getEpicId());
            }

        }
    }


    @Override
    public void checkEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        Status epicStatus = Status.IN_PROGRESS;
        boolean epicStatusIsDone = false;
        boolean epicStatusIsNew = false;

        for (Subtask sub : getSubtaskList(epicId)) {
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

    @Override
    public List<Subtask> getSubtaskList(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subId : epics.get(epicId).getSubtasksId()) {
            epicSubtasks.add(subtasks.get(subId));
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> his = history.getHistory();
        System.out.println(his);
        return his;
    }

    public static void  resetID() {
        id = 0;
    }
}
