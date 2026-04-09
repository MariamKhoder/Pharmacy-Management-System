/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pharmacysystem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Mlk
 */
public class StartController implements Initializable {
    
    @FXML
    private Text flashTextBtn;
   
    @FXML
    public void startFlashingThread() {

        Thread flashThread = new Thread(() -> {
            try {
                String[] loading = {
                        "Loading",
                        "Loading.",
                        "Loading..",
                        "Loading..."
                };

                for (int i = 0; i < 12; i++) {
                    int index = i % loading.length;

                    Platform.runLater(() ->
                            flashTextBtn.setText(loading[index])
                    );

                    Thread.sleep(400);
                }

               
                Platform.runLater(this::openSignup);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        flashThread.setDaemon(true);
        flashThread.start();
    }
       @FXML
     private void openSignup() {
    try {
        Parent root = FXMLLoader.load(
                getClass().getResource("SignUp.fxml")
        );

        Stage stage = (Stage) flashTextBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         flashTextBtn.setText("");
        startFlashingThread();
    }
}
