package server;

import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class HistoryHandlerTest extends HttpTaskManagersTest {
    private final static String PATH = "/history";

    @Test
    void testHistory() throws Exception {
        Task task1 = new Task("Task1", "TaskD",
                Status.NEW, 32, LocalDateTime.of(2025, 6, 22, 10, 30));
        Task task2 = new Task("Task2", "TaskD",
                Status.NEW, 32, LocalDateTime.of(2025, 7, 22, 10, 30));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.getTask(1);
        manager.getTask(2);

        HttpRequest historyRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH))
                .GET()
                .build();

        HttpResponse<String> historyResponse = client.send(historyRequest, HttpResponse.BodyHandlers.ofString());

        int indexTask2 = historyResponse.body().indexOf("Task2");
        int indexTask1 = historyResponse.body().indexOf("Task1");

        Assertions.assertEquals(200, historyResponse.statusCode());
        Assertions.assertTrue(historyResponse.body().contains("Task1"));
        Assertions.assertTrue(historyResponse.body().contains("Task2"));
        Assertions.assertTrue(indexTask1 < indexTask2);
    }
}
