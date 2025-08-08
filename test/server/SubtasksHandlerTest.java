package server;

import com.yandex.app.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class SubtasksHandlerTest  extends  HttpTaskManagersTest {
    private final static TaskType TASK_TYPE = TaskType.SUBTASK;
    private final static String PATH = "/subtasks";

    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeEach
    void refreshTasks() {
        subtask1 = new Subtask("Task1", "TaskD1",
                Status.NEW, 1, 32, LocalDateTime.of(2025, 6, 22, 10,
                30));
        subtask2 = new Subtask("Task2", "Task2D",
                Status.NEW, 1, 32, LocalDateTime.of(2025, 7, 22, 10,
                32));
        manager.createTask(new Epic("Epic", "EpicD"));
    }

    @Test
    protected void testAddSubtask() throws IOException, InterruptedException {
        String task1Json = gson.toJson(subtask1);
        String task2Json = gson.toJson(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = (List<Task>) manager.getTaskList(TASK_TYPE);
        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    protected void testGetSubtask() throws IOException, InterruptedException {
        manager.createTask(subtask1);
        // Запрашиваем список задач
        HttpRequest listRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .GET()
                .build();

        HttpResponse<String> listResponse = client.send(listRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, listResponse.statusCode());
        List<Task> tasksFromManager = (List<Task>) manager.getTaskList(TASK_TYPE);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        // Запрашиваем конкретную задачу
        subtask1.setId(1);
        String task1Json = gson.toJson(subtask1);
        HttpRequest singleRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/2"))
                .GET()
                .build();

        HttpResponse<String> singleResponse = client.send(singleRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, singleResponse.statusCode());
        Assertions.assertEquals(singleResponse.body(), task1Json);

        HttpRequest incorrectSingleRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/3"))
                .GET()
                .build();

        singleResponse = client.send(incorrectSingleRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, singleResponse.statusCode());
    }

    @Test
    protected void testRemoveSubtask() throws IOException, InterruptedException {
        manager.createTask(subtask1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(manager.getTaskList(TASK_TYPE).isEmpty());
    }

    @Test
    protected void testUpdateSubtask() throws IOException, InterruptedException {
        manager.createTask(subtask1);
        String task2Json = gson.toJson(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/2"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(manager.getTask(2).getName(), subtask2.getName());
    }
}
