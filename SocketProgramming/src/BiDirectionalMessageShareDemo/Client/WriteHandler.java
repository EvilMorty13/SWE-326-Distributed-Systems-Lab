package BiDirectionalMessageShareDemo.Client;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;


class WriteHandler implements Runnable {
    private Socket socket;
    private String username;

    public WriteHandler(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String message = scanner.nextLine();
                outputStream.writeUTF(message);
            }
        } catch (Exception e) {
            System.out.println("Disconnected from server");
        }
    }
}


