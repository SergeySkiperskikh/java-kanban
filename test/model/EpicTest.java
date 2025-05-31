package model;

import com.yandex.app.model.Epic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EpicTest {
    @Test
    public void ifIdEqualThenEpicEqual() {
        Epic epic1 = new Epic("епик1", "epic1");
        epic1.setId(1);
        Epic epic2 = new Epic("епик2", "epic2");
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2);

    }
}
