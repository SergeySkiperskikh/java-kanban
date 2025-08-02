package service;

import com.yandex.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    @Test
    public void exampleManagersReturn() {
        Assertions.assertNotNull(Managers.getDefault());
        Assertions.assertNotNull(Managers.getDefaultHistory());
        Assertions.assertNotNull(Managers.getFiledManager());
    }

}
