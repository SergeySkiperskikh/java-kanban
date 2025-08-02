package service;

import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;


public class InMemoryTaskManagerTest extends TaskManagerTest {
    @Override
    protected TaskManager getTaskManager() {
        return Managers.getDefault();
    }


}
