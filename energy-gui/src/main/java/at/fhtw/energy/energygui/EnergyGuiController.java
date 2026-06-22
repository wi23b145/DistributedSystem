package at.fhtw.energy.energygui;

import at.fhtw.energy.energygui.dto.CurrentEnergyDto;
import at.fhtw.energy.energygui.dto.HistoricalEnergyDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
// JavaFX Controller - handles all GUI logic, fetches data from energy-api
public class EnergyGuiController {

    @FXML private Label lblCommunityPool;
    @FXML private Label lblGridPortion;
    @FXML private Label lblLastUpdate;
    @FXML private DatePicker startPicker;
    @FXML private DatePicker endPicker;
    @FXML private FlowPane flowHistorical;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
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
    // Called by Refresh button and auto-refresh timer
    @FXML
    public void onRefresh() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/current"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            CurrentEnergyDto dto = mapper.readValue(response.body(), CurrentEnergyDto.class);

            lblCommunityPool.setText(String.format("%.2f%%", dto.communityDepleted));
            lblGridPortion.setText(String.format("%.2f%%", dto.gridPortion));
            lblLastUpdate.setText("Letzte Aktualisierung: " + dto.hour);

        } catch (Exception e) {
            lblCommunityPool.setText("Fehler!");
            lblGridPortion.setText("Fehler!");
            lblLastUpdate.setText("Keine Verbindung zur API");
        }
    }
    // Called by "Anzeigen" button
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
            // DatePicker returns LocalDate - atStartOfDay() adds T00:00 for API compatibility
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
    // Builds a visual card
    private void buildHistoricalCards(String json) {
        flowHistorical.getChildren().clear();

        if (json.equals("[]")) {
            Label msg = new Label("📭 Keine Daten für den gewählten Zeitraum.");
            msg.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 13;");
            flowHistorical.getChildren().add(msg);
            return;
        }

        try {
            // Jackson deserializes JSON array into DTO array
            HistoricalEnergyDto[] entries = mapper.readValue(json, HistoricalEnergyDto[].class);

            Arrays.sort(entries, (a, b) -> a.hour.compareTo(b.hour));

            for (HistoricalEnergyDto entry : entries) {
                boolean hasData = entry.communityProduced > 0
                        || entry.communityUsed > 0
                        || entry.gridUsed > 0;
                boolean hasGrid = entry.gridUsed > 0;
                String borderColor = hasGrid ? "#f7931a" : hasData ? "#64ffda" : "#e94560";

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

                Label lblHour = new Label("⏱ " + entry.hour);
                lblHour.setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold; -fx-font-size: 11;");

                Label lblProduced = new Label(String.format("⚡ Produziert:  %.4f kWh", entry.communityProduced));
                lblProduced.setStyle("-fx-text-fill: #64ffda; -fx-font-family: 'Courier New'; -fx-font-size: 11;");

                Label lblUsed = new Label(String.format("🔋 Verbraucht:  %.4f kWh", entry.communityUsed));
                lblUsed.setStyle("-fx-text-fill: #ccd6f6; -fx-font-family: 'Courier New'; -fx-font-size: 11;");

                Label lblGrid = new Label(String.format("🔌 Grid:        %.4f kWh", entry.gridUsed));
                String gridColor = hasGrid ? "#f7931a" : "#8892b0";
                lblGrid.setStyle("-fx-text-fill: " + gridColor + "; -fx-font-family: 'Courier New'; -fx-font-size: 11;");

                card.getChildren().addAll(lblHour, lblProduced, lblUsed, lblGrid);
                flowHistorical.getChildren().add(card);
            }

        } catch (Exception e) {
            Label err = new Label("❌ Fehler beim Laden der Daten!");
            err.setStyle("-fx-text-fill: #e94560; -fx-font-size: 13;");
            flowHistorical.getChildren().add(err);
        }
    }
}