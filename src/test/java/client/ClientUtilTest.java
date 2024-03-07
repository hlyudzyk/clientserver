package client;


import static client.ClientUtil.openSocket;
import static client.ClientUtil.writeToSocket;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientUtilTest {
    @BeforeEach
    public void init() {
    }

    @Order(1)
    @ParameterizedTest
    @ValueSource(ints = {8899, 9988})
    @DisplayName("openSocket creates an instance of Socket based on given host and port")
    @SneakyThrows
    void openSocketCreatesSocketConnectedToServer(int port) {
        try (var serverSocket = new ServerSocket(port);
             var clientSocket = openSocket(serverSocket.getInetAddress().getHostAddress(), port)) {

            assertNotNull(clientSocket);
            assertThat(clientSocket.getClass()).isEqualTo(Socket.class);
            assertThat(clientSocket.getPort()).isEqualTo(port);
            InetAddress remoteSocketAddress = ((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getAddress();
            assertThat(remoteSocketAddress).isEqualTo(InetAddress.getLocalHost());
        }
    }

    @Order(2)
    @Test
    @DisplayName("openSocket returns socket that is bound")
    @SneakyThrows
    void openSocketReturnsBoundSocket() {
        int port = 8899;
        try (var serverSocket = new ServerSocket(port);
             var clientSocket = openSocket(serverSocket.getInetAddress().getHostAddress(), port)) {

            assertTrue(clientSocket.isBound());
        }
    }

    @Order(3)
    @Test
    @DisplayName("openSocket returns socket that is connected")
    @SneakyThrows
    void openSocketReturnsNotClosedSocket() {
        int port = 8899;
        try (var serverSocket = new ServerSocket(port);
             var clientSocket = openSocket(serverSocket.getInetAddress().getHostAddress(), port)) {

            assertTrue(clientSocket.isConnected());
        }
    }

    @Order(4)
    @ParameterizedTest
    @ValueSource(strings = {"bye-bye", "see ya"})
    @DisplayName("writeToSocket sends given messages to server via socket output stream")
    @SneakyThrows
    void writeToSocketSendsMessageViaOutputStream(String givenMessage) {
        int port = 8899;
        try (var serverSocket = new ServerSocket(port)) {
            CompletableFuture<String> receivedMessageFuture = CompletableFuture.supplyAsync(() -> readMessage(serverSocket));
            try (var clientSocket = new Socket(InetAddress.getLocalHost().getHostAddress(), port)) {

                writeToSocket(givenMessage, clientSocket);

            }
            assertThat(receivedMessageFuture.get()).isEqualTo(givenMessage);
        }
    }

    @SneakyThrows
    private String readMessage(ServerSocket serverSocket) {
        try (var clientSocket = serverSocket.accept()) {
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.readLine();
        }
    }
}