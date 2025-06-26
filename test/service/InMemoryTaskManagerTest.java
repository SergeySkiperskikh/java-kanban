package service;

import com.yandex.app.model.*;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void BeforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);

        final int taskId = taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(TaskType.TASK, taskId);

        Assertions.assertNotNull(savedTask, "Task not found");
        Assertions.assertEquals(task, savedTask, "Tasks do not match");

        final List<Task> tasks = (List<Task>) taskManager.getTaskList(TaskType.TASK);

        Assertions.assertNotNull(tasks, "Tasks are not returned");
        Assertions.assertEquals(1, tasks.size(), "Incorrect number of tasks");
        Assertions.assertEquals(task, tasks.get(0), "Tasks do not match");
    }

    @Test
    void addNewSubtaskAndEpic() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");

        final int epicId = taskManager.createTask(epic);

        final Epic savedEpic = (Epic) taskManager.getTask(TaskType.EPIC, epicId);

        Assertions.assertNotNull(savedEpic, "Task not found");
        Assertions.assertEquals(epic, savedEpic, "Tasks do not match");

        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description", Status.NEW, epicId);

        final int subtaskId = taskManager.createTask(subtask);

        final Subtask savedSubtask = (Subtask) taskManager.getTask(TaskType.SUBTASK, subtaskId);

        Assertions.assertNotNull(savedSubtask, "Task not found");
        Assertions.assertEquals(subtask, savedSubtask, "Tasks do not match");
    }

    @Test
    public void checkFieldImmutability() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);

        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(TaskType.TASK, taskId);

        Assertions.assertEquals(task.getName(), savedTask.getName(), "Name doesn't match");
        Assertions.assertEquals(task.getDescription(), savedTask.getDescription(), "Description doesn't match");
        Assertions.assertEquals(task.getId(), savedTask.getId(), "ID doesn't match");
    }

    @Test
    public void saveHistoryOldVersionTask() {
        Task task = new Task("Task", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.createTask(task);
        taskManager.getTask(TaskType.TASK, taskId);
        taskManager.updateTask(new Task("Task2", "Test addNewTask description", Status.NEW), TaskType.TASK, taskId);
        Assertions.assertNotEquals(taskManager.getHistory().get(0).getName(), taskManager.getTask(TaskType.TASK, taskId).getName());

    }
}
