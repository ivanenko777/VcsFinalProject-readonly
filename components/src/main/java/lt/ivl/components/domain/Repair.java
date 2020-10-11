package lt.ivl.components.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(indexes = {@Index(name = "repair__customer_id__index", columnList = "customer_id")})
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    private String deviceType;
    private String deviceManufacturer;
    private String deviceModel;
    private String deviceSerialNo;
    private String description;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Repair() {
        this.updatedAt = timeNow();
    }

    public Repair(
            Customer customer,
            String deviceType,
            String deviceManufacturer,
            String deviceModel,
            String deviceSerialNo,
            String description
    ) {
        this.customer = customer;
        this.status = RepairStatus.PENDING;
        this.deviceType = deviceType;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceModel = deviceModel;
        this.deviceSerialNo = deviceSerialNo;
        this.description = description;

        this.createdAt = timeNow();
        this.updatedAt = timeNow();
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    private Timestamp timeNow() {
        return new Timestamp(System.currentTimeMillis());
    }
}
