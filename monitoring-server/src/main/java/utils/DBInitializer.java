package utils;

import annotations.Loggable;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс инициализации базы данных.
 * Он аннотирован @Loggable.
 */
@Loggable
public class DBInitializer {

    /**
     * Инициализирует базу данных.
     * Создает соединение с базой данных, находит подходящую реализацию базы данных и выполняет обновление Liquibase.
     * В случае успешного завершения миграции выводит сообщение "Migration is completed successfully".
     * В случае возникновения исключений выводит сообщение об ошибке и, при необходимости, выбрасывает исключение.
     */
    public static void initialize() {
        try {
            Connection connection = DBConnectionManager.getConnection();
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase("db.changelog/main-changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Миграция успешно завершена");
        } catch (LiquibaseException e) {
            System.out.println("SQL исключение в миграции " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}