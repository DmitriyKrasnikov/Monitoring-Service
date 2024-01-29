import server.HttpMeterServer;

import java.io.IOException;

/**
 * Главный класс приложения, содержащий метод main, который является точкой входа в приложение.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        HttpMeterServer server = new HttpMeterServer();
        server.start();
    }
}
