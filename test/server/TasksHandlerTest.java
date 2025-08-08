package server;

import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandlerTest extends HttpTaskManagersTest {
    private static final TaskType TASK_TYPE = TaskType.TASK;
    private static final String PATH = "/tasks";
    private static Task task1;
    private static Task task2;

    @BeforeEach
    void refreshTasks() {
        task1 = new Task("Task1", "Task1D",
                Status.NEW, 32, LocalDateTime.of(2025, 6, 22, 10, 30));
        task2 = new Task("Task2", "Task2D",
                Status.NEW, 32, LocalDateTime.of(2025, 6, 22, 10, 32));
    }
    @Test
    void testAddTask() throws IOException, InterruptedException {
        String task1Json = gson.toJson(task1);
        String task2Json = gson.toJson(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = (List<Task>) manager.getTaskList(TASK_TYPE);
        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        manager.createTask(task1);
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
        task1.setId(1);
        String task1Json = gson.toJson(task1);
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
    void testRemoveTask() throws IOException, InterruptedException {
        manager.createTask(task1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(manager.getTaskList(TASK_TYPE).isEmpty());
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        manager.createTask(task1);
        String task2Json = gson.toJson(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH + "/1"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(manager.getTask(1).getName(), task2.getName());
    }
}
