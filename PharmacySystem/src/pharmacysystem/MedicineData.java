/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pharmacysystem;

/**
 *
 * @author MariamReda
 */
public class MedicineData {
    private final String medicineID;
    private final String medicineName;
    private final String medicinePrice;
    private final String company;
    private final String type;
    private final String status;

    public MedicineData(String medicineID, String medicineName, String medicinePrice, 
                        String company, String type, String status) {
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.medicinePrice = medicinePrice;
        this.company = company;
        this.type = type;
        this.status = status;
    }

    
    public String getMedicineID() {
        return medicineID;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getMedicinePrice() {
        return medicinePrice;
    }

    public String getCompany() {
        return company;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
    
}
