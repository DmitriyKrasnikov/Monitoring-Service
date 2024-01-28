package main.java.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс LoggerConfig предоставляет функциональность для настройки и получения логгера.
 * Этот класс использует шаблон проектирования Singleton для обеспечения единственного экземпляра логгера в приложении.
 */
public class LoggerConfig {
    private static final Logger logger;

    static {
        logger = Logger.getLogger(LoggerConfig.class.getName());
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false); // Отключаем использование родительских обработчиков
    }

    public static Logger getLogger() {
        return logger;
    }
}

