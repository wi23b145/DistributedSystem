package at.fhtw.energy.energygui;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class EnergyGuiController {

    @FXML private Label lblCommunityPool;
    @FXML private Label lblGridPortion;
    @FXML private Label lblLastUpdate;
    @FXML private DatePicker startPicker;
    @FXML private DatePicker endPicker;
    @FXML private FlowPane flowHistorical;

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
        onRefresh();
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
            double depleted = extractDouble(body, "communityDepleted");
            double grid = extractDouble(body, "gridPortion");
            String hour = extractString(body, "hour");

            lblCommunityPool.setText(String.format("%.2f%%", depleted));
            lblGridPortion.setText(String.format("%.2f%%", grid));
            lblLastUpdate.setText("Letzte Aktualisierung: " + hour);

        } catch (Exception e) {
            lblCommunityPool.setText("Fehler!");
            lblGridPortion.setText("Fehler!");
            lblLastUpdate.setText("Keine Verbindung zur API");
        }
    }

    @FXML
    public void onShowData() {
        if (startPicker.getValue() == null || endPicker.getValue() == null) {
            flowHistorical.getChildren().clear();
            Label msg = new Label("⚠ Bitte Start und End Datum auswählen!");
            msg.setStyle("-fx-text-fill: #e94560;");
            flowHistorical.getChildren().add(msg);
            return;
        }

        flowHistorical.getChildren().clear();
        Label loading = new Label("⏳ Lade Daten...");
        loading.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 13;");
        flowHistorical.getChildren().add(loading);

        try {
            String start = startPicker.getValue().atStartOfDay().toString();
            String end = endPicker.getValue().atStartOfDay().toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/historical?start=" + start + "&end=" + end))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            buildHistoricalCards(response.body());

        } catch (Exception e) {
            flowHistorical.getChildren().clear();
            Label err = new Label("❌ Fehler beim Laden der Daten!");
            err.setStyle("-fx-text-fill: #e94560; -fx-font-size: 13;");
            flowHistorical.getChildren().add(err);
        }
    }

    private void buildHistoricalCards(String json) {
        flowHistorical.getChildren().clear();

        if (json.equals("[]")) {
            Label msg = new Label("📭 Keine Daten für den gewählten Zeitraum.");
            msg.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 13;");
            flowHistorical.getChildren().add(msg);
            return;
        }

        String[] entries = json.replace("[{", "").replace("}]", "").split("\\},\\{");

        // Chronologisch sortieren
        Arrays.sort(entries, (a, b) -> {
            String hourA = extractString(a, "hour");
            String hourB = extractString(b, "hour");
            return hourA.compareTo(hourB);
        });

        for (String entry : entries) {
            String hour = extractString(entry, "hour");
            double produced = extractDouble(entry, "communityProduced");
            double used = extractDouble(entry, "communityUsed");
            double grid = extractDouble(entry, "gridUsed");

            // Kartenfarbe je nach Datenlage
            boolean hasData = produced > 0 || used > 0 || grid > 0;
            String borderColor = hasData ? "#64ffda" : "#e94560";

            VBox card = new VBox(6);
            card.setStyle(
                    "-fx-background-color: #16213e;" +
                            "-fx-padding: 12;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: " + borderColor + ";" +
                            "-fx-border-radius: 6;" +
                            "-fx-border-width: 1;" +
                            "-fx-min-width: 200;" +
                            "-fx-max-width: 200;"
            );

            Label lblHour = new Label("⏱ " + hour);
            lblHour.setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold; -fx-font-size: 11;");

            Label lblProduced = new Label(String.format("⚡ Produziert:  %.4f kWh", produced));
            lblProduced.setStyle("-fx-text-fill: #64ffda; -fx-font-family: 'Courier New'; -fx-font-size: 11;");

            Label lblUsed = new Label(String.format("🔋 Verbraucht:  %.4f kWh", used));
            lblUsed.setStyle("-fx-text-fill: #ccd6f6; -fx-font-family: 'Courier New'; -fx-font-size: 11;");

            Label lblGrid = new Label(String.format("🔌 Grid:        %.4f kWh", grid));
            lblGrid.setStyle("-fx-text-fill: #f7931a; -fx-font-family: 'Courier New'; -fx-font-size: 11;");

            card.getChildren().addAll(lblHour, lblProduced, lblUsed, lblGrid);
            flowHistorical.getChildren().add(card);
        }
    }

    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "-";
        int start = idx + search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private double extractDouble(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return 0;
        int start = idx + search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        try {
            return Double.parseDouble(json.substring(start, end).trim());
        } catch (Exception e) {
            return 0;
        }
    }
}