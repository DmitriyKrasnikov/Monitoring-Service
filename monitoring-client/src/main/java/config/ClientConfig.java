package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LoggerConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ClientConfig {
    public static String HOST;
    public static String PORT;

    private static final Logger logger = LoggerFactory.getLogger(LoggerConfig.class);

    static {
        Properties prop = new Properties();
        try (InputStream input = ClientConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            prop.load(input);

            HOST = prop.getProperty("server.host");
            PORT = prop.getProperty("server.port");

            if (HOST == null || PORT == null) {
                logger.error("Хост или порт не указаны в файле свойств");
            }

        } catch (FileNotFoundException e) {
            logger.error("Не удалось найти файл свойств");
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error("Произошла ошибка при чтении файла свойств");
            logger.error(e.getMessage());
        }
    }
}