package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    public static final LocalDateTime DEFAULT_START_TIME = LocalDateTime.MIN;
    public static final LocalDateTime DEFAULT_END_TIME = LocalDateTime.MIN;
    private static final Duration DEFAULT_DURATION = Duration.ZERO;

    private List<Integer> subtasksId = new ArrayList<>();


    public Epic(String name, String description) {
        //инициализирую поля с временем, чтобы не было Null, при сейве пустого епика
        super(name, description, Status.NEW, 0, DEFAULT_START_TIME);
        this.type = TaskType.EPIC;

    }

    public void addSubtask(Task subtask) {
        subtasksId.add(subtask.getId());
    }

    public void removeSubtask(Integer id) {
        subtasksId.remove(id);
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(List<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public void recalculateTime(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.startTime = DEFAULT_START_TIME;
            this.duration = DEFAULT_DURATION;
            this.endTime = DEFAULT_END_TIME;
            return;
        }

        this.startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(DEFAULT_START_TIME);

        this.endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(DEFAULT_END_TIME);

        calculateDuration();
    }

    public void calculateDuration() {
        this.duration = Duration.between(startTime, endTime);
    }


    @Override
    public String toString() {
        return super.toString() +
                "subtasksId=" + subtasksId +
                '}';
    }
}
