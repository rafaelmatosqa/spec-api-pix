package client;

import dto.PayloadLocationRecDTO;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class PayloadLocationRecClient {

    private static final String LOCREC_BASE_PATH = "/locrec";
    private static final String LOCREC_BY_ID_PATH = "/locrec/{id}";
    private static final String LOCREC_DELETE_PATH = "/locrec/{id}/idRec";

    public ValidatableResponse criarLocation(RequestSpecification spec, Map<String, String> headers, PayloadLocationRecDTO payload) {
        return given().spec(spec)
                .headers(headers)
                .body(payload)
                .when()
                .post(LOCREC_BASE_PATH)
                .then();
    }

    public ValidatableResponse consultarLocations(RequestSpecification spec, Map<String, String> headers, Map<String, Object> queryParams) {
        return given().spec(spec)
                .headers(headers)
                .queryParams(queryParams)
                .when()
                .get(LOCREC_BASE_PATH)
                .then();
    }

    public ValidatableResponse consultarLocationPorId(RequestSpecification spec, Map<String, String> headers, String id) {
        return given().spec(spec)
                .headers(headers)
                .pathParam("id", id)
                .when()
                .get(LOCREC_BY_ID_PATH)
                .then();
    }

    public ValidatableResponse deletarLocation(RequestSpecification spec, Map<String, String> headers, String id, String idRec) {
        return given().spec(spec)
                .headers(headers)
                .pathParams("id", id, "idRec", idRec)
                .when()
                .delete(LOCREC_DELETE_PATH)
                .then();
    }
}
