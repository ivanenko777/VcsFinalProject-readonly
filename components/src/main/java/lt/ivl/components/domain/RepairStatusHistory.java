package lt.ivl.components.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class RepairStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id", nullable = false)
    private Repair repair;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String notes;

    @Column(nullable = true)
    private String store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;
    private Timestamp createdAt;

    public RepairStatusHistory() {
        this.createdAt = timeNow();
    }

    public int getId() {
        return id;
    }

    public Repair getRepair() {
        return repair;
    }

    public void setRepair(Repair repair) {
        this.repair = repair;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createAt) {
        this.createdAt = createAt;
    }

    private Timestamp timeNow() {
        return new Timestamp(System.currentTimeMillis());
    }
}
