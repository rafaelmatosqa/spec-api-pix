package factory;


import java.util.HashMap;
import java.util.Map;

public class MapFactory {

    public static Map<String, String> headersComWrite() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer valid_token_with_payloadlocationrec_write");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public static Map<String, String> headersComRead() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer valid_token_with_payloadlocationrec_read");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public static Map<String, String> headersSemPermissao() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token_without_write_permission");
        headers.put("Content-Type", "application/json");
        return headers;
    }


    public static Map<String, Object> queryParamsValidos() {
        Map<String, Object> params = new HashMap<>();
        params.put("inicio", "2023-12-01T00:00:00Z");
        params.put("fim", "2024-04-01T23:59:59Z");
        params.put("paginaAtual", 0);
        params.put("itensPorPagina", 100);
        return params;
    }

    public static Map<String, Object> queryParamsInvalidos() {
        Map<String, Object> params = new HashMap<>();
        params.put("inicio", "2024-04-01T00:00:00Z");
        params.put("fim", "2023-12-01T23:59:59Z");
        return params;
    }


    public static Map<String, String> pathParamsComIdValido() {
        Map<String, String> params = new HashMap<>();
        params.put("id", "12069");
        return params;
    }

    public static Map<String, String> pathParamsComIdInvalido() {
        Map<String, String> params = new HashMap<>();
        params.put("id", "99999");
        return params;
    }


    public static Map<String, String> pathParamsParaDelecao() {
        Map<String, String> params = new HashMap<>();
        params.put("id", "12069");
        params.put("idRec", "RR123456782024011510056892226");
        return params;
    }
}
