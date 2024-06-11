package test;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Test2 {
	   public static void llamadaApiGet(String tablename, String dfcolumn, List<String> columndrop, List<String> dummies, Map<String, Object> newdata) throws IOException {
	        Gson gson = new Gson();

	        // Convertir el nuevo data a JSON y luego codificarlo
	        String newdataJson = gson.toJson(newdata);

	        // Crear URL con parámetros de consulta
	        String urlString = String.format(
	            "http://localhost:8000/datos?tablename=%s&dfcolumn=%s&cadena1=%s&cadena2=%s",
	            URLEncoder.encode(tablename, "UTF-8"),
	            URLEncoder.encode(dfcolumn, "UTF-8"),
	            URLEncoder.encode(newdataJson, "UTF-8"),
	            URLEncoder.encode(newdataJson, "UTF-8")
	        );

	        // Añadir parámetros de columndrop
	        for (String str : columndrop) {
	            urlString += "&columndrop=" + URLEncoder.encode(str, "UTF-8");
	        }

	        // Añadir parámetros de dummies
	        for (String str : dummies) {
	            urlString += "&dummies=" + URLEncoder.encode(str, "UTF-8");
	        }

	        URL url = new URL(urlString);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");

	        int responseCode = conn.getResponseCode();
	        if (responseCode == 200) {
	            Scanner scanner = new Scanner(conn.getInputStream());
	            String response = scanner.useDelimiter("\\A").next();
	            scanner.close();

	            System.out.println("Respuesta de la API: " + response);
	        } else {
	            System.out.println("Error al llamar a la API: " + responseCode);
	        }
	    }

	    public static void main(String[] args) {
	        try {
	            String tablename = "salario";
	            String dfcolumn = "Salarios";
	            List<String> columndrop = List.of("Nombre","Salario");
	            List<String> dummies = List.of("Género","Departamento");
	            Map<String, Object> newdata = Map.of(
	                "Nombre", "Nuevo",
	                "Edad", 36,
	                "Género", "Femenino",
	                "Departamento", "Finanzas"
	            );

	            llamadaApiGet(tablename, dfcolumn, columndrop, dummies, newdata);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}