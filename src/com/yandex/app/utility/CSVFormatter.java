package com.yandex.app.utility;

import com.yandex.app.model.*;
import com.yandex.app.service.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class CSVFormatter {
    private static final String DELIMITER = ",";
    private static final byte lengthForSubtask = 8;

    public static String getHeader() {
        return "id,type,name,status,description,starttime,duration,epic";
    }

    public static String toString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(DELIMITER);//0
        builder.append(task.getType().toString()).append(DELIMITER);//1
        builder.append(task.getName()).append(DELIMITER);//2
        builder.append(task.getStatus().toString()).append(DELIMITER);//3
        builder.append(task.getDescription()).append(DELIMITER);//4
        builder.append(task.getStartTime().toString()).append(DELIMITER);//5
        builder.append(task.getDuration().toString()).append(DELIMITER);//6
        if (task.getType() == TaskType.SUBTASK) {
            builder.append(((Subtask) task).getEpicId());//7
        }
        return builder.toString();
    }


    public static Task taskFromString(String line) {
        line = line.trim();

        String[] taskToSting = line.split(DELIMITER);
        int taskId = Integer.parseInt(taskToSting[0]);
        TaskType taskType = TaskType.valueOf(taskToSting[1]);
        String taskName = taskToSting[2];
        Status taskStatus = Status.valueOf(taskToSting[3]);
        String taskDescription = taskToSting[4];
        LocalDateTime taskStartTime = LocalDateTime.parse(taskToSting[5]);
        Duration taskDuration = Duration.parse(taskToSting[6]);

        switch (taskType) {
            case EPIC:
                Epic epic = new Epic(taskName, taskDescription);
                epic.setStatus(taskStatus);
                epic.setId(taskId);
                epic.setStartTime(taskStartTime);
                epic.setDuration(taskDuration);
                epic.calculateEndTime();
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(taskToSting[lengthForSubtask - 1]);
                Subtask subtask = new Subtask(taskName, taskDescription, taskStatus, epicId, (int) taskDuration.toMinutes(), taskStartTime);
                subtask.setId(taskId);
                return subtask;
            case TASK:
            default:
                Task task = new Task(taskName, taskDescription, taskStatus, (int) taskDuration.toMinutes(), taskStartTime);
                task.setId(taskId);
                return task;

        }
    }
}
