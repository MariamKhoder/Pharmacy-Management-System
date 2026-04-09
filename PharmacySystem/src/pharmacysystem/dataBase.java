/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pharmacysystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Mlk
 */
public class dataBase {
    
    private static final String url = "jdbc:mysql://localhost:3306/pharmacy_system"; 
    private static final String username = "mariam";
    private static final String password = "1234";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to MySQL database!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
            return null;
        }
    }
             public static void main(String[] args) {
           getConnection();
          } 
}
     
    
    



