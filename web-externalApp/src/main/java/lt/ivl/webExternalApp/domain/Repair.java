package lt.ivl.webExternalApp.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(indexes = {@Index(name = "repair__created_by_customer_id__index", columnList = "created_by_customer_id")})
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @ManyToOne
    @JoinColumn(name = "created_by_customer_id", nullable = false)
    private Customer created_by_customer;

    private Timestamp created_at;
    private Timestamp updated_at;

    private String deviceType;
    private String deviceManufacturer;
    private String deviceModel;
    private String deviceSerialNo;
    private String description;

    public int getId() {
        return id;
    }

    public Customer getCreated_by_customer() {
        return created_by_customer;
    }

    public void setCreated_by_customer(Customer created_by_customer) {
        this.created_by_customer = created_by_customer;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
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
}
