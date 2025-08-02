package service;

import com.yandex.app.Exceptions.ManagerSaveException;
import com.yandex.app.model.*;
import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utility.CSVFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private File tempFile;


    @Override
    protected TaskManager getTaskManager() {
       try {
           tempFile = File.createTempFile("tasks", ".csv", new File("src/resources"));
           return FileBackedTaskManager.loadFromFile(tempFile);
       } catch (IOException e) {
           throw new ManagerSaveException("Failed create temp file");
       }
    }

    @AfterEach
    void AfterEach() {
        tempFile.deleteOnExit();
    }

    @Test
    void shouldLoadAndSaveEmptyFile() throws IOException {
        taskManager.createTask(new Task("Task", "Description", Status.NEW,20, LocalDateTime.of(2025, 6, 22, 10, 30)));
        taskManager.removeTask(1);

        String emptyFile = Files.readString(tempFile.toPath());
        emptyFile = emptyFile.trim();

        Assertions.assertEquals(emptyFile, CSVFormatter.getHeader());

        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertTrue(taskManager.getTaskList(TaskType.TASK).isEmpty());
        Assertions.assertTrue(taskManager.getTaskList(TaskType.SUBTASK).isEmpty());
        Assertions.assertTrue(taskManager.getTaskList(TaskType.EPIC).isEmpty());
        Assertions.assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldLoadAndSaveTask() throws IOException {
        taskManager.createTask(new Task("Task", "Description", Status.NEW,20, LocalDateTime.of(2025, 6, 22, 10, 30)));
        taskManager.createTask(new Epic("Epic", "epicD"));
        taskManager.createTask(new Subtask("Sub", "subD", Status.NEW, 2, 20, LocalDateTime.of(2025, 7, 22, 10, 30)));

        Task taskForTest = taskManager.getTask(1);
        Subtask subtaskForTest = (Subtask) taskManager.getTask(3);
        Epic epicForTest = (Epic) taskManager.getTask(2);


        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertFalse(taskManager.getTaskList(TaskType.TASK).isEmpty());
        Assertions.assertFalse(taskManager.getTaskList(TaskType.SUBTASK).isEmpty());
        Assertions.assertFalse(taskManager.getTaskList(TaskType.EPIC).isEmpty());

        Task taskForTest1 = taskManager.getTask(1);
        Subtask subtaskForTest1 = (Subtask) taskManager.getTask(3);
        Epic epicForTest1 = (Epic) taskManager.getTask(2);

        Assertions.assertEquals(taskForTest1, taskForTest);
        Assertions.assertEquals(taskForTest1.getName(), taskForTest.getName());
        Assertions.assertEquals(taskForTest1.getStatus(), taskForTest.getStatus());
        Assertions.assertEquals(taskForTest1.getDescription(), taskForTest.getDescription());
        Assertions.assertEquals(taskForTest1.getDuration(), taskForTest.getDuration());
        Assertions.assertEquals(taskForTest1.getStartTime(), taskForTest.getStartTime());
        Assertions.assertEquals(taskForTest1.getEndTime(),taskForTest.getEndTime());

        Assertions.assertEquals(epicForTest1, epicForTest);
        Assertions.assertEquals(epicForTest1.getName(), epicForTest.getName());
        Assertions.assertEquals(epicForTest1.getStatus(), epicForTest.getStatus());
        Assertions.assertEquals(epicForTest1.getDescription(), epicForTest.getDescription());
        //епик обновился, поэтому сравниваю с сабтасками
        Assertions.assertEquals(epicForTest1.getDuration(), subtaskForTest.getDuration());
        Assertions.assertEquals(epicForTest1.getEndTime(), subtaskForTest.getEndTime());
        Assertions.assertEquals(epicForTest1.getStartTime(), subtaskForTest.getStartTime());


        Assertions.assertEquals(subtaskForTest1, subtaskForTest);
        Assertions.assertEquals(subtaskForTest1.getName(), subtaskForTest.getName());
        Assertions.assertEquals(subtaskForTest1.getStatus(), subtaskForTest.getStatus());
        Assertions.assertEquals(subtaskForTest1.getDescription(), subtaskForTest.getDescription());
        Assertions.assertEquals(subtaskForTest1.getEpicId(), subtaskForTest.getEpicId());
        Assertions.assertEquals(subtaskForTest1.getDuration(), subtaskForTest.getDuration());
        Assertions.assertEquals(subtaskForTest1.getEndTime(), subtaskForTest.getEndTime());
        Assertions.assertEquals(subtaskForTest1.getStartTime(), subtaskForTest.getStartTime());

    }
    @Test
    void shouldThrowWhenTryingToLoadNonExistentFile() {
        //Не совсем понял что нужно проверять, какие случаи
        File notExists = new File("non-existent file.csv");
        Assertions.assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(notExists));
    }

}
