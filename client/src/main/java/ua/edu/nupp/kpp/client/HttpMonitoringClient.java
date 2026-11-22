package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpMonitoringClient {
    private static final String BASE = "http://localhost:8080/field-monitoring-server";

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public HttpMonitoringClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("Помилка запиту: HTTP " + response.statusCode() + "\n" + response.body());
        }
        return response.body();
    }

    private String post(String endpoint, Map<String, String> params) throws IOException, InterruptedException {
        StringBuilder body = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                body.append("&");
            }
            body.append(enc(entry.getKey()))
                    .append("=")
                    .append(enc(entry.getValue()));
            first = false;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("Помилка запиту: HTTP " + response.statusCode() + "\n" + response.body());
        }
        return response.body();
    }

    private <T> List<T> readList(String path, TypeReference<List<T>> typeRef) {
        try {
            String json = get(path);
            return mapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося отримати дані з сервера: " + e.getMessage(), e);
        }
    }

    // --- Field ---
    public List<Field> getFields() {
        return readList("/fields", new TypeReference<List<Field>>() {});
    }

    public void addField(String name, double area, String cropType) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("name", name);
        params.put("area", Double.toString(area));
        params.put("cropType", cropType);
        post("/fields", params);
    }

    public void updateField(long id, String name, double area, String cropType) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", Long.toString(id));
        params.put("name", name);
        params.put("area", Double.toString(area));
        params.put("cropType", cropType);
        post("/fields", params);
    }
    public void deleteField(long id) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "delete");
        params.put("id", Long.toString(id));
        post("/fields", params);
    }

    // --- Observation ---
    public List<Observation> getObservations() {
        return readList("/observations", new TypeReference<List<Observation>>() {});
    }

    public void addObservation(long fieldId, String obsDate, double f1, double f2, double f3)
            throws IOException, InterruptedException {
        post("/observations", observationParams("add", null, fieldId, obsDate, f1, f2, f3));
    }

    public void updateObservation(long id, long fieldId, String obsDate, double f1, double f2, double f3)
            throws IOException, InterruptedException {
        post("/observations", observationParams("update", id, fieldId, obsDate, f1, f2, f3));
    }

    public void deleteObservation(long id) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "delete");
        params.put("id", Long.toString(id));
        post("/observations", params);
    }

    private Map<String, String> observationParams(String action, Long id, long fieldId, String obsDate,
                                                  double f1, double f2, double f3) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", action);
        if (id != null) {
            params.put("id", Long.toString(id));
        }
        params.put("fieldId", Long.toString(fieldId));
        params.put("obsDate", obsDate);
        params.put("f1", Double.toString(f1));
        params.put("f2", Double.toString(f2));
        params.put("f3", Double.toString(f3));
        return params;
    }

    // --- Problem ---
    public List<Problem> getProblems() {
        return readList("/problems", new TypeReference<List<Problem>>() {});
    }

    public void addProblem(long observationId) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("observationId", Long.toString(observationId));
        post("/problems", params);
    }

    public void updateProblem(long id, long observationId) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", Long.toString(id));
        params.put("observationId", Long.toString(observationId));
        post("/problems", params);
    }

    public void deleteProblem(long id) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "delete");
        params.put("id", Long.toString(id));
        post("/problems", params);
    }

    public void changeProblemState(long id, ProblemState state) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "transition");
        params.put("id", Long.toString(id));
        params.put("state", state.name());
        post("/problems", params);
    }

    // --- TreatmentAction ---
    public List<TreatmentAction> getActions() {
        return readList("/actions", new TypeReference<List<TreatmentAction>>() {});
    }

    public void addAction(long problemId, String description, String plannedDate)
            throws IOException, InterruptedException {
        post("/actions", actionParams("add", null, problemId, description, plannedDate));
    }

    public void updateAction(long id, long problemId, String description, String plannedDate)
            throws IOException, InterruptedException {
        post("/actions", actionParams("update", id, problemId, description, plannedDate));
    }
    public void deleteAction(long id) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "delete");
        params.put("id", Long.toString(id));
        post("/actions", params);
    }

    private Map<String, String> actionParams(String action, Long id, long problemId,
                                             String description, String plannedDate) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", action);
        if (id != null) {
            params.put("id", Long.toString(id));
        }
        params.put("problemId", Long.toString(problemId));
        params.put("description", description);
        params.put("plannedDate", plannedDate);
        return params;
    }
}

