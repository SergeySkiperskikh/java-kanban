package com.yandex.app.model;

import java.util.Objects;

public class Task {
    protected int id;
    protected final String name;
    protected final String description;
    protected Status taskStatus;


    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.taskStatus = status;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
