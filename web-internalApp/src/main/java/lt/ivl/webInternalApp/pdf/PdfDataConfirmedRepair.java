package lt.ivl.webInternalApp.pdf;

import lt.ivl.components.domain.Repair;

import java.text.SimpleDateFormat;

public class PdfDataConfirmedRepair {
    private String repairId;

    private String confirmedAt;
    private String confirmedByFullName;
    private String confirmedByEmail;
    private String confirmedByPhone;

    private String deviceType;
    private String deviceManufacturer;
    private String deviceModel;
    private String deviceSerialNo;
    private String description;

    private String customerFullName;
    private String customerEmail;
    private String customerPhone;

    public PdfDataConfirmedRepair() {
    }

    public PdfDataConfirmedRepair(Repair repair) {
        this.repairId = String.valueOf(repair.getId());
        this.confirmedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(repair.getConfirmedAt());
        this.confirmedByFullName = repair.getConfirmedBy().getFullName();
        this.confirmedByEmail = repair.getConfirmedBy().getEmail();
        this.confirmedByPhone = repair.getConfirmedBy().getPhone();
        this.deviceType = repair.getDeviceType();
        this.deviceManufacturer = repair.getDeviceManufacturer();
        this.deviceModel = repair.getDeviceModel();
        this.deviceSerialNo = repair.getDeviceSerialNo();
        this.description = repair.getDescription();
        this.customerFullName = repair.getCustomer().getFullName();
        this.customerEmail = repair.getCustomer().getEmail();
        this.customerPhone = repair.getCustomer().getPhone();
    }

    public String getRepairId() {
        return repairId;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public String getConfirmedByFullName() {
        return confirmedByFullName;
    }

    public String getConfirmedByEmail() {
        return confirmedByEmail;
    }

    public String getConfirmedByPhone() {
        return confirmedByPhone;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }

    public String getDescription() {
        return description;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }
}
