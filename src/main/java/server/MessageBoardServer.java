package server;


import static server.ServerUtil.acceptClientSocket;
import static server.ServerUtil.createServerSocket;
import static server.ServerUtil.printMessage;
import static server.ServerUtil.readMessageFromSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MessageBoardServer {
    public static final String HOST = ServerUtil.getLocalHost();
    public static final int PORT = 8189; // you can use any free port you want

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = createServerSocket(PORT)) {
            while (true) {
                try (Socket clientSocket = acceptClientSocket(serverSocket)) {
                    String message = readMessageFromSocket(clientSocket);
                    printMessage(clientSocket, message);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
