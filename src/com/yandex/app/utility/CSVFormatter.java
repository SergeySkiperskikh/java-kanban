package com.yandex.app.utility;

import com.yandex.app.model.*;
import com.yandex.app.service.HistoryManager;

import java.util.List;

public class CSVFormatter {
    private static final String DELIMITER = ",";

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public static String toString(Task task, TaskType type) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(DELIMITER);
        builder.append(type.toString()).append(DELIMITER);
        builder.append(task.getName()).append(DELIMITER);
        builder.append(task.getStatus().toString()).append(DELIMITER);
        builder.append(task.getDescription()).append(DELIMITER);
        if (type == TaskType.SUBTASK) {
            builder.append(((Subtask) task).getEpicId());
        }
        return builder.toString();
    }


    public static Task taskFromString(String line) {
        line = line.trim();
        byte lengthForSubtask = 6;

        String[] taskToSting = line.split(DELIMITER);
        int taskId = Integer.parseInt(taskToSting[0]);
        TaskType taskType = TaskType.valueOf(taskToSting[1]);
        String taskName = taskToSting[2];
        Status taskStatus = Status.valueOf(taskToSting[3]);
        String taskDescription = taskToSting[4];

        switch (taskType) {
            case EPIC:
                Epic epic = new Epic(taskName, taskDescription);
                epic.setStatus(taskStatus);
                epic.setId(taskId);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(taskToSting[lengthForSubtask - 1]);
                Subtask subtask = new Subtask(taskName, taskDescription, taskStatus, epicId);
                subtask.setId(taskId);
                return subtask;
            case TASK:
            default:
                Task task = new Task(taskName, taskDescription, taskStatus);
                task.setId(taskId);
                return task;

        }
    }

    //Я оставил на будущее, как я понял, можно не делать это
   /* public static String toString(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();
        if (history.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (Task task : history) {
            builder.append(task.getId()).append(DELIMITER);
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public static void historyFromString(String line) {

    }
    */

}
