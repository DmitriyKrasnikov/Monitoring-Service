package utils;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

public class DBInitializer {

    public static void initialize() {
        try {
            Connection connection = DBConnectionManager.getConnection();
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase("db.changelog/main-changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Migration is completed successfully");
        } catch (LiquibaseException e) {
            System.out.println("SQL Exception in migration " + e.getMessage());
        }
    }
}