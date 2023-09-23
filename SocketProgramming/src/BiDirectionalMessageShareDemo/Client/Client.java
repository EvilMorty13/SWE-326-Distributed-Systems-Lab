package BiDirectionalMessageShareDemo.Client;


import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to Server");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            DataOutputStream usernameStream = new DataOutputStream(socket.getOutputStream());
            usernameStream.writeUTF(username);

            new Thread(new ReadHandler(socket)).start();
            new Thread(new WriteHandler(socket, username)).start();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
