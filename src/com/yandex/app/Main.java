package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.TaskType;
import com.yandex.app.service.*;
import org.junit.jupiter.api.Assertions;


public class Main {

    public static void main(String[] args) {
        FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getDefault();
    }

}
