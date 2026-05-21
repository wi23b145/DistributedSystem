package at.fhtw.energy.energyproducer.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WeatherService {

    private static final String WEATHER_URL =
            "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=48.2082&longitude=16.3738" +
                    "&current=cloud_cover" +
                    "&timezone=Europe%2FVienna";

    private final HttpClient client = HttpClient.newHttpClient();

    public double getCloudCover() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WEATHER_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            // extract cloud_cover value
            int idx = body.indexOf("\"cloud_cover\":");
            if (idx == -1) return 50.0;
            int start = idx + 14;
            int end = body.indexOf(",", start);
            if (end == -1) end = body.indexOf("}", start);
            return Double.parseDouble(body.substring(start, end).trim());

        } catch (Exception e) {
            return 50.0; // default wenn API nicht erreichbar
        }
    }
}