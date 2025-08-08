package server;

import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class PrioritizedHandlerTest extends HttpTaskManagersTest {
    private final static String PATH = "/prioritized";

    @Test
    void testPrioritized() throws Exception {
        Task task1 = new Task("Task1", "TaskD",
                Status.NEW, 32, LocalDateTime.of(2025, 7, 22, 10, 30));
        Task task2 = new Task("Task2", "TaskD",
                Status.NEW, 32, LocalDateTime.of(2025, 6, 22, 10, 30));

        manager.createTask(task1);
        manager.createTask(task2);

        HttpRequest prioritizedRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .GET()
                .build();

        HttpResponse<String> prioritizedResponse = client.send(prioritizedRequest, HttpResponse.BodyHandlers.ofString());

        int indexTask2 = prioritizedResponse.body().indexOf("Task2");
        int indexTask1 = prioritizedResponse.body().indexOf("Task1");

        Assertions.assertEquals(200, prioritizedResponse.statusCode());
        Assertions.assertTrue(indexTask1 > indexTask2);
    }
}
