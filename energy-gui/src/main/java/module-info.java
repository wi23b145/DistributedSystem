module at.fhtw.energy.energygui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens at.fhtw.energy.energygui to javafx.fxml, com.fasterxml.jackson.databind;
    opens at.fhtw.energy.energygui.dto to com.fasterxml.jackson.databind;
    exports at.fhtw.energy.energygui;
}