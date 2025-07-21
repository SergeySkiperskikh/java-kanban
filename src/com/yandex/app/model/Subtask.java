package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status taskStatus, int epicId, Integer durationInMinutes, LocalDateTime startTime) {
        super(name, description, taskStatus, durationInMinutes, startTime);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.toString() +
                "epicId=" + epicId +
                '}';
    }
}
