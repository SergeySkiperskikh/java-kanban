package com.yandex.app.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void addSubtask(Integer id) {
        subtasksId.add(id);
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

    @Override
    public String toString() {
        return super.toString() +
                "subtasksId=" + subtasksId +
                '}';
    }
}
