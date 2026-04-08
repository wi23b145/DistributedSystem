module at.fhtw.energy.energygui {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.fhtw.energy.energygui to javafx.fxml;
    exports at.fhtw.energy.energygui;
}