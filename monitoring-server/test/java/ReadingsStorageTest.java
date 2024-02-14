import dao.readings.ReadingsRepositoryImpl;
import dao.user.UserRepositoryImpl;
import model.readings.MeterReadings;
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
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        new UserRepositoryImpl().addNewUser("testuser", "testemail", "testpassword", "testSalt");

        Map<String, Integer> readings = new HashMap<>();
        readings.put("HEATING", 100);
        readings.put("HOT_WATER", 200);
        readings.put("COLD_WATER", 300);
        MeterReadings newReadings = new MeterReadings(readings, Month.JANUARY);

        new ReadingsRepositoryImpl().addNewReadings(1, newReadings);
    }

    private final ReadingsRepositoryImpl readingsStorage = new ReadingsRepositoryImpl();

    @Test
    public void testAddNewReadings() {
        assertNotNull(readingsStorage.getCurrentReadings(1));
    }


    @Test
    public void testGetCurrentReadings() {
        Optional<MeterReadings> readings = readingsStorage.getCurrentReadings(1);

        assertNotNull(readings);
    }

    @Test
    public void testGetReadingsForMonth() {
        Optional<MeterReadings> readings = readingsStorage.getReadingsForMonth(1, Month.JANUARY);

        assertTrue(readings.isPresent());
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