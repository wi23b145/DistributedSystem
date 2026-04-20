package at.fhtw.energy.energygui;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
public class EnergyGuiController {

    @FXML private Label lblCommunityPool;
    @FXML private Label lblGridPortion;
    @FXML private DatePicker startPicker;
    @FXML private DatePicker endPicker;
    @FXML private TextArea txtHistorical;

    private final HttpClient client = HttpClient.newHttpClient();
    private static final String API_BASE = "http://localhost:8080/energy";
    private Timeline autoRefresh;

    @FXML
    public void initialize() {
        autoRefresh = new Timeline(
                new KeyFrame(Duration.seconds(20), e -> onRefresh())
        );
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }
    @FXML
    public void onRefresh() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/current"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            // einfaches JSON parsen
            double depleted = extractDouble(body, "communityDepleted");
            double grid = extractDouble(body, "gridPortion");

            lblCommunityPool.setText(depleted + "%");
            lblGridPortion.setText(grid + "%");

        } catch (Exception e) {
            lblCommunityPool.setText("Fehler!");
            lblGridPortion.setText("Fehler!");
        }
    }

    @FXML
    public void onShowData() {
        if (startPicker.getValue() == null || endPicker.getValue() == null) {
            txtHistorical.setText("Bitte Start und End Datum auswählen!");
            return;
        }

        try {
            String start = startPicker.getValue().atStartOfDay().toString();
            String end = endPicker.getValue().atStartOfDay().toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/historical?start=" + start + "&end=" + end))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            txtHistorical.setText(response.body());

        } catch (Exception e) {
            txtHistorical.setText("Fehler beim Laden der Daten!");
        }
    }

    private double extractDouble(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return 0;
        int start = idx + search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Double.parseDouble(json.substring(start, end).trim());
    }
}

