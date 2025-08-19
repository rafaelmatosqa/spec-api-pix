package validator;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class PayloadLocationRecValidator {

    public static void validaCriacaoLocation(ValidatableResponse response, Integer expectedId, String expectedLocation, String expectedIdRec) {
        response
                .statusCode(201)
                .body("id", equalTo(expectedId))
                .body("location", equalTo(expectedLocation))
                .body("idRec", equalTo(expectedIdRec))
                .body("criacao", notNullValue());
    }

    public static void validaConsultaLista(ValidatableResponse response, int totalItens, String expectedIdRec) {
        response
                .statusCode(200)
                .body("parametros.paginacao.quantidadeTotalDeItens", equalTo(totalItens))
                .body("loc[0].idRec", equalTo(expectedIdRec));
    }

    public static void validaConsultaPorId(ValidatableResponse response, Integer expectedId, String expectedIdRec) {
        response
                .statusCode(200)
                .body("id", equalTo(expectedId))
                .body("idRec", equalTo(expectedIdRec));
    }

    public static void validaDelecao(ValidatableResponse response, Integer expectedId, String expectedIdRec) {
        response
                .statusCode(200)
                .body("idLocation", equalTo(expectedId))
                .body("idRec", equalTo(expectedIdRec));
    }

    public static void validaErro400(ValidatableResponse response, String detail) {
        response
                .statusCode(400)
                .body("title", equalTo("PayloadLocation inválido."))
                .body("detail", equalTo(detail));
    }

    public static void validaErro403(ValidatableResponse response) {
        response
                .statusCode(403)
                .body("title", equalTo("Acesso negado."))
                .body("detail", Matchers.containsString("escopo necessário"));
    }

    public static void validaErro404(ValidatableResponse response) {
        response
                .statusCode(404)
                .body("title", equalTo("Location não encontrada."));
    }

    public static void validaErroConsultaInvalida(ValidatableResponse response) {
        response
                .statusCode(400)
                .body("title", equalTo("Consulta inválida."))
                .body("detail", equalTo("O parâmetro 'fim' não pode ser anterior ao 'inicio'."));
    }
}
