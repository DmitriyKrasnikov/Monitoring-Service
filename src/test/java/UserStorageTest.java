import dao.user.UserStorageImpl;
import org.junit.Test;
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
        new UserStorageImpl().addNewUser("testuser", "testemail", "testpassword");
    }

    private final UserStorageImpl userStorage = new UserStorageImpl();

    @Test
    public void testAddNewUser() {
        assertTrue(userStorage.isRegister("testemail"));
    }

    @Test
    public void testIsRegister() {
        assertFalse(userStorage.isRegister("nonexistentemail"));
    }

    @Test
    public void testGetUserIdFromEmail() {
        Integer userId = userStorage.getUserIdFromEmail("testemail");

        assertNotNull(userId);
    }

    @Test
    public void testGetUserIdFromName() {
        Integer userId = userStorage.getUserIdFromName("testuser");

        assertNotNull(userId);
    }

    @Test
    public void testIsAdmin() {
        assertFalse(userStorage.isAdmin("testemail"));
    }

    @Test
    public void testValidateUser() {
        assertTrue(userStorage.validateUser("testemail", "testpassword"));
    }
}
