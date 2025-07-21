package service;

import com.yandex.app.model.*;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public abstract class TaskManagerTest {
    protected TaskManager taskManager;

    protected abstract TaskManager getTaskManager();

    @BeforeEach
    void BeforeEach() {
        taskManager = getTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 10, 30));

        final int taskId = taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(taskId);

        Assertions.assertNotNull(savedTask, "Task not found");
        Assertions.assertEquals(task, savedTask, "Tasks do not match");

        final List<Task> tasks = (List<Task>) taskManager.getTaskList(TaskType.TASK);
        final List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        Assertions.assertNotNull(tasks, "Tasks are not returned");
        Assertions.assertEquals(1, tasks.size(), "Incorrect number of tasks");
        Assertions.assertEquals(task, tasks.getFirst(), "Tasks do not match");
        Assertions.assertEquals(task, prioritizedTasks.getFirst(), "Tasks do not match");
    }

    @Test
    void addNewSubtaskAndEpic() {
        Epic epic = new Epic("epic", "epicD");

        final int epicId = taskManager.createTask(epic);

        final Epic savedEpic = (Epic) taskManager.getTask(epicId);

        Assertions.assertNotNull(savedEpic, "Task not found");
        Assertions.assertEquals(epic, savedEpic, "Tasks do not match");

        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description", Status.NEW, epicId, 20, LocalDateTime.of(2025, 6, 22, 10, 30));

        final int subtaskId = taskManager.createTask(subtask);

        final Subtask savedSubtask = (Subtask) taskManager.getTask(subtaskId);

        Assertions.assertNotNull(savedSubtask, "Task not found");
        Assertions.assertEquals(subtask, savedSubtask, "Tasks do not match");
        Assertions.assertEquals(subtask.getStartTime(), epic.getStartTime(), "Incorrect epic startTime");
        Assertions.assertEquals(subtask.getEndTime(), epic.getEndTime(), "Incorrect epic endTime");
        Assertions.assertEquals(subtask.getDuration(), epic.getDuration(), "Incorrect epic duration");
    }

    @Test
    public void checkFieldImmutability() {
        Task task = new Task("Task", "Task description", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 10, 30));

        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        Assertions.assertEquals(task.getName(), savedTask.getName(), "Name doesn't match");
        Assertions.assertEquals(task.getDescription(), savedTask.getDescription(), "Description doesn't match");
        Assertions.assertEquals(task.getId(), savedTask.getId(), "ID doesn't match");
        Assertions.assertEquals(task.getStatus(), savedTask.getStatus(), "Status doesn't match");
        Assertions.assertEquals(task.getDuration(), savedTask.getDuration(), "Duration doesn't match");
        Assertions.assertEquals(task.getStartTime(), savedTask.getStartTime(), "StartTime doesn't match");
        Assertions.assertEquals(task.getEndTime(), savedTask.getEndTime(), "EndTime doesn't match");
    }

    @Test
    public void saveHistoryOldVersionTask() {
        Task task = new Task("Task", "Test addNewTask description", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 10, 30));
        final int taskId = taskManager.createTask(task);
        taskManager.getTask(taskId);
        taskManager.updateTask(new Task("Task2", "Test addNewTask description", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 10, 30)), taskId);
        Assertions.assertNotEquals(taskManager.getHistory().getFirst().getName(), taskManager.getTask(taskId).getName());
    }

    @Test
    void testEpicStatusCalculation() {
        Epic epic = new Epic("Epic", "EpicD");
        int epicId = taskManager.createTask(epic);
        Subtask sub1 = new Subtask("Sub1", "Sub1D", Status.NEW, epicId, 20, LocalDateTime.of(2025, 6, 22, 10, 30));
        Subtask sub2 = new Subtask("Sub2", "Sub2D", Status.NEW, epicId, 20, LocalDateTime.of(2025, 6, 22, 11, 30));

        taskManager.createTask(sub1);
        taskManager.createTask(sub2);
        //2 NEW
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "Epic status not NEW");

        sub1.setStatus(Status.DONE);
        taskManager.updateTask(sub1, sub1.getId());
        //1 DONE 1 NEW
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status not IN_PROGRESS");

        sub2.setStatus(Status.DONE);
        taskManager.updateTask(sub2, sub2.getId());
        //2 DONE
        Assertions.assertEquals(Status.DONE, epic.getStatus(), "Epic status not DONE");

        sub1.setStatus(Status.IN_PROGRESS);
        sub2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(sub1, sub1.getId());
        taskManager.updateTask(sub2, sub2.getId());
        //2 IN_PROGRESS
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status not IN_PROGRESS");

    }

    @Test
    void testIsOverLapping() {
        Task task1 = new Task("Sub1", "Sub1D", Status.NEW, 90, LocalDateTime.of(2025, 6, 22, 10, 30));
        Task task2 = new Task("Sub2", "Sub2D", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 11, 30));
        Task task3 = new Task("Sub2", "Sub2D", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 14, 30));

        taskManager.createTask(task1);
        Assertions.assertTrue(taskManager.isOverlapping(task2));
        Assertions.assertFalse(taskManager.isOverlapping(task3));
    }
}
