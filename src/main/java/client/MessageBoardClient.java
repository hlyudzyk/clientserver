package client;


import static client.ClientUtil.openConsoleReader;
import static client.ClientUtil.openSocket;
import static client.ClientUtil.readMessage;
import static client.ClientUtil.writeToSocket;

import java.io.BufferedReader;
import java.net.Socket;
import lombok.SneakyThrows;
import server.MessageBoardServer;


public class MessageBoardClient {
    private static final String SERVER_ADDRESS = MessageBoardServer.HOST;
    private static final int SERVER_PORT = MessageBoardServer.PORT;

    @SneakyThrows
    public static void main(String[] args) {
        try (BufferedReader reader = openConsoleReader()) {
            String message = readMessage(reader);

            while (!message.equals("q")) {
                try (Socket socket = openSocket(SERVER_ADDRESS, SERVER_PORT)) {
                    writeToSocket(message, socket);
                }
                message = readMessage(reader);
            }
        }
    }
}
