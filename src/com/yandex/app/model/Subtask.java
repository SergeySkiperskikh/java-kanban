package com.yandex.app.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status taskStatus, int epicId) {
        super(name, description, taskStatus);
        this.epicId = epicId;
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
