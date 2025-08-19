import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import wiremock.WireMockPixServer;

import java.util.Map;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;

public abstract class BaseTeste {

    protected static RequestSpecification spec;
    private static final WireMockPixServer wireMockServer = new WireMockPixServer();

    @BeforeTest(alwaysRun = true)
    public void preCondicao() {
        wireMockServer.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        spec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8081) //
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeTest(alwaysRun = true)
    public void posCondicao() {
        wireMockServer.stop();
    }


    protected RequestSpecification givenWithHeaders(Map<String, String> headers) {
        return given().spec(spec).headers(headers);
    }

    protected RequestSpecification givenWithQueryParams(Map<String, Object> queryParams) {
        return given().spec(spec).queryParams(queryParams);
    }

    protected RequestSpecification givenWithPathParams(Map<String, String> pathParams) {
        return given().spec(spec).pathParams(pathParams);
    }
}
