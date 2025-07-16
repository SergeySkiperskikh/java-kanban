package service;

import com.yandex.app.model.*;
import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utility.CSVFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager taskManager;

    // Не уверен что правильно тут все сделал, больше времени на тесты потратил, чем на ТЗ
    @BeforeEach
    void BeforeEach() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv", new File("src/resources"));
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @AfterEach
    void AfterEach() {
        tempFile.deleteOnExit();
    }

    @Test
    void shouldLoadAndSaveEmptyFile() throws IOException {
        taskManager.createTask(new Task("Task", "Description", Status.NEW));
        taskManager.removeTask(TaskType.TASK, 1);

        String emptyFile = Files.readString(tempFile.toPath());
        emptyFile = emptyFile.trim();

        Assertions.assertEquals(emptyFile, CSVFormatter.getHeader());

        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertTrue(taskManager.getTaskList(TaskType.TASK).isEmpty());
        Assertions.assertTrue(taskManager.getTaskList(TaskType.SUBTASK).isEmpty());
        Assertions.assertTrue(taskManager.getTaskList(TaskType.EPIC).isEmpty());
    }

    @Test
    void shouldLoadAndSaveTask() throws IOException {
        FileBackedTaskManager.resetID();
        taskManager.createTask(new Task("Task", "TaskD", Status.NEW));
        taskManager.createTask(new Epic("Epic", "epicD"));
        taskManager.createTask(new Subtask("Sub", "subD", Status.NEW, 2));

        Task taskForTest = taskManager.getTask(TaskType.TASK, 1);
        Subtask subtaskForTest = (Subtask) taskManager.getTask(TaskType.SUBTASK, 3);
        Epic epicForTest = (Epic) taskManager.getTask(TaskType.EPIC, 2);


        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertFalse(taskManager.getTaskList(TaskType.TASK).isEmpty());
        Assertions.assertFalse(taskManager.getTaskList(TaskType.SUBTASK).isEmpty());
        Assertions.assertFalse(taskManager.getTaskList(TaskType.EPIC).isEmpty());

        Task taskForTest1 = taskManager.getTask(TaskType.TASK, 1);
        Subtask subtaskForTest1 = (Subtask) taskManager.getTask(TaskType.SUBTASK, 3);
        Epic epicForTest1 = (Epic) taskManager.getTask(TaskType.EPIC, 2);

        Assertions.assertEquals(taskForTest1, taskForTest);
        Assertions.assertEquals(taskForTest1.getName(), taskForTest.getName());
        Assertions.assertEquals(taskForTest1.getStatus(), taskForTest.getStatus());
        Assertions.assertEquals(taskForTest1.getDescription(), taskForTest.getDescription());

        Assertions.assertEquals(epicForTest1, epicForTest);
        Assertions.assertEquals(epicForTest1.getName(), epicForTest.getName());
        Assertions.assertEquals(epicForTest1.getStatus(), epicForTest.getStatus());
        Assertions.assertEquals(epicForTest1.getDescription(), epicForTest.getDescription());

        Assertions.assertEquals(subtaskForTest1, subtaskForTest);
        Assertions.assertEquals(subtaskForTest1.getName(), subtaskForTest.getName());
        Assertions.assertEquals(subtaskForTest1.getStatus(), subtaskForTest.getStatus());
        Assertions.assertEquals(subtaskForTest1.getDescription(), subtaskForTest.getDescription());
        Assertions.assertEquals(subtaskForTest1.getEpicId(), subtaskForTest.getEpicId());

    }

}
