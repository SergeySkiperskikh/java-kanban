package utility;

import com.yandex.app.model.*;
import com.yandex.app.utility.CSVFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class CSVFormatterTest {
    private final Task task = new Task("Task1", "TaskD", Status.NEW);
    private final Epic epic = new Epic("Epic1", "EpicD");
    private final Subtask subtask = new Subtask("Sub1", "SubD", Status.DONE, 2);


    @Test
    void toStringShouldBeInCorrectFormat() {
        task.setId(1);
        String taskToString = CSVFormatter.toString(task, TaskType.TASK);
        Assertions.assertEquals("1,TASK,Task1,NEW,TaskD,", taskToString);

        epic.setId(2);
        String epicToString = CSVFormatter.toString(epic, TaskType.EPIC);
        Assertions.assertEquals("2,EPIC,Epic1,NEW,EpicD,", epicToString);

        subtask.setId(3);
        String subToString = CSVFormatter.toString(subtask, TaskType.SUBTASK);
        Assertions.assertEquals("3,SUBTASK,Sub1,DONE,SubD,2", subToString);

    }

    @Test
    void taskFromStringShouldBeInCorrectFormat() {
        //так сдеалал потому что equals переопредлен на ID, по другому не додумался как проще проверить

        task.setId(1);
        Task task1 = CSVFormatter.taskFromString("1,TASK,Task1,NEW,TaskD,");
        Assertions.assertEquals(task1, task);
        Assertions.assertEquals(task1.getName(), task.getName());
        Assertions.assertEquals(task1.getStatus(), task.getStatus());
        Assertions.assertEquals(task1.getDescription(), task.getDescription());

        epic.setId(2);
        Epic epic1 = (Epic) CSVFormatter.taskFromString("2,EPIC,Epic1,NEW,EpicD,");
        Assertions.assertEquals(epic1, epic);
        Assertions.assertEquals(epic1.getName(), epic.getName());
        Assertions.assertEquals(epic1.getStatus(), epic.getStatus());
        Assertions.assertEquals(epic1.getDescription(), epic.getDescription());

        subtask.setId(3);
        Subtask sub1 = (Subtask) CSVFormatter.taskFromString("3,SUBTASK,Sub1,DONE,SubD,2");
        Assertions.assertEquals(sub1, subtask);
        Assertions.assertEquals(sub1.getName(), subtask.getName());
        Assertions.assertEquals(sub1.getStatus(), subtask.getStatus());
        Assertions.assertEquals(sub1.getDescription(), subtask.getDescription());
        Assertions.assertEquals(sub1.getEpicId(), subtask.getEpicId());

    }
}
