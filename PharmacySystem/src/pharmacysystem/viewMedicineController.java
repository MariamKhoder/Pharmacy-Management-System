/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
  /* 
    Created on : Dec 14, 2025, 1:40:35 PM
    Author     : MariamReda
*/

package pharmacysystem;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;

/**
 *
 * @author Al Khat Al Asfar
 */
public class viewMedicineController implements Initializable{
     
    @FXML
    private TextField Medicine_MedicineCompany;

    @FXML
    private TextField Medicine_MedicineID;

    @FXML
    private TextField Medicine_MedicineName;

    @FXML
    private TextField Medicine_MedicinePrice;

    @FXML
    private ComboBox<String> Medicine_MedicineStatus;

    @FXML
    private TextField Medicine_MedicineType;

    @FXML
    private Button Medicine_addButton;

    @FXML
    private Button Medicine_deleteButton;

    @FXML
    private TableView<MedicineData> Medicine_TableView; // تم تغيير الـ '?' إلى <MedicineData>

    @FXML
    private TableColumn<MedicineData, String> Medicine_medicineCompany_column; // تم تغيير الـ '?' إلى <MedicineData, String>

    @FXML
    private TableColumn<MedicineData, String> Medicine_medicineID_column;

    @FXML
    private TableColumn<MedicineData, String> Medicine_medicineName_column;

    @FXML
    private TableColumn<MedicineData, String> Medicine_medicinePrice_column;

    @FXML
    private TableColumn<MedicineData, String> Medicine_medicineStatus_column;

    @FXML
    private TableColumn<MedicineData, String> Medicine_medicineType_column;

    @FXML
    private TextField Medicine_search;

    @FXML
    private Button Medicine_updateButton;

    @FXML
    private Button aboutus_btn;

    @FXML
    private AnchorPane addMedicine_form;

    @FXML
    private Button dashboard_btn;

    @FXML
    private Button purchase_btn;

    @FXML
    private Button setting_btn;

    @FXML
    private Button signout_btn;

    @FXML
    private Button view_btn;
    
    private ObservableList<MedicineData> medicineGetData() {

    ObservableList<MedicineData> listData = FXCollections.observableArrayList();
    String sql = "SELECT * FROM Medicine"; 
    
    
    java.sql.Connection connect = dataBase.getConnection();
    if (connect == null) {
    
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Connection Error");
    alert.setHeaderText(null);
    alert.setContentText("Failed to establish database connection. Check database.java.");
    alert.showAndWait();
    return listData; 
}

    PreparedStatement prepare;
    ResultSet result;

    try {
        prepare = connect.prepareStatement(sql);
        result = prepare.executeQuery();
        MedicineData medicineD;

        while (result.next()) {
            // MedicineData(medicineID, medicineName, medicinePrice, company, type, status)
            medicineD = new MedicineData(result.getString("medicine_id"), 
                                 result.getString("name"), 
                                 result.getString("price"), 
                                 result.getString("company"), 
                                 result.getString("type"), 
                                 result.getString("status"));
            listData.add(medicineD);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return listData;
}
    
    private ObservableList<MedicineData> medicineList;

public void medicineShowData() {
    medicineList = medicineGetData();
    Medicine_TableView.setItems(medicineList);
    
}

public void medicineClearFields() {
    Medicine_MedicineID.setText(""); 
    Medicine_MedicineName.setText("");
    Medicine_MedicinePrice.setText("");
    Medicine_MedicineCompany.setText("");
    Medicine_MedicineType.setText("");
    Medicine_MedicineStatus.getSelectionModel().clearSelection(); 
}

private boolean isMedicineIDExists(String id) {
    String check = "SELECT medicine_id FROM medicine WHERE medicine_id = ?";
    Connection connect = dataBase.getConnection();
    PreparedStatement prepare;
    ResultSet result;

    try {
        prepare = connect.prepareStatement(check);
        prepare.setString(1, id);
        result = prepare.executeQuery();
        return result.next(); 
    } catch (SQLException e) {
        e.printStackTrace();
        return true; 
    }
}
@FXML
public void medicineAdd() {

    
    if (Medicine_MedicineID.getText().isEmpty() 
            || Medicine_MedicineName.getText().isEmpty()
            || Medicine_MedicinePrice.getText().isEmpty()
            || Medicine_MedicineCompany.getText().isEmpty()
            || Medicine_MedicineType.getText().isEmpty()
            || Medicine_MedicineStatus.getSelectionModel().isEmpty()) 
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText("Please fill all blank fields.");
        alert.showAndWait();
        return; 
    }
    
    
    String newID = Medicine_MedicineID.getText();
    if (isMedicineIDExists(newID)) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText("Medicine ID: " + newID + " already exists!");
        alert.showAndWait();
        return; 
    }
    
    
    String insertData = "INSERT INTO medicine (medicine_id, name, price, company, type, status) "
            + "VALUES (?, ?, ?, ?, ?, ?)"; // الآن لدينا 6 علامات استفهام
    
    Connection connect = dataBase.getConnection(); 
    PreparedStatement prepare;

    try {
        prepare = connect.prepareStatement(insertData);
        
        
        prepare.setString(1, newID); 
        
        prepare.setString(2, Medicine_MedicineName.getText()); 
        prepare.setString(3, Medicine_MedicinePrice.getText());
        prepare.setString(4, Medicine_MedicineCompany.getText());
        prepare.setString(5, Medicine_MedicineType.getText());
        
        String statusValue = Medicine_MedicineStatus.getSelectionModel().getSelectedItem();
        prepare.setString(6, statusValue); 

        prepare.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText("Successfully Added!");
        alert.showAndWait();

        medicineShowData(); 
        medicineClearFields();
        
    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("SQL Error");
        alert.setHeaderText(null);
        alert.setContentText("Database Error: " + e.getMessage());
        alert.showAndWait();
    }
}
    @FXML
public void medicineSelect() {
    
    MedicineData medicineD = Medicine_TableView.getSelectionModel().getSelectedItem();
    int num = Medicine_TableView.getSelectionModel().getSelectedIndex();
    
    
    if ((num - 1) < -1) {
        return;
    }
    
    
    Medicine_MedicineID.setText(medicineD.getMedicineID());
    Medicine_MedicineName.setText(medicineD.getMedicineName());
    Medicine_MedicinePrice.setText(medicineD.getMedicinePrice());
    Medicine_MedicineCompany.setText(medicineD.getCompany());
    Medicine_MedicineType.setText(medicineD.getType());
    
    
    Medicine_MedicineStatus.setValue(medicineD.getStatus());
}

@FXML
public void medicineUpdate() {
    
    if (Medicine_MedicineID.getText().isEmpty()
            || Medicine_MedicineName.getText().isEmpty()
            || Medicine_MedicinePrice.getText().isEmpty()
            || Medicine_MedicineCompany.getText().isEmpty()
            || Medicine_MedicineType.getText().isEmpty()
            || Medicine_MedicineStatus.getSelectionModel().isEmpty()) 
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText("Please select and fill all fields to update!");
        alert.showAndWait();
        return; 
    }
    
    
    String updateData = "UPDATE medicine SET name = ?, price = ?, company = ?, type = ?, status = ? "
                      + "WHERE medicine_id = ?";
    
    Connection connect = dataBase.getConnection(); 
    PreparedStatement prepare;

    try {
        prepare = connect.prepareStatement(updateData);
        
        
        prepare.setString(1, Medicine_MedicineName.getText()); 
        prepare.setString(2, Medicine_MedicinePrice.getText());
        prepare.setString(3, Medicine_MedicineCompany.getText());
        prepare.setString(4, Medicine_MedicineType.getText());
        prepare.setString(5, Medicine_MedicineStatus.getSelectionModel().getSelectedItem());
        
        
        prepare.setString(6, Medicine_MedicineID.getText());

        int result = prepare.executeUpdate();

        if (result > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Message");
            alert.setHeaderText(null);
            alert.setContentText("Successfully Updated!");
            alert.showAndWait();

            medicineShowData();      
            medicineClearFields();   
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Update failed! Please ensure the ID exists.");
            alert.showAndWait();
        }

    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("SQL Error");
        alert.setHeaderText(null);
        alert.setContentText("Database Error during Update: " + e.getMessage());
        alert.showAndWait();
    }
}

    
@FXML
public void medicineDelete() {
    
    if (Medicine_MedicineID.getText().isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText("Please select the medicine to delete.");
        alert.showAndWait();
        return;
    }
    
    
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirmation Message");
    confirmation.setHeaderText(null);
    confirmation.setContentText("Are you sure you want to delete Medicine ID: " + Medicine_MedicineID.getText() + "?");
    
    
    java.util.Optional<javafx.scene.control.ButtonType> option = confirmation.showAndWait();

    if (option.get().equals(javafx.scene.control.ButtonType.OK)) {
        
        
        String deleteData = "DELETE FROM medicine WHERE medicine_id = ?";
        Connection connect = dataBase.getConnection(); 
        PreparedStatement prepare;

        try {
            prepare = connect.prepareStatement(deleteData);
            prepare.setString(1, Medicine_MedicineID.getText());
            
            int result = prepare.executeUpdate();

            if (result > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Deleted!");
                alert.showAndWait();

                medicineShowData();
                medicineClearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQL Error");
            alert.setHeaderText(null);
            alert.setContentText("Database Error during Delete: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
    Medicine_medicineID_column.setCellValueFactory(new PropertyValueFactory<>("medicineID"));
    Medicine_medicineName_column.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
    Medicine_medicinePrice_column.setCellValueFactory(new PropertyValueFactory<>("medicinePrice"));
    Medicine_medicineCompany_column.setCellValueFactory(new PropertyValueFactory<>("company"));
    Medicine_medicineType_column.setCellValueFactory(new PropertyValueFactory<>("type"));
    Medicine_medicineStatus_column.setCellValueFactory(new PropertyValueFactory<>("status"));
    String[] listStatus = {"Available", "Not Available"};
    ObservableList<String> statusList = FXCollections.observableArrayList(listStatus);
    Medicine_MedicineStatus.setItems(statusList); // <--- يجب وضع هذا السطر
    
    
    medicineShowData(); 
}

    
}
