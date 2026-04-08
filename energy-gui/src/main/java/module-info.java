module at.fhtw.energy.energygui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens at.fhtw.energy.energygui to javafx.fxml;
    exports at.fhtw.energy.energygui;
}