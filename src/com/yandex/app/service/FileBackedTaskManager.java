package com.yandex.app.service;

import com.yandex.app.Exceptions.ManagerSaveException;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import com.yandex.app.utility.CSVFormatter;

import java.io.*;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    private FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            String loaderFile = Files.readString(file.toPath());
            String[] splitFile = loaderFile.split(System.lineSeparator());

            for (int i = 1; i < splitFile.length; i++) {
                String line = splitFile[i];
                if (line == null || line.isEmpty() || line.isBlank()) {
                    continue;
                }
                Task task = CSVFormatter.taskFromString(line);
                if (task.getId() > id) {
                    id = task.getId();
                }
                taskManager.addTask(task);
            }

            if (!taskManager.subtasks.isEmpty()) {
                for (Subtask subtask : taskManager.subtasks.values()) {
                    Epic epic = taskManager.epics.get(subtask.getEpicId());
                    if (epic == null) {
                        throw new IllegalArgumentException("Epic not found");
                    }
                    epic.addSubtask(id);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load: " + e.getMessage());
        }
        return taskManager;
    }

    @Override
    public Task getTask(TaskType taskType, int identifier) {
        Task task = super.getTask(taskType, identifier);
        save();
        return task;
    }

    @Override
    public int createTask(Task task) {
        int createdTaskId = super.createTask(task);
        save();
        return createdTaskId;
    }

    @Override
    public void updateTask(Task task, TaskType taskType, int identifier) {
        super.updateTask(task, taskType, identifier);
        save();
    }

    @Override
    public void removeTask(TaskType taskType, int identifier) {
        super.removeTask(taskType, identifier);
        save();
    }

    @Override
    public void removeTasksMap(TaskType taskType) {
        super.removeTasksMap(taskType);
        save();
    }

    private void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFormatter.getHeader());
            writer.newLine();

            if (!tasks.isEmpty()) {
                for (Task task : tasks.values()) {
                    writer.write(CSVFormatter.toString(task, TaskType.TASK));
                    writer.newLine();
                }
            }
            if (!epics.isEmpty()) {
                for (Task task : epics.values()) {
                    writer.write(CSVFormatter.toString(task, TaskType.EPIC));
                    writer.newLine();
                }
            }
            if (!subtasks.isEmpty()) {
                for (Task task : subtasks.values()) {
                    writer.write(CSVFormatter.toString(task, TaskType.SUBTASK));
                    writer.newLine();
                }
            }
            writer.newLine();
            // writer.write(CSVFormatter.toString(history));
            // writer.newLine();

        } catch (IOException e) {
            throw new ManagerSaveException("Failed to save: " + e.getMessage());
        }
    }

    private void addTask(Task task) {
        // Отедльный метод, который копирует метод создания таски, но здесь не меняется ID
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            epics.put(epic.getId(), epic);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);
        } else {
            tasks.put(task.getId(), task);
        }
    }
}
