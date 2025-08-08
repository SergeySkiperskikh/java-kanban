package server;

import com.yandex.app.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class EpicsHandlerTest extends HttpTaskManagersTest {
    protected static final TaskType TASK_TYPE = TaskType.EPIC;
    protected static final String PATH = "/epics";

    private static Epic epic1;
    private static Epic epic2;

    @BeforeEach
    void refreshTasks() {
        epic1 = new Epic("Epic1", "EpicD");
        epic2 = new Epic("Epic2", "EpicD");
    }

    @Test
    protected void testAddEpic() throws IOException, InterruptedException {
        String epic1Json = gson.toJson(epic1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .POST(HttpRequest.BodyPublishers.ofString(epic1Json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = (List<Task>) manager.getTaskList(TASK_TYPE);
        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Epic1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    protected void testGetEpic() throws IOException, InterruptedException {
        manager.createTask(epic1);
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
        Assertions.assertEquals("Epic1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        // Запрашиваем конкретную задачу
        epic1.setId(1);
        String task1Json = gson.toJson(epic1);
        HttpRequest singleRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/1"))
                .GET()
                .build();

        HttpResponse<String> singleResponse = client.send(singleRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, singleResponse.statusCode());
        Assertions.assertEquals(singleResponse.body(), task1Json);

        HttpRequest incorrectSingleRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/2"))
                .GET()
                .build();

        singleResponse = client.send(incorrectSingleRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, singleResponse.statusCode());
    }

    @Test
    protected void testRemoveEpic() throws IOException, InterruptedException {
        manager.createTask(epic1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(manager.getTaskList(TASK_TYPE).isEmpty());
    }

    @Test
    protected void testUpdateEpic() throws IOException, InterruptedException {
        manager.createTask(epic1);
        String task2Json = gson.toJson(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/1"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(manager.getTask(1).getName(), epic2.getName());
    }

    @Test
    protected void testGetEpicSubtasks() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Task1", "TaskD1",
                Status.NEW, 1, 32, LocalDateTime.of(2025, 6, 22, 10,
                30));
        Subtask subtask2 = new Subtask("Task2", "Task2D",
                Status.NEW, 1, 32, LocalDateTime.of(2025, 7, 22, 10,
                32));
        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.createTask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/1" + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Integer> subtasksID = gson.fromJson(response.body(), List.class);
        Assertions.assertEquals(2, subtasksID.size());
    }
}
