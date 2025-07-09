package com.yandex.app.service;

import java.io.File;

public class Managers {
    private Managers (){

    }

    public static TaskManager getDefault() {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("src/resources/data.csv"));
        return taskManager;
    }


    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
