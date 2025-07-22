package com.yandex.app.service;

import com.yandex.app.Exceptions.ManagerSaveException;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import com.yandex.app.utility.CSVFormatter;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

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
            Arrays.stream(splitFile).skip(1)
                    .filter(line -> !(line == null || line.isEmpty() || line.isBlank()))
                    .map(CSVFormatter::taskFromString)
                    .forEach(taskManager::addTask);

            taskManager.tasksById.values()
                    .stream()
                    .filter(task -> task.getType() == TaskType.SUBTASK)
                    .map(task -> (Subtask) task)
                    .forEach(subtask -> {
                        Epic epic = (Epic) taskManager.tasksById.get(subtask.getEpicId());
                        if (epic == null) {
                            throw new IllegalArgumentException("Epic not found");
                        }
                        epic.addSubtask(subtask);
                    });

        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load: " + e.getMessage());
        }
        return taskManager;
    }

    @Override
    public Task getTask(int identifier) {
        Task task = super.getTask(identifier);
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
    public void updateTask(Task task, int identifier) {
        super.updateTask(task, identifier);
        save();
    }

    @Override
    public void removeTask(int identifier) {
        super.removeTask(identifier);
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

            tasksById.values()
                    .stream()
                    .map(CSVFormatter::toString)
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException("Line writing error" + line, e);
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to save: " + e.getMessage());
        }
    }

    private void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getId() > identifier) {
            identifier = task.getId();
        }
        tasksById.put(task.getId(), task);
        prioritizedTasks.add(task);

    }
}
