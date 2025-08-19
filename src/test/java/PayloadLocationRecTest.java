import client.PayloadLocationRecClient;
import dto.PayloadLocationRecDTO;
import factory.MapFactory;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;
import validator.PayloadLocationRecValidator;

import java.util.HashMap;
import java.util.Map;


public class PayloadLocationRecTest extends BaseTeste {
    private final PayloadLocationRecClient client = new PayloadLocationRecClient();

    private final PayloadLocationRecDTO payloadValido1 = PayloadLocationRecDTO.builder()
            .tipo("rec")
            .build();

    private final PayloadLocationRecDTO payloadInvalido = PayloadLocationRecDTO.builder()
            .tipo("cob")
            .build();

    @Test(description = "POST /locrec - Criar location com sucesso - 201")
    public void testCriarLocationComSucesso() {
        Map<String, String> headers = MapFactory.headersComWrite();

        ValidatableResponse response = client.criarLocation(spec, headers, payloadValido1);

        PayloadLocationRecValidator.validaCriacaoLocation(
                response,
                12069,
                "pix.example.com/qr/v2/rec/2353c790eefb11eaadc10242ac120002",
                "RR123456782024011510056892226"
        );
    }

    @Test(description = "POST /locrec - Erro 400: tipo inválido")
    public void testCriarLocationTipoInvalido() {
        Map<String, String> headers = MapFactory.headersComWrite();

        ValidatableResponse response = client.criarLocation(spec, headers, payloadInvalido);

        PayloadLocationRecValidator.validaErro400(
                response,
                "A presente requisição busca criar uma location sem respeitar o _schema_ estabelecido."
        );
    }

    @Test(description = "POST /locrec - Erro 403: token sem permissão")
    public void testCriarLocationSemPermissao() {
        Map<String, String> headers = MapFactory.headersSemPermissao();

        ValidatableResponse response = client.criarLocation(spec, headers, payloadValido1);

        PayloadLocationRecValidator.validaErro403(response);
    }

    @Test(description = "GET /locrec - Consultar lista com sucesso - 200")
    public void testConsultarListaLocations() {
        Map<String, String> headers = MapFactory.headersComRead();
        Map<String, Object> queryParams = MapFactory.queryParamsValidos();

        ValidatableResponse response = client.consultarLocations(spec, headers, queryParams);

        PayloadLocationRecValidator.validaConsultaLista(response, 1, "RR123456782024011510056892226");
    }

    @Test(description = "GET /locrec - Erro 400: parâmetros inválidos")
    public void testConsultarListaComParametrosInvalidos() {
        Map<String, String> headers = MapFactory.headersComRead();
        Map<String, Object> queryParams = MapFactory.queryParamsInvalidos();

        ValidatableResponse response = client.consultarLocations(spec, headers, queryParams);

        PayloadLocationRecValidator.validaErroConsultaInvalida(response);
    }

    @Test(description = "GET /locrec/{id} - Consultar location por ID - 200")
    public void testConsultarLocationPorId() {
        Map<String, String> headers = MapFactory.headersComRead();
        Map<String, String> pathParams = MapFactory.pathParamsComIdValido();

        ValidatableResponse response = givenWithPathParams(pathParams)
                .spec(spec)
                .headers(headers)
                .when()
                .get("/locrec/{id}")
                .then();

        PayloadLocationRecValidator.validaConsultaPorId(response, 12069, "RR123456782024011510056892226");
    }

    @Test(description = "GET /locrec/{id} - Erro 404: location não encontrada")
    public void testConsultarLocationPorIdNaoEncontrada() {
        Map<String, String> headers = MapFactory.headersComRead();
        Map<String, String> pathParams = MapFactory.pathParamsComIdInvalido();

        ValidatableResponse response = givenWithPathParams(pathParams)
                .spec(spec)
                .headers(headers)
                .when()
                .get("/locrec/{id}")
                .then();

        PayloadLocationRecValidator.validaErro404(response);
    }

    @Test(description = "DELETE /locrec/{id}/idRec - Deletar location com sucesso - 200")
    public void testDeletarLocationComSucesso() {
        Map<String, String> headers = MapFactory.headersComWrite();
        Map<String, String> pathParams = MapFactory.pathParamsParaDelecao();

        ValidatableResponse response = givenWithPathParams(pathParams)
                .spec(spec)
                .headers(headers)
                .when()
                .delete("/locrec/{id}/{idRec}")
                .then();

        PayloadLocationRecValidator.validaDelecao(response, 12069, "RR123456782024011510056892226");
    }

    @Test(description = "DELETE /locrec/{id}/idRec - Erro 404: location não encontrada")
    public void testDeletarLocationNaoEncontrada() {
        Map<String, String> headers = MapFactory.headersComWrite();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", "99999");
        pathParams.put("idRec", "RR123456782024011510056892226");

        ValidatableResponse response = givenWithPathParams(pathParams)
                .spec(spec)
                .headers(headers)
                .when()
                .delete("/locrec/{id}/{idRec}")
                .then();

        PayloadLocationRecValidator.validaErro404(response);
    }
}
