package com.yandex.app.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasksList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void addSubtask(Subtask subtask) {
        subtasksList.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasksList.remove(subtask);
    }

    public List<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void setSubtasksList(List<Subtask> subtasksList) {
        this.subtasksList = subtasksList;
    }

    @Override
    public String toString() {
        return super.toString() +
                "subtasksList=" + subtasksList +
                '}';
    }
}
