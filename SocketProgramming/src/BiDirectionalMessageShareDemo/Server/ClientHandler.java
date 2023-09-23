package BiDirectionalMessageShareDemo.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import static BiDirectionalMessageShareDemo.Server.Server.clients;

class ClientHandler implements Runnable {
    private Socket socket;
    private String username;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());


            username = inputStream.readUTF();
            System.out.println(username+" has connected");

            while (true) {

                String message = inputStream.readUTF();
                if (message.startsWith("/private")) {
                    String[] parts = message.split(" ");
                    if (parts.length >= 3) {
                        String recipient = parts[1];

                        if (recipient.equalsIgnoreCase("server")) {

                            String privateMessage = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
                            String formattedMessage = "Private message to server from " + username + ": " + privateMessage;
                            Server.sendPrivateMessageToServer(formattedMessage);

                        }
                        else {

                            String privateMessage = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
                            Server.sendPrivateMessage("Private message from " + username + " to " + recipient + ": " + privateMessage, recipient, this);
                            System.out.println("Private message from " + username + " to " + recipient + ": " + privateMessage);
                        }
                    }
                    else {
                        sendMessage("Invalid private message format. Use /private recipient message");
                    }
                }

                else {
                    String formattedMessage = username + ": " + message;
                    Server.broadcastMessage(formattedMessage, this);
                    System.out.println(formattedMessage);
                }
            }
        } catch (IOException e) {
            System.out.println(username+" has disconnected");
            clients.remove(this);
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}