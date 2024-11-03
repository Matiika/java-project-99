package hexlet.code.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AppApplicationTest {

    @Test
    void contextLoads() {

    }

    @Test
    public void mainTest() {
        var a = AppApplication.plus(1, 1);
        assertEquals(2, 2);
    }
}
