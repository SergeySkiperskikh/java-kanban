package model;

import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void ifIdEqualThenTaskEqual() {
        Task task1 = new Task("task1", "task1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("task2", "task2", Status.NEW);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);

    }
}
