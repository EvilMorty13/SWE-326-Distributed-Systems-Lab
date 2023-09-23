package BiDirectionalMessageShareDemo.Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int SERVER_PORT = 5000;
    private static final int MAX_CLIENTS = 10;

    static Set<ClientHandler> clients = new HashSet<>();
    private static ExecutorService clientThreadPool = Executors.newFixedThreadPool(MAX_CLIENTS);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is running on port " + SERVER_PORT);


            new Thread(new ServerMessageHandler()).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientThreadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientThreadPool.shutdown();
        }

    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void sendPrivateMessage(String message, String recipient, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(message);
                return;
            }
        }
        sender.sendMessage("User '" + recipient + "' not found or is not online.");
    }

    public static void sendPrivateMessageToServer(String message) {
        System.out.println(message);
    }


    public static void broadcastMsgFromServerToClient(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage("Server: " + message);
        }
    }

    public static void sendPrivateMsgFromServerToClient(String message, String recipient) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage("Private message From Server to "+client.getUsername()+" : " + message);
                return;
            }
        }
        sendPrivateMessageToServer("User '" + recipient + "' not found or is not online.");
    }
}


