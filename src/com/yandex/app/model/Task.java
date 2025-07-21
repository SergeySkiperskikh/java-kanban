package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected final String name;
    protected final String description;
    protected Status taskStatus;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected TaskType type = TaskType.TASK;


    public Task(String name, String description, Status status, Integer durationInMinutes, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.taskStatus = status;
        this.duration = Duration.ofMinutes(durationInMinutes);
        this.startTime = startTime;
        calculateEndTime();
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return taskStatus;
    }

    public void setStatus(Status status) {
        this.taskStatus = status;
    }

    public void calculateEndTime() {
        endTime = startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", type=" + type +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }
}
