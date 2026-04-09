package pharmacysystem;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;


public class SettingsController implements Initializable {

    @FXML
    private ComboBox<Integer> fontSizeCombo;

    @FXML
    private ComboBox<String> themeCombo;

    @FXML
    private Button testDBBtn;

    @FXML
    private Label dbStatusLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Font sizes
        fontSizeCombo.getItems().addAll(10, 12, 14, 16, 18, 20, 24, 28, 32);
        fontSizeCombo.setValue(14);

        fontSizeCombo.setOnAction(e -> {
            Scene scene = fontSizeCombo.getScene();
            scene.getRoot().setStyle("-fx-font-size: " + fontSizeCombo.getValue() + "px;");
        });

        // Theme
        themeCombo.getItems().addAll("Light", "Dark");
        themeCombo.setValue("Light");
        

        themeCombo.setOnAction(e -> applyTheme());

        // DB Test
        testDBBtn.setOnAction(e -> updateDBStatus());

        updateDBStatus();
    }
    
    @FXML
private StackPane rootPane;

@FXML
private AnchorPane settings_form;

public void toggleDarkMode(boolean enable) {
    if (enable) {
        if (!rootPane.getStyleClass().contains("dark")) {
            rootPane.getStyleClass().add("dark");
        }
    } else {
        rootPane.getStyleClass().remove("dark");
    }
}


    private void applyTheme() {
        Scene scene = themeCombo.getScene();
        scene.getStylesheets().clear();

        if (themeCombo.getValue().equals("Dark")) {
            scene.getStylesheets().add(getClass().getResource("darkTheme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("lightTheme.css").toExternalForm());
        }
    }

    private void updateDBStatus() {
        try (Connection conn = dataBase.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                dbStatusLabel.setText("Status: Connected");
                dbStatusLabel.setStyle("-fx-text-fill: green;");
            } else {
                dbStatusLabel.setText("Status: Disconnected");
                dbStatusLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            dbStatusLabel.setText("Status: Error");
            dbStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
