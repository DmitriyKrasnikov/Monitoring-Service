package config;

import annotations.Loggable;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.DBInitializer;

@Loggable
@WebListener
public class AppContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        DBInitializer.initialize();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
