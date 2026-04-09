/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pharmacysystem;
import com.sun.jdi.connect.spi.Connection;
import java.io.IOException;
import java.sql.SQLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author Mlk
 */
public class SignController implements Initializable {
       @FXML
    private TextField PhoneBtn;
        @FXML
    private TextField addBtn;
        @FXML
    private Button closeBtn;
    @FXML
    private Button logBtn;
      @FXML
    private TextField nameBtn;

    @FXML
    private TextField passField;

    @FXML
    private Button signBtn;

    @FXML
    private TextField userField;
    
     private PreparedStatement prepare;
    private java.sql.Connection connect;
    private ResultSet result;

    @FXML
private void signUp(ActionEvent event) {

    String name = nameBtn.getText();
    String username = userField.getText();
    String password = passField.getText();
    String phone = PhoneBtn.getText();
    String address = addBtn.getText();

    // Regex password
    String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$";

    if (name.isEmpty() || username.isEmpty() || password.isEmpty()
            || phone.isEmpty() || address.isEmpty()) {

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Missing Data");
        alert.setHeaderText(null);
        alert.setContentText("Please fill all fields");
        alert.showAndWait();
        return;
    }

    if (!password.matches(passwordRegex)) {

        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Password");
        alert.setHeaderText("Weak Password");
        alert.setContentText(
                "Password must contain:\n" +
                "- Letters\n" +
                "- Numbers\n" +
                "- Special characters\n" +
                "- At least 8 characters"
        );
        alert.showAndWait();
        return;
    }

    String sql = "INSERT INTO Pharmacist (name, user_name, password, phone_no, address) VALUES (?, ?, ?, ?, ?)";
      connect = dataBase.getConnection();
      if (connect == null) {
     Alert alert = new Alert(AlertType.INFORMATION);
     alert.setTitle("Information Message");
     alert.setHeaderText(null);
     alert.setContentText("Failed to establish database connection. Check database.java.");
     alert.showAndWait();

        }
   try   {
    prepare = connect.prepareStatement(sql);
    prepare.setString(1, name);
    prepare.setString(2, username);
    prepare.setString(3, password);
    prepare.setString(4, phone);
    prepare.setString(5, address);

    prepare.executeUpdate();
    
    Alert alert = new Alert(AlertType.INFORMATION);
     alert.setTitle("Information Message");
     alert.setHeaderText(null);
     alert.setContentText("Successfully SignUp");
     alert.showAndWait();

   } catch(SQLException e) {
    e.printStackTrace();
}

}

    
    @FXML
private void goToLogin(ActionEvent event) throws IOException {

   
    Parent root = FXMLLoader.load(
        getClass().getResource("FXMLDocument.fxml")
    );

   
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    Scene scene = new Scene(root);
    stage.setScene(scene);

    stage.show();
}
@FXML
 public void exitBtn(ActionEvent event) throws IOException{
        System.exit(0);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

}


