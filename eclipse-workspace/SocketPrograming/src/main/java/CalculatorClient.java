import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class CalculatorClient {

    public static void main(String[] args) {

        // 1. load config
        ServerConfig config = new ServerConfig("server_info.dat");
        config.load(); // try reading if not default

        String host = config.getServerHost();
        int port = config.getServerPort();

        System.out.println("서버 접속 시도: " + host + ":" + port);

        // try-with-resources
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)) // 사용자 입력을 위한 스트림
        ) {
            System.out.println("서버에 연결되었습니다.");
            System.out.println("계산식을 입력하세요 (예: ADD 10 20, SUB 10 5). 종료는 'exit'.");

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {

                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }

                // 2. Sending calculations to the server
                out.println(userInput);

                // 3. receive and output results from the server
                String serverResponse = in.readLine();
                System.out.println("서버 응답: " + serverResponse);
                System.out.println("\n계산식을 입력하세요 (종료: 'exit'):");
            }

        } catch (UnknownHostException e) {
            System.err.println("호스트를 찾을 수 없습니다: " + host);
        } catch (IOException e) {
            System.err.println("I/O 오류: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        } finally {
            System.out.println("클라이언트를 종료합니다.");
        }
    }
}