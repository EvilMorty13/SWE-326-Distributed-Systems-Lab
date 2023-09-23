package BiDirectionalMessageShareDemo.Client;

import java.io.DataInputStream;
import java.net.Socket;

class ReadHandler implements Runnable {
    private Socket socket;

    public ReadHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            while (true) {
                String message = inputStream.readUTF();
                System.out.println(message);
            }
        } catch (Exception e) {
            System.out.println("Disconnected from server");
        }
    }
}
