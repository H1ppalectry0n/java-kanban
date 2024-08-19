import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefaultNotNull() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void getDefaultHistoryNotNull() {
        assertNotNull(Managers.getDefaultHistory());
    }
}