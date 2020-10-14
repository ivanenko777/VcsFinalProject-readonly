package lt.ivl.webInternalApp.dto;

import lt.ivl.components.domain.Repair;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RepairDto {
    @NotNull(message = "Laukas negali būti tuščias.")
    @Min(value = 1, message = "Laukas negali būti tuščias.")
    private int customer;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String deviceType;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String deviceManufacturer;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String deviceModel;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String deviceSerialNo;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String description;

    @NotNull
    private boolean deviceWarranty;

    private boolean confirmRepair;

    public RepairDto() {
    }

    public RepairDto(Repair repair) {
        this.customer = repair.getCustomer().getId();
        this.deviceType = repair.getDeviceType();
        this.deviceManufacturer = repair.getDeviceManufacturer();
        this.deviceModel = repair.getDeviceModel();
        this.deviceSerialNo = repair.getDeviceSerialNo();
        this.description = repair.getDescription();
        this.deviceWarranty = repair.isDeviceWarranty();
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }

    public void setDeviceSerialNo(String deviceSerialNo) {
        this.deviceSerialNo = deviceSerialNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isConfirmRepair() {
        return confirmRepair;
    }

    public void setConfirmRepair(boolean confirmRepair) {
        this.confirmRepair = confirmRepair;
    }

    public boolean isDeviceWarranty() {
        return deviceWarranty;
    }

    public void setDeviceWarranty(boolean deviceWarranty) {
        this.deviceWarranty = deviceWarranty;
    }
}
