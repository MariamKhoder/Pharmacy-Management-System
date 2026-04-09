package pharmacysystem;

import java.io.FileOutputStream;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DashboardController implements Initializable {

    // ================= VIEW MEDICINE FXML =================
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
    private TableView<MedicineData> Medicine_TableView;
    @FXML
    private TableColumn<MedicineData, String> Medicine_medicineCompany_column;
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
    private AnchorPane addMedicine_form;
    @FXML
    private AnchorPane medicineView_form;
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
    @FXML
    private Button aboutus_btn;

    private ObservableList<MedicineData> medicineList;
    
    private double x= 0;
    private double y= 0;

    // ================= PURCHASE MEDICINE FXML =================
    @FXML
    private AnchorPane purchase_form;
    @FXML
    private ComboBox<String> purchase_type;
    @FXML
    private ComboBox<Integer> purchase_medicineID;
    @FXML
    private ComboBox<String> purchase_brand;
    @FXML
    private ComboBox<String> purchase_productName;
    @FXML
    private TextField purchase_amount;
    @FXML
    private Label purchase_total;
    @FXML
    private Label purchase_balance;
    @FXML
    private TableView<customerData> purchase_tableView;
    @FXML
    private TableColumn<customerData, Integer> purchase_co_medicineId;
    @FXML
    private TableColumn<customerData, String> purchase_co_brand;
    @FXML
    private TableColumn<customerData, String> purchase_co_productName;
    @FXML
    private TableColumn<customerData, String> purchase_co_type;
    @FXML
    private TableColumn<customerData, Integer> purchase_co_qty;
    @FXML
    private TableColumn<customerData, Double> purchase_co_price;
    @FXML
    private Spinner<Integer> purchase_qty_spinner;

    private ObservableList<customerData> cart = FXCollections.observableArrayList();
    private double totalPrice = 0;

    // ==============Settings =================
    @FXML
    private AnchorPane settings_form;

    @FXML
    private ComboBox<String> fontSizeCombo;
    @FXML
    private ComboBox<String> themeCombo;
    @FXML
    private Button testDBBtn;
    @FXML
    private Label dbStatusLabel;
    @FXML
    private Button saveSettingsBtn;
    @FXML
    private AnchorPane root;

    // =============== Abou Us ======================
    @FXML
    private AnchorPane about_form;

    // ================= INITIALIZE =================
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // dashboard setup
        loadDashboardData();
        // View Medicine Setup
        Medicine_medicineID_column.setCellValueFactory(new PropertyValueFactory<>("medicineID"));
        Medicine_medicineName_column.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        Medicine_medicinePrice_column.setCellValueFactory(new PropertyValueFactory<>("medicinePrice"));
        Medicine_medicineCompany_column.setCellValueFactory(new PropertyValueFactory<>("company"));
        Medicine_medicineType_column.setCellValueFactory(new PropertyValueFactory<>("type"));
        Medicine_medicineStatus_column.setCellValueFactory(new PropertyValueFactory<>("status"));
        String[] listStatus = {"available", "not Available"};
        Medicine_MedicineStatus.setItems(FXCollections.observableArrayList(listStatus));
        medicineShowData();

        // Purchase Medicine Setup
        setupPurchaseTable();
        loadTypes();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        purchase_qty_spinner.setValueFactory(valueFactory);

        showDashboard();

        // Settings
        fontSizeCombo.setItems(FXCollections.observableArrayList("12", "14", "16", "18"));
        fontSizeCombo.setOnAction(e -> {
            Scene scene = fontSizeCombo.getScene();
            scene.getRoot().setStyle("-fx-font-size: " + fontSizeCombo.getValue() + "px;");
        });

        themeCombo.setItems(
                FXCollections.observableArrayList("Light", "Dark")
        );

        themeCombo.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> applyTheme(newVal));

        testDBBtn.setOnAction(e -> {
            try (Connection conn = dataBase.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    dbStatusLabel.setText("Connected");
                    dbStatusLabel.setStyle("-fx-text-fill: green;");
                }
            } catch (Exception ex) {
                dbStatusLabel.setText("Error");
                dbStatusLabel.setStyle("-fx-text-fill: red;");
            }
        });

    }

    // ================= VIEW MEDICINE METHODS =================
    private ObservableList<MedicineData> medicineGetData() {
        ObservableList<MedicineData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Medicine";
        Connection connect = dataBase.getConnection();
        if (connect == null) {
            showAlert("Connection Error", "Failed to establish database connection. Check database.java.");
            return listData;
        }
        try {
            PreparedStatement prepare = connect.prepareStatement(sql);
            ResultSet result = prepare.executeQuery();
            while (result.next()) {
                MedicineData medicineD = new MedicineData(
                        result.getString("medicine_id"),
                        result.getString("name"),
                        result.getString("price"),
                        result.getString("company"),
                        result.getString("type"),
                        result.getString("status")
                );
                listData.add(medicineD);
            }
        } catch (SQLException e) {
            
        }
        return listData;
    }

    //==================Regex=======================
    private boolean isValidNameOrCompany(String text) {
        String regex = "^[a-zA-Z][a-zA-Z0-9\\s]{0,99}$";
        return java.util.regex.Pattern.matches(regex, text);
    }

    private boolean isValidID(String text) {
        String regex = "^[1-9][0-9]*$";
        return java.util.regex.Pattern.matches(regex, text);
    }

    private boolean isValidPrice(String text) {
        try {
            double price = Double.parseDouble(text);
            if (price > 0) {
                String regex = "^\\d+(\\.\\d+)?$";
                return java.util.regex.Pattern.matches(regex, text);
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // ================= COMMON ALERT =================

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearActiveNavButtons() {
        Button[] buttons = {dashboard_btn, view_btn, purchase_btn, aboutus_btn, setting_btn};

        for (Button btn : buttons) {
            if (btn != null) {
                btn.getStyleClass().remove("active-nav-btn");
            }
        }
    }

    public void medicineShowData() {
        medicineList = medicineGetData();

        FilteredList<MedicineData> filteredData = new FilteredList<>(medicineList, b -> true);

        Medicine_search.textProperty().addListener((observable, oldValue, newValue) -> {
            final String searchKeyword = (newValue == null) ? "" : newValue.trim();

            try {
                final String regexPattern = "(?i).*" + java.util.regex.Pattern.quote(searchKeyword) + ".*";
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regexPattern);

                filteredData.setPredicate(medicine -> {
                    if (searchKeyword.isEmpty()) {
                        return true;
                    }

                    return pattern.matcher(medicine.getMedicineID()).matches()
                            || pattern.matcher(medicine.getMedicineName()).matches()
                            || pattern.matcher(medicine.getCompany()).matches()
                            || pattern.matcher(medicine.getType()).matches();
                });
            } catch (java.util.regex.PatternSyntaxException e) {
                System.err.println("Invalid search pattern (Regex error): " + e.getMessage());
                filteredData.setPredicate(medicine -> true);
            }
        });

        Medicine_TableView.setItems(filteredData);
    }

    private void medicineClearFields() {
        Medicine_MedicineID.clear();
        Medicine_MedicineName.clear();
        Medicine_MedicinePrice.clear();
        Medicine_MedicineCompany.clear();
        Medicine_MedicineType.clear();
        Medicine_MedicineStatus.getSelectionModel().clearSelection();
    }

    private boolean isMedicineIDExists(String id) {
        String check = "SELECT medicine_id FROM medicine WHERE medicine_id = ?";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(check)) {
            prepare.setString(1, id);
            ResultSet result = prepare.executeQuery();
            return result.next();
        } catch (SQLException e) {
            
            return true;
        }
    }

    @FXML
    public void medicineAdd() {
        if (Medicine_MedicineID.getText().isEmpty() || Medicine_MedicineName.getText().isEmpty()
                || Medicine_MedicinePrice.getText().isEmpty() || Medicine_MedicineCompany.getText().isEmpty()
                || Medicine_MedicineType.getText().isEmpty() || Medicine_MedicineStatus.getSelectionModel().isEmpty()) {
            showAlert("Error", "Please fill all blank fields.");
            return;
        }

        if (!isValidID(Medicine_MedicineID.getText())) {
            showAlert("Validation Error", "Medicine ID must be a positive integer.");
            return;
        }
        if (!isValidNameOrCompany(Medicine_MedicineName.getText())) {
            showAlert("Validation Error", "Medicine Name contains invalid characters. Use letters and numbers only.");
            return;
        }
        if (!isValidNameOrCompany(Medicine_MedicineCompany.getText())) {
            showAlert("Validation Error", "Company name contains invalid characters. Use letters and numbers only.");
            return;
        }
        if (!isValidNameOrCompany(Medicine_MedicineType.getText())) {
            showAlert("Validation Error", "Type contains invalid characters. Use letters and numbers only.");
            return;
        }
        if (!isValidPrice(Medicine_MedicinePrice.getText())) {
            showAlert("Validation Error", "Price must be a valid positive number.");
            return;
        }

        String newID = Medicine_MedicineID.getText();
        if (isMedicineIDExists(newID)) {
            showAlert("Error", "Medicine ID: " + newID + " already exists!");
            return;
        }

        String insertData = "INSERT INTO medicine (medicine_id, name, price, company, type, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(insertData)) {
            prepare.setString(1, newID);
            prepare.setString(2, Medicine_MedicineName.getText());
            prepare.setString(3, Medicine_MedicinePrice.getText());
            prepare.setString(4, Medicine_MedicineCompany.getText());
            prepare.setString(5, Medicine_MedicineType.getText());
            prepare.setString(6, Medicine_MedicineStatus.getSelectionModel().getSelectedItem());
            prepare.executeUpdate();
            showAlert("Information", "Successfully Added!");
            medicineShowData();
            medicineClearFields();
            loadDashboardData();
        } catch (SQLException e) {
            
            showAlert("SQL Error", e.getMessage());
        }
    }

    @FXML
    public void medicineSelect() {
        MedicineData medicineD = Medicine_TableView.getSelectionModel().getSelectedItem();
        if (medicineD == null) {
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
        if (Medicine_MedicineID.getText().isEmpty() || Medicine_MedicineName.getText().isEmpty()
                || Medicine_MedicinePrice.getText().isEmpty() || Medicine_MedicineCompany.getText().isEmpty()
                || Medicine_MedicineType.getText().isEmpty() || Medicine_MedicineStatus.getSelectionModel().isEmpty()) {
            showAlert("Error", "Please select and fill all fields to update!");
            return;
        }

        if (!isValidID(Medicine_MedicineID.getText())) {
            showAlert("Validation Error", "Medicine ID must be a positive integer.");
            return;
        }
        if (!isValidNameOrCompany(Medicine_MedicineName.getText())) {
            showAlert("Validation Error", "Medicine Name contains invalid characters. Use letters and numbers only.");
            return;
        }
        if (!isValidNameOrCompany(Medicine_MedicineCompany.getText())) {
            showAlert("Validation Error", "Company name contains invalid characters. Use letters and numbers only.");
            return;
        }
        if (!isValidNameOrCompany(Medicine_MedicineType.getText())) {
            showAlert("Validation Error", "Type contains invalid characters. Use letters and numbers only.");
            return;
        }
        if (!isValidPrice(Medicine_MedicinePrice.getText())) {
            showAlert("Validation Error", "Price must be a valid positive number.");
            return;
        }

        String updateData = "UPDATE medicine SET name = ?, price = ?, company = ?, type = ?, status = ? WHERE medicine_id = ?";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(updateData)) {
            prepare.setString(1, Medicine_MedicineName.getText());
            prepare.setString(2, Medicine_MedicinePrice.getText());
            prepare.setString(3, Medicine_MedicineCompany.getText());
            prepare.setString(4, Medicine_MedicineType.getText());
            prepare.setString(5, Medicine_MedicineStatus.getSelectionModel().getSelectedItem());
            prepare.setString(6, Medicine_MedicineID.getText());
            int result = prepare.executeUpdate();
            if (result > 0) {
                showAlert("Information", "Successfully Updated!");
                medicineShowData();
                medicineClearFields();
                loadDashboardData();
            } else {
                showAlert("Error", "Update failed! Ensure the ID exists.");
            }
        } catch (SQLException e) {
         
            showAlert("SQL Error", e.getMessage());
        }
    }

    @FXML
    public void medicineDelete() {
        if (Medicine_MedicineID.getText().isEmpty()) {
            showAlert("Error", "Please select the medicine to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete Medicine ID: " + Medicine_MedicineID.getText() + "?");
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        String deleteData = "DELETE FROM medicine WHERE medicine_id = ?";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(deleteData)) {
            prepare.setString(1, Medicine_MedicineID.getText());
            int result = prepare.executeUpdate();
            if (result > 0) {
                showAlert("Information", "Successfully Deleted!");
                medicineShowData();
                medicineClearFields();
                loadDashboardData();
            }
        } catch (SQLException e) {
           
            showAlert("SQL Error", e.getMessage());
        }
    }

    // ================= PURCHASE MEDICINE METHODS =================
    private void setupPurchaseTable() {
        purchase_co_brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        purchase_co_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        purchase_co_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        purchase_co_qty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        purchase_co_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        purchase_tableView.setItems(cart);
    }

    private void loadTypes() {
        String sql = "SELECT DISTINCT type FROM medicine WHERE status='available'";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(sql); ResultSet result = prepare.executeQuery()) {

            ObservableList<String> list = FXCollections.observableArrayList();
            while (result.next()) {
                list.add(result.getString("type"));
            }
            purchase_type.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadProductNames() {
        if (purchase_brand.getValue() == null || purchase_type.getValue() == null) {
            return;
        }
        String sql = "SELECT name, price FROM medicine WHERE type=? AND company=?";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(sql)) {

            prepare.setString(1, purchase_type.getValue());
            prepare.setString(2, purchase_brand.getValue());
            ResultSet result = prepare.executeQuery();
            ObservableList<String> list = FXCollections.observableArrayList();
            while (result.next()) {
                list.add(result.getString("name"));
            }
            purchase_productName.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadBrands() {
        if (purchase_type.getValue() == null) {
            return;
        }
        String sql = "SELECT DISTINCT company FROM medicine WHERE type=?";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(sql)) {

            prepare.setString(1, purchase_type.getValue());
            ResultSet result = prepare.executeQuery();
            ObservableList<String> list = FXCollections.observableArrayList();
            while (result.next()) {
                list.add(result.getString("company"));
            }
            purchase_brand.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addToCart() {
        if (purchase_type.getValue() == null || purchase_brand.getValue() == null
                || purchase_productName.getValue() == null) {
            showAlert("Error", "Fill all fields");
            return;
        }

        double price = getPrice(purchase_type.getValue(), purchase_brand.getValue(), purchase_productName.getValue());
        int qty = purchase_qty_spinner.getValue();

        customerData item = new customerData(0, purchase_type.getValue(), 0,
                purchase_brand.getValue(), purchase_productName.getValue(), qty, price * qty, new Date());

        cart.add(item);
        calculateTotal();
    }

    private double getPrice(String type, String brand, String name) {
        String sql = "SELECT price FROM medicine WHERE type=? AND company=? AND name=?";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(sql)) {

            prepare.setString(1, type);
            prepare.setString(2, brand);
            prepare.setString(3, name);
            ResultSet result = prepare.executeQuery();
            if (result.next()) {
                return result.getDouble("price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void calculateTotal() {
        totalPrice = cart.stream().mapToDouble(customerData::getPrice).sum();
        purchase_total.setText("$" + totalPrice);
    }

    @FXML
    private void calculateBalance() {
        if (purchase_amount.getText().isEmpty()) {
            return;
        }
        double amount = Double.parseDouble(purchase_amount.getText());
        double balance = amount - totalPrice;
        purchase_balance.setText("$" + balance);
    }

    @FXML
    private void pay() {
        if (cart.isEmpty()) {
            showAlert("Error", "Cart is empty");
            return;
        }

        String insertBill = "INSERT INTO Bill (pharmacist_id, total_price, date) VALUES (?,?,?)";
        String insertBillMedicine = "INSERT INTO Bill_Medicine (bill_id, medicine_id, quantity) VALUES (?,?,?)";

        try (Connection connect = dataBase.getConnection()) {
            // Insert Bill
            PreparedStatement billStmt = connect.prepareStatement(insertBill, Statement.RETURN_GENERATED_KEYS);
            int pharmacistId = getData.adminId;
            billStmt.setInt(1, pharmacistId);
            
            billStmt.setDouble(2, totalPrice);
            billStmt.setDate(3, new java.sql.Date(new Date().getTime()));
            billStmt.executeUpdate();

            ResultSet rs = billStmt.getGeneratedKeys();
            int billId = 0;
            if (rs.next()) {
                billId = rs.getInt(1);
            }

            for (customerData item : cart) {
                int medicineId = getMedicineId(item.getType(), item.getBrand(), item.getProductName());
                if (medicineId == 0) {
                    continue;
                }

                PreparedStatement bmStmt = connect.prepareStatement(insertBillMedicine);
                bmStmt.setInt(1, billId);
                bmStmt.setInt(2, medicineId);
                bmStmt.setInt(3, item.getQuantity());
                bmStmt.executeUpdate();
            }

            // Reset UI
            cart.clear();
            calculateTotal();
            purchase_amount.clear();
            purchase_balance.setText("$0.0");
            showAlert("Success", "Payment Done");
            loadDashboardData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getMedicineId(String type, String brand, String name) {
        String sql = "SELECT medicine_id FROM medicine WHERE type=? AND company=? AND name=?";
        try (Connection connect = dataBase.getConnection(); PreparedStatement prepare = connect.prepareStatement(sql)) {

            prepare.setString(1, type);
            prepare.setString(2, brand);
            prepare.setString(3, name);
            ResultSet result = prepare.executeQuery();
            if (result.next()) {
                return result.getInt("medicine_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ============Settings methods=============
    private void applyTheme(String theme) {
        Scene scene = dashboard_form.getScene();
        if (scene == null) {
            return;
        }

        if ("Dark".equals(theme)) {
            if (!scene.getRoot().getStyleClass().contains("dark")) {
                scene.getRoot().getStyleClass().add("dark");
            }
        } else {
            scene.getRoot().getStyleClass().remove("dark");
        }
    }

    //===================== Dashboard FXML ==========
    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private AreaChart<String, Number> dashboard_chart;

    @FXML
    private Label dashboard_availableMed;

    @FXML
    private Label dashboard_totalIncome;

    @FXML
    private Label dashboard_totalCustomers;

//    @FXML
//    private Button dashboard_btn;
    // ====================== Dashboard Methods================
    private double getSingleValue(String sql) { // count/sum
        double value = 0;

        try (Connection conn = dataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                value = rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public void loadAvailableMedicines() {
        String sql = "SELECT COUNT(medicine_id) FROM  Medicine WHERE status='available'";
        int count = (int) getSingleValue(sql);
        dashboard_availableMed.setText(String.valueOf(count));
    }

    public void loadTotalIncome() {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM Bill";
        double total = getSingleValue(sql);
        dashboard_totalIncome.setText("$" + total);
    }

    public void loadTotalCustomers() {
        String sql = "SELECT COUNT(bill_id) FROM Bill";
        int count = (int) getSingleValue(sql);
        dashboard_totalCustomers.setText(String.valueOf(count));
    }

    public void loadDashboardChart() {

        dashboard_chart.getData().clear();

        String sql = """
        SELECT date, SUM(total_price)
        FROM Bill
        GROUP BY date
        ORDER BY date ASC
        LIMIT 9
        """;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Income");

        try (Connection conn = dataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                series.getData().add(
                        new XYChart.Data<>(
                                rs.getDate(1).toString(),
                                rs.getDouble(2)
                        )
                );
            }

            dashboard_chart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDashboardData() {
        loadAvailableMedicines();
        loadTotalIncome();
        loadTotalCustomers();
        loadDashboardChart();
    }

    // ==================== navigate pages=========
    @FXML
    private void showDashboard() {
        clearActiveNavButtons();
        dashboard_btn.getStyleClass().add("active-nav-btn");

        dashboard_form.setVisible(true);
        purchase_form.setVisible(false);
        medicineView_form.setVisible(false);
        settings_form.setVisible(false);
        about_form.setVisible(false);
    }

    @FXML
    private void showViewMedicine() {
        clearActiveNavButtons();
        view_btn.getStyleClass().add("active-nav-btn");

        medicineView_form.setVisible(true);
        purchase_form.setVisible(false);
        dashboard_form.setVisible(false);
        settings_form.setVisible(false);
        about_form.setVisible(false);
    }

    @FXML
    private void showPurchaseMedicine() {
        clearActiveNavButtons();
        purchase_btn.getStyleClass().add("active-nav-btn");

        purchase_form.setVisible(true);
        dashboard_form.setVisible(false);
        medicineView_form.setVisible(false);
        settings_form.setVisible(false);
        about_form.setVisible(false);
    }

    @FXML
    private void showSettings() {
        clearActiveNavButtons();
        setting_btn.getStyleClass().add("active-nav-btn");

        dashboard_form.setVisible(false);
        purchase_form.setVisible(false);
        medicineView_form.setVisible(false);
        settings_form.setVisible(true);
        about_form.setVisible(false);
    }

    @FXML
    public void showAboutUs() {
        clearActiveNavButtons();
        aboutus_btn.getStyleClass().add("active-nav-btn");
        dashboard_form.setVisible(false);
        purchase_form.setVisible(false);
        medicineView_form.setVisible(false);      
        settings_form.setVisible(false);       
        about_form.setVisible(true);
    }
    
    @FXML
    public void backTOLogin() throws IOException{
        
        
        // HIDE YOUR DASHBOARD 
        signout_btn.getScene().getWindow().hide();
        // LINK YOUR DASHBOARD FORM
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
               
                  
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
    }
}
