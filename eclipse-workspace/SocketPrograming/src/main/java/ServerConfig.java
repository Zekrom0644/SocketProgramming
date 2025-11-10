import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

// Class for configuration
public class ServerConfig {

    private final String configFilePath;
    private String serverHost;
    private int serverPort;

    // Default set (in case server information doesn't exist)
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1234;

    public ServerConfig(String configFilePath) {
        this.configFilePath = configFilePath;
        // init as default
        this.serverHost = DEFAULT_HOST;
        this.serverPort = DEFAULT_PORT;
    }

    public void load() {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {

            // overwrite if file exists
            properties.load(reader);
            this.serverHost = properties.getProperty("HOST", DEFAULT_HOST);
            this.serverPort = Integer.parseInt(properties.getProperty("PORT", String.valueOf(DEFAULT_PORT)));

            System.out.println("설정 파일 로드 완료.");

        } catch (FileNotFoundException e) {
            // if file doesn't exist set as default
            System.err.println("'" + configFilePath + "' 파일을 찾을 수 없습니다. 기본값(localhost:1234)을 사용합니다.");
        } catch (IOException e) {
            System.err.println("설정 파일 읽기 오류: " + e.getMessage() + ". 기본값을 사용합니다.");
        } catch (NumberFormatException e) {
            System.err.println("포트 번호 형식이 잘못되었습니다. 기본 포트(1234)를 사용합니다.");
            this.serverPort = DEFAULT_PORT;
        }
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }
}