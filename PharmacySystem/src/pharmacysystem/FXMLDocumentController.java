/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package pharmacysystem;

import java.io.IOException;
import java.util.ResourceBundle;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;



/**
 *
 * @author Mlk
 */
public class FXMLDocumentController implements Initializable {
     @FXML
    private Button exitBut;
      @FXML
    private Button loginBtn;

    @FXML
    private AnchorPane main_form;

    @FXML
    private TextField password;
    @FXML
    private Button returnsignupBtn;

    @FXML
    private TextField username;
    
    private PreparedStatement prepare;
    private Connection connect;
    private ResultSet result;
    
    private double x= 0;
    private double y= 0;
    @FXML
    public void loginAdmin(){
        
        String sql = "SELECT * FROM pharmacist WHERE user_name = ? and password = ?";
        connect = dataBase.getConnection();
         if (connect == null) {
           Alert alert = new Alert(AlertType.INFORMATION);
             alert.setTitle("Information Message");
             alert.setHeaderText(null);
             alert.setContentText("Failed to establish database connection. Check database.java.");
             alert.showAndWait();

        }
        try{
            
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, username.getText());
            prepare.setString(2, password.getText());
            
            result = prepare.executeQuery();
            
            Alert alert;
            
            if(username.getText().isEmpty() || password.getText().isEmpty()){
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            }else{
                if(result.next()){
                    // TAKE THE ID OF USER(pharmacist)
                  getData.adminId = result.getInt("pharmacist_id");
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Login");
                    alert.showAndWait();
                    
                    // HIDE YOUR LOGIN FORM
                     loginBtn.getScene().getWindow().hide();
                    // LINK YOUR DASHBOARD FORM
                  Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
               
                  
                  stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();
                       
                }else{
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong Username/Password");
                    alert.showAndWait();
                }
            }
        } catch (Exception ex) {
              Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
          }
    }
      @FXML
      public void backToSignUp(ActionEvent event) throws IOException{
             Parent root = FXMLLoader.load(getClass().getResource("SignUp.fxml")
    );
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    
    Scene scene = new Scene(root);
    stage.setScene(scene);

    stage.show();
      }
    @FXML
    public void exitButton(ActionEvent event) throws IOException{
        System.exit(0);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }  // TODO
        
    
}
    
