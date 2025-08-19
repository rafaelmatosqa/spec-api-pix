package wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockPixServer {

    private static final int PORT = 8081;
    private WireMockServer wireMockServer;

    public void start() {
        WireMockConfiguration config = options()
                .port(PORT)
                .usingFilesUnderClasspath("wiremock-pix-recorrencias");

        wireMockServer = new WireMockServer(config);
        wireMockServer.start();

        System.out.println("✅ Servidor WireMock iniciado na porta " + PORT);
        System.out.println("📁 Mapeamentos carregados de: classpath:/wiremock-pix-recorrencias/mappings");
        System.out.println("🔗 Endpoints disponíveis: POST /locrec, GET /locrec, etc.");
        System.out.println("ℹ️  Acesse o console em: http://localhost:" + PORT + "/__admin/mappings");
    }

    public void stop() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            System.out.println("🛑 Servidor WireMock parado.");
        }
    }

    public static void main(String[] args) {
        WireMockPixServer server = new WireMockPixServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }

}