import java.io.IOException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import services.UserService;



public class grpcProducer {

    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting grpcProducer server on port " + PORT);

        Server producer = ServerBuilder.forPort(PORT)
                .addService(new UserService())
                .build();

        producer.start();

        System.out.println("grpcProducer server started and listening on port " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down the grpcProducer server...");
            producer.shutdown();
            System.out.println("grpcProducer server shut down.");
        }));

        producer.awaitTermination();
    }
}
