package service;

import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Task1", "des", Status.NEW,20, LocalDateTime.of(2025, 6, 22, 10, 30));
        task2 = new Task("Task2", "des", Status.NEW,20, LocalDateTime.of(2025, 3, 22, 10, 30));
        task1.setId(1);
        task2.setId(2);
    }

    @Test
    void addHistory() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history);
        Assertions.assertEquals(1, history.size());

    }

    @Test
    void notHaveDuplicates() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task2, history.get(0));
        Assertions.assertEquals(task1, history.get(1));
    }

    @Test
    void removeTask() {
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        historyManager.remove(task1.getId());
        history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());

    }
}
