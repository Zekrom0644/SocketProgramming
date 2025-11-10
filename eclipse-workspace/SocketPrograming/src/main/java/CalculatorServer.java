import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalculatorServer {

    // Matches the client's default port 1234
    public static final int DEFAULT_PORT = 1234;

    public static void main(String[] args) {
        // Create a ThreadPool
        // newCachedThreadPool creates new threads as needed when connection requests arrive.
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            System.out.println("서버가 " + DEFAULT_PORT + " 포트에서 대기 중입니다...");

            // Continuously handle connections from multiple clients
            while (true) {
                // Wait for a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress());

                // Submit the task to handle the connected client to the ThreadPool
                // ClientHandler implements the Runnable interface
                threadPool.submit(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        } finally {
            threadPool.shutdown(); // Shut down the ThreadPool when the server stops
        }
    }
}