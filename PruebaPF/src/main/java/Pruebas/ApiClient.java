package Pruebas;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {

    public static void main(String[] args) throws Exception {
        // Datos a enviar
        Map<String, String> newdata = new HashMap<>();
        newdata.put("key1", "value1");
        newdata.put("key2", "value2");

        // Construir el cuerpo de la solicitud JSON
        String jsonBody = "{\"newdata\": {\"key1\": \"value1\", \"key2\": \"value2\"}}";

        // URL de la API FastAPI
        String url = "http://127.0.0.1:8000/recibir_datos";

        // Crear la solicitud HTTP POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Crear un cliente HTTP y enviar la solicitud
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Imprimir la respuesta
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
    }
}

