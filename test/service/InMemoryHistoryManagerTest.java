package service;

import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void addHistory() {
        historyManager.add(new Task("name", "des", Status.NEW));
        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history);
        Assertions.assertEquals(1, history.size());

    }
}
