import dao.readings.ReadingsStorageImpl;
import dao.user.UserStorageImpl;
import model.readings.MeterReadings;
import model.readings.MeterType;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import utils.DBConnectionManager;
import utils.DBInitializer;

import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class ReadingsStorageTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("test")
                .withUsername("test")
                .withPassword("test");
        postgreSQLContainer.start();
        DBConnectionManager.setConnectionDetails(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        );
        DBInitializer.initialize();
        new UserStorageImpl().addNewUser("testuser", "testemail", "testpassword");

        Map<MeterType, Integer> readings = new HashMap<>();
        readings.put(MeterType.HEATING, 100);
        readings.put(MeterType.HOT_WATER, 200);
        readings.put(MeterType.COLD_WATER, 300);
        MeterReadings newReadings = new MeterReadings(readings, Month.JANUARY);

        new ReadingsStorageImpl().addNewReadings(1, newReadings);
    }

    private final ReadingsStorageImpl readingsStorage = new ReadingsStorageImpl();

    @Test
    public void testAddNewReadings() {
        assertNotNull(readingsStorage.getCurrentReadings(1));
    }


    @Test
    public void testGetCurrentReadings() {
        MeterReadings readings = readingsStorage.getCurrentReadings(1);

        assertNotNull(readings);
    }

    @Test
    public void testGetReadingsForMonth() {
        MeterReadings readings = readingsStorage.getReadingsForMonth(1, Month.JANUARY);

        assertNotNull(readings);
    }

    @Test
    public void testGetReadingsHistory() {
        Collection<MeterReadings> readingsHistory = readingsStorage.getReadingsHistory(1);

        assertNotNull(readingsHistory);
    }

    @Test
    public void testGetAllCurrentReadings() {
        Map<String, MeterReadings> allCurrentReadings = readingsStorage.getAllCurrentReadings();

        assertNotNull(allCurrentReadings);
    }
}

