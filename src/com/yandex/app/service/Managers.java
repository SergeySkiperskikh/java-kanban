package com.yandex.app.service;

import java.io.File;

public class Managers {
    private Managers() {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFiledManager() {
        return FileBackedTaskManager.loadFromFile(new File("src/resources/data.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
