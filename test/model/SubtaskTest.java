package model;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class SubtaskTest {
    @Test
    public void ifIdEqualThenSubtaskEqual() {
        Epic epic = new Epic("епик", "епик");
        epic.setId(1);
        Subtask sub1 = new Subtask("sub1", "sub1", Status.NEW, 1, 20, LocalDateTime.of(2025, 6, 22, 10, 30));
        sub1.setId(2);
        Subtask sub2 = new Subtask("sub2", "sub2", Status.NEW, 1, 20, LocalDateTime.of(2025, 7, 22, 10, 30));
        sub2.setId(2);
        Assertions.assertEquals(sub1, sub2);

    }
}
