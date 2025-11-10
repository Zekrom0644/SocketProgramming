import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

// Implements the Runnable interface
public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        // Use try-with-resources
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true) // true: enables auto-flush
        ) {
            String requestLine;
            // Receive one line at a time from the client
            while ((requestLine = in.readLine()) != null) {
                System.out.println("클라이언트 요청: " + requestLine);

                // Process the request and generate a response according to the protocol
                String response = processRequest(requestLine);

                // Send the response to the client
                out.println(response);
                System.out.println("서버 응답: " + response);
            }
        } catch (IOException e) {
            System.err.println("클라이언트 핸들러 오류: " + e.getMessage());
        } finally {
            try {
                clientSocket.close(); // Close the socket
                System.out.println("클라이언트 연결 종료: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                System.err.println("소켓 종료 오류: " + e.getMessage());
            }
        }
    }

    /**
     * Parses the client's request string and performs the calculation.
     * @param requestLine (e.g., "ADD 10 20")
     * @return Response string (e.g., "OK 30", "ERROR DIV_ZERO")
     */
    private String processRequest(String requestLine) {
        // Parse the string using StringTokenizer with spaces as delimiters
        StringTokenizer tokenizer = new StringTokenizer(requestLine);

        if (!tokenizer.hasMoreTokens()) {
            return "ERROR BAD_REQUEST 빈 요청입니다.";
        }

        String command = tokenizer.nextToken().toUpperCase(); // e.g., ADD, SUB, MUL, DIV

        // Supports the four basic arithmetic operations
        switch (command) {
            case "ADD":
            case "SUB":
            case "MUL":
            case "DIV":
                return handleArithmetic(command, tokenizer);
            // case "MIN": // Example scenario
            //     return "ERROR TOO_MANY_ARGS 예제 시나리오 에러";
            default:
                return "ERROR UNKNOWN_CMD 알 수 없는 명령어입니다.";
        }
    }

    /**
     * Handles arithmetic operations and returns a response matching the protocol.
     */
    private String handleArithmetic(String command, StringTokenizer tokenizer) {
        double num1, num2;

        try {
            // Check the number of arguments (e.g., 10, 20 - two arguments)
            if (tokenizer.countTokens() != 2) {
                // "Too many arguments" from the example scenario
                return "ERROR BAD_ARGS 인자의 개수가 2개가 아닙니다.";
            }

            num1 = Double.parseDouble(tokenizer.nextToken());
            num2 = Double.parseDouble(tokenizer.nextToken());

        } catch (NumberFormatException e) {
            return "ERROR BAD_ARGS 인자가 숫자가 아닙니다.";
        } catch (Exception e) {
            return "ERROR UNKNOWN 알 수 없는 오류: " + e.getMessage();
        }

        double result = 0;

        switch (command) {
            case "ADD":
                result = num1 + num2;
                break;
            case "SUB":
                result = num1 - num2;
                break;
            case "MUL":
                result = num1 * num2;
                break;
            case "DIV":
                // Handle division by zero exception
                if (num2 == 0) {
                    // "divided by zero"
                    return "ERROR DIV_ZERO 0으로 나눌 수 없습니다.";
                }
                result = num1 / num2;
                break;
        }

        // Success response: "code corresponding to the meaning" + "field corresponding to the answer value"
        // e.g., "OK 30"
        return "OK " + result;
    }
}