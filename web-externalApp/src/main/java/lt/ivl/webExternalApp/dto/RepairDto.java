package lt.ivl.webExternalApp.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RepairDto {
    @NotNull
    @NotEmpty
    private String deviceType;

    @NotNull
    @NotEmpty
    private String deviceManufacturer;

    @NotNull
    @NotEmpty
    private String deviceModel;

    @NotNull
    @NotEmpty
    private String deviceSerialNo;

    @NotNull
    @NotEmpty
    private String description;

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
}
