import dao.user.UserRepositoryImpl;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import utils.DBConnectionManager;
import utils.DBInitializer;

import static org.junit.Assert.*;

public class UserStorageTest {

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
    }

    private final UserRepositoryImpl userStorage = new UserRepositoryImpl();

    @Test
    @DisplayName("Тестирование метода addNewUser")
    public void testAddNewUser() {
        assertTrue(userStorage.findByEmail("testemail").isPresent());
    }

    @Test
    @DisplayName("Тестирование метода isRegister")
    public void testIsRegister() {
        assertFalse(userStorage.findByEmail("nonexistentemail").isPresent());
    }

    @Test
    @DisplayName("Тестирование метода getUserIdFromName")
    public void testGetUserIdFromName() {
        Integer userId = userStorage.getUserIdFromName("testuser");

        assertNotNull(userId);
    }

    @Test
    @DisplayName("Тестирование метода isAdmin")
    public void testIsAdmin() {
        assertFalse(userStorage.findByEmail("testemail").get().isAdmin());
    }

    @Test
    @DisplayName("Тестирование метода validateUser")
    public void testValidateUser() {
        assertTrue(userStorage.validateUser("testemail", "testpassword"));
    }
}