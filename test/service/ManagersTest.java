package service;

import com.yandex.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    @Test
    public void exampleManagersReturn() {// Честно говоря, плохо понял что нужно протеститровать
        Assertions.assertNotNull(Managers.getDefaultHistory());
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }

}
