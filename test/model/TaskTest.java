package model;

import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class TaskTest {
    @Test
    public void ifIdEqualThenTaskEqual() {
        Task task1 = new Task("task1", "task1", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 10, 30));
        task1.setId(1);
        Task task2 = new Task("task2", "task2", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 12, 30));
        task2.setId(1);
        Assertions.assertEquals(task1, task2);

    }
}
