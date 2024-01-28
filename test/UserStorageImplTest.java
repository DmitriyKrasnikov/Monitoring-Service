import main.java.model.readings.MeterReadings;
import main.java.storage.UserStorageImpl;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Month;
import java.util.Map;
public class UserStorageImplTest {
    private UserStorageImpl userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new UserStorageImpl();
    }

    @Test
    void testAddNewUser() {
        userStorage.addNewUser("testUser", "testPassword");
        assertTrue(userStorage.isRegister("testUser"));
        assertTrue(userStorage.validateUser("testUser", "testPassword"));
    }

    @Test
    void testIsAdmin() {
        userStorage.addNewUser("adminUser", "adminPassword");
        assertTrue(userStorage.isAdmin("adminUser"));
    }

    @Test
    void testAddNewReadings() {
        userStorage.addNewUser("testUser", "testPassword");
        MeterReadings meterReadings = new MeterReadings(10, 20, 30, Month.JANUARY);
        userStorage.addNewReadings("testUser", meterReadings);
        assertEquals(meterReadings, userStorage.getCurrentReadings("testUser"));
    }

    @Test
    void testGetReadingsForMonth() {
        userStorage.addNewUser("testUser", "testPassword");
        MeterReadings meterReadings = new MeterReadings(10, 20, 30, Month.JANUARY);
        userStorage.addNewReadings("testUser", meterReadings);
        assertEquals(meterReadings, userStorage.getReadingsForMonth("testUser", Month.JANUARY));
    }

    @Test
    void testGetAllCurrentReadings() {
        userStorage.addNewUser("testUser1", "testPassword1");
        userStorage.addNewUser("testUser2", "testPassword2");
        MeterReadings meterReadings1 = new MeterReadings(10, 20, 30, Month.JANUARY);
        MeterReadings meterReadings2 = new MeterReadings(40, 50, 60, Month.FEBRUARY);
        userStorage.addNewReadings("testUser1", meterReadings1);
        userStorage.addNewReadings("testUser2", meterReadings2);
        Map<String, MeterReadings> allCurrentReadings = userStorage.getAllCurrentReadings();
        assertEquals(meterReadings1, allCurrentReadings.get("testUser1"));
        assertEquals(meterReadings2, allCurrentReadings.get("testUser2"));
    }
}
