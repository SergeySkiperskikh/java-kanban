package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.app.HttpTaskServer;
import com.yandex.app.model.TaskType;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utility.DurationTypeAdapter;
import com.yandex.app.utility.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskManagersTest {
    protected static HttpTaskServer server;
    protected static HttpClient client;
    protected static TaskManager manager;
    protected static Gson gson;
    protected static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    protected static void beforeAll() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    @BeforeEach
    protected void beforeEach() throws IOException {
        server.start();
    }

    @AfterEach
    protected void afterEach() {
        server.stop();
        manager.removeTasksMap(TaskType.TASK);
        manager.removeTasksMap(TaskType.EPIC);
        InMemoryTaskManager.resetID();
    }
}
