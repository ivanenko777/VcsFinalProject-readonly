package lt.ivl.webExternalApp.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RepairDto {
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

    public boolean isDeviceWarranty() {
        return deviceWarranty;
    }

    public void setDeviceWarranty(boolean deviceWarranty) {
        this.deviceWarranty = deviceWarranty;
    }
}
