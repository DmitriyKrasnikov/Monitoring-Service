import dao.audit.AuditStorageImpl;
import dao.user.UserStorageImpl;
import model.audit.ActionType;
import model.audit.AuditLog;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import utils.DBConnectionManager;
import utils.DBInitializer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AuditStorageTest {
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
    }

    private final AuditStorageImpl auditStorage = new AuditStorageImpl();
    private final UserStorageImpl userStorage = new UserStorageImpl();

    @Before
    public void initializeDatabase() {
        String url = postgreSQLContainer.getJdbcUrl();
        String user = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = new String(Files.readAllBytes(Paths.get("src/test/testResources/init.sql")));
            Statement statement = conn.createStatement();
            statement.execute(sql);
            DBInitializer.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRecordAction() {
        userStorage.addNewUser("testuser", "testemail", "testpassword");
        auditStorage.recordAction(new AuditLog(1, ActionType.LOGIN, LocalDateTime.now(), "Test action"));

        assertFalse(auditStorage.getUserActions(1).isEmpty());
    }

    @Test
    public void testGetUserActions() {
        List<AuditLog> userActions = auditStorage.getUserActions(1);

        assertNotNull(userActions);
    }
}


