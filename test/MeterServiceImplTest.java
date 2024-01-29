import model.readings.MeterReadings;
import model.user.dto.UserDto;
import service.MeterServiceImpl;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Month;
import java.util.Collection;
import java.util.Map;

public class MeterServiceImplTest {

    private MeterServiceImpl meterService;

    @BeforeEach
    void setUp() {
        meterService = new MeterServiceImpl();
    }

    @Test
    void testRegister() {
        UserDto userDto = new UserDto("testUser", "testPassword");
        assertEquals(meterService.register(userDto), "A user with that name already exists");
    }

    @Test
    void testLogin() {
        UserDto userDto = new UserDto("testUser", "testPassword");
        meterService.register(userDto);
        assertEquals("You are logged in", meterService.login(userDto));
    }

    @Test
    void testLogout() {
        UserDto userDto = new UserDto("testUser", "testPassword");
        meterService.register(userDto);
        meterService.login(userDto);
        assertEquals("You are logged out", meterService.logout("testUser"));
        assertFalse(meterService.isLogin("testUser"));
    }

    @Test
    void testPostCurrentReadings() {
        UserDto userDto = new UserDto("testUser", "testPassword");
        meterService.register(userDto);
        meterService.login(userDto);
        MeterReadings meterReadings = new MeterReadings(10, 20, 30, Month.JANUARY);
        assertEquals("The readings for this month has already been submitted", meterService.postCurrentReadings("testUser", meterReadings));
        assertEquals(meterReadings, meterService.getCurrentReadings("testUser"));
    }

    @Test
    void testGetReadingsForMonth() {
        UserDto userDto = new UserDto("testUser", "testPassword");
        meterService.register(userDto);
        meterService.login(userDto);
        MeterReadings meterReadings = new MeterReadings(10, 20, 30, Month.JANUARY);
        meterService.postCurrentReadings("testUser", meterReadings);
        assertEquals(meterReadings, meterService.getReadingsForMonth("testUser", Month.JANUARY));
    }

    @Test
    void testGetReadingHistory() {
        UserDto userDto = new UserDto("testUser", "testPassword");
        meterService.register(userDto);
        meterService.login(userDto);
        MeterReadings meterReadings = new MeterReadings(10, 20, 30, Month.JANUARY);
        meterService.postCurrentReadings("testUser", meterReadings);
        Collection<MeterReadings> history = meterService.getReadingHistory("testUser");
        assertTrue(history.contains(meterReadings));
    }

    @Test
    void testGetAllCurrentReadings() {
        UserDto userDto1 = new UserDto("testUser1", "testPassword1");
        UserDto userDto2 = new UserDto("testUser2", "testPassword2");
        meterService.register(userDto1);
        meterService.register(userDto2);
        meterService.login(userDto1);
        meterService.login(userDto2);
        MeterReadings meterReadings1 = new MeterReadings(10, 20, 30, Month.JANUARY);
        MeterReadings meterReadings2 = new MeterReadings(40, 50, 60, Month.FEBRUARY);
        meterService.postCurrentReadings("testUser1", meterReadings1);
        meterService.postCurrentReadings("testUser2", meterReadings2);
        Map<String, MeterReadings> allCurrentReadings = meterService.getAllCurrentReadings();
        assertEquals(meterReadings1, allCurrentReadings.get("testUser1"));
        assertEquals(meterReadings2, allCurrentReadings.get("testUser2"));
    }

    @Test
    void testValidateReadings() {
        MeterReadings validReadings = new MeterReadings(10, 20, 30, Month.JANUARY);
        MeterReadings invalidReadings = new MeterReadings(-10, 20, 30, Month.JANUARY);
        assertTrue(meterService.validateReadings(validReadings));
        assertFalse(meterService.validateReadings(invalidReadings));
    }
}
