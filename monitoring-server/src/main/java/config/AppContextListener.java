package config;

import annotations.Loggable;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.DBInitializer;

/**
 * Слушатель контекста приложения.
 * Этот класс реализует интерфейс ServletContextListener, позволяя реагировать на события инициализации и уничтожения контекста приложения.
 *
 * @Loggable Аннотация, указывающая, что вызовы методов этого класса должны быть залогированы.
 * @WebListener Аннотация, указывающая, что этот класс является слушателем веб-событий.
 */
@Loggable
@WebListener
public class AppContextListener implements ServletContextListener {

    /**
     * Метод, вызываемый при инициализации контекста приложения.
     * Инициализирует базу данных.
     *
     * @param servletContextEvent Объект ServletContextEvent, содержащий информацию о событии.
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        DBInitializer.initialize();
    }

    /**
     * Метод, вызываемый при уничтожении контекста приложения.
     * В текущей реализации этот метод не выполняет никаких действий.
     *
     * @param servletContextEvent Объект ServletContextEvent, содержащий информацию о событии.
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}

