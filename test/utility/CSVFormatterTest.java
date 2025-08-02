package utility;

import com.yandex.app.model.*;
import com.yandex.app.utility.CSVFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;

public class CSVFormatterTest {
    private final Task task = new Task("Task1", "TaskD", Status.NEW, 20, LocalDateTime.of(2025, 6, 22, 10, 30));
    private final Epic epic = new Epic("Epic1", "EpicD");
    private final Subtask subtask = new Subtask("Sub1", "SubD", Status.DONE, 2, 20, LocalDateTime.of(2025, 7, 22, 10, 30));


    @Test
    void toStringShouldBeInCorrectFormat() {
        task.setId(1);
        String taskToString = CSVFormatter.toString(task);
        Assertions.assertEquals("1,TASK,Task1,NEW,TaskD,2025-06-22T10:30,PT20M,", taskToString);

        epic.setId(2);
        String epicToString = CSVFormatter.toString(epic);
        Assertions.assertEquals("2,EPIC,Epic1,NEW,EpicD,-999999999-01-01T00:00,PT0S,", epicToString);

        subtask.setId(3);
        String subToString = CSVFormatter.toString(subtask);
        Assertions.assertEquals("3,SUBTASK,Sub1,DONE,SubD,2025-07-22T10:30,PT20M,2", subToString);

    }

    @Test
    void taskFromStringShouldBeInCorrectFormat() {


        task.setId(1);
        Task task1 = CSVFormatter.taskFromString("1,TASK,Task1,NEW,TaskD,2025-06-22T10:30,PT20M,");
        Assertions.assertEquals(task1, task);
        Assertions.assertEquals(task1.getName(), task.getName());
        Assertions.assertEquals(task1.getStatus(), task.getStatus());
        Assertions.assertEquals(task1.getDescription(), task.getDescription());
        Assertions.assertEquals(task1.getStartTime(), task.getStartTime());
        Assertions.assertEquals(task1.getDuration(), task.getDuration());
        Assertions.assertEquals(task1.getEndTime(), task.getEndTime());

        epic.setId(2);
        Epic epic1 = (Epic) CSVFormatter.taskFromString("2,EPIC,Epic1,NEW,EpicD,-999999999-01-01T00:00,PT0S,");
        Assertions.assertEquals(epic1, epic);
        Assertions.assertEquals(epic1.getName(), epic.getName());
        Assertions.assertEquals(epic1.getStatus(), epic.getStatus());
        Assertions.assertEquals(epic1.getDescription(), epic.getDescription());
        Assertions.assertEquals(epic1.getStartTime(), epic.getStartTime());
        Assertions.assertEquals(epic1.getDuration(), epic.getDuration());
        Assertions.assertEquals(epic1.getEndTime(), epic.getEndTime());

        subtask.setId(3);
        Subtask sub1 = (Subtask) CSVFormatter.taskFromString("3,SUBTASK,Sub1,DONE,SubD,2025-07-22T10:30,PT20M,2");
        Assertions.assertEquals(sub1, subtask);
        Assertions.assertEquals(sub1.getName(), subtask.getName());
        Assertions.assertEquals(sub1.getStatus(), subtask.getStatus());
        Assertions.assertEquals(sub1.getDescription(), subtask.getDescription());
        Assertions.assertEquals(sub1.getEpicId(), subtask.getEpicId());
        Assertions.assertEquals(sub1.getStartTime(), subtask.getStartTime());
        Assertions.assertEquals(sub1.getDuration(), subtask.getDuration());
        Assertions.assertEquals(sub1.getEndTime(), subtask.getEndTime());

    }
}
