package BiDirectionalMessageShareDemo.Server;

import java.util.Scanner;
import java.util.Arrays;

public class ServerMessageHandler implements Runnable {

    public void run() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            try {
                String consoleInput = scanner.nextLine();
                if (consoleInput != null) {

                    if (consoleInput.startsWith("/broadcast")) {
                        String message = consoleInput.substring("/broadcast".length()).trim();
                        Server.broadcastMsgFromServerToClient(message);

                    }

                    else if (consoleInput.startsWith("/private")) {

                        String[] parts = consoleInput.split(" ");

                        if (parts.length >= 3) {

                            String recipient = parts[1];
                            String privateMessage = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
                            Server.sendPrivateMsgFromServerToClient(privateMessage,recipient);

                        }

                        else {
                            System.out.println("Invalid private message format. Use /private username message");
                        }
                    }

                    else {
                        System.out.println("Invalid command. Use /broadcast message or /private username message");
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

