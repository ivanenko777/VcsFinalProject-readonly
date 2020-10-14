package lt.ivl.webExternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatus;
import lt.ivl.components.exception.InvalidStatusException;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.components.service.RepairService;
import lt.ivl.webExternalApp.dto.RepairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ExternalRepairService {
    @Autowired
    private RepairService componentRepairService;

    @Transactional
    public Repair createNewRepairItemByCustomer(Customer customer, RepairDto repairDto) throws InvalidStatusException {
        RepairStatus newStatus = RepairStatus.PENDING;
        componentRepairService.verifyNewStatus(null, newStatus);

        String deviceType = repairDto.getDeviceType();
        String deviceManufacturer = repairDto.getDeviceManufacturer();
        String deviceModel = repairDto.getDeviceModel();
        String deviceSerialNo = repairDto.getDeviceSerialNo();
        boolean deviceWarranty = repairDto.isDeviceWarranty();
        String description = repairDto.getDescription();

        Repair repair = new Repair(customer, deviceType, deviceManufacturer, deviceModel, deviceSerialNo, deviceWarranty, description);
        repair = componentRepairService.saveRepair(repair);
        repair = componentRepairService.changeRepairStatus(repair, newStatus, null, null, null);
        return repair;
    }

    public List<Repair> findAllCustomerRepairs(Customer customer) {
        return componentRepairService.findAllCustomerRepairs(customer);
    }

    public Repair findCustomerRepair(Customer customer, int id) throws ItemNotFoundException {
        return componentRepairService.findCustomerRepair(customer, id);
    }

    public Repair findCustomerRepairToDelete(Customer customer, int id) throws ItemNotFoundException {
        return componentRepairService.findCustomerRepairToDelete(customer, id);
    }

    public void deleteCustomerRepair(Customer customer, int id) throws ItemNotFoundException {
        componentRepairService.deleteCustomerRepair(customer, id);
    }

    @Transactional
    public Repair confirmPaymentByCustomer(Repair repair) throws InvalidStatusException {
        RepairStatus currentStatus = repair.getStatus();
        RepairStatus newStatus = RepairStatus.PAYMENT_CONFIRMED;
        repair = changeRepairStatus(repair, currentStatus, newStatus);

        currentStatus = repair.getStatus();
        newStatus = RepairStatus.REPAIR_WAITING;
        repair = changeRepairStatus(repair, currentStatus, newStatus);
        return repair;
    }

    @Transactional
    public Repair cancelPaymentByCustomer(Repair repair) throws InvalidStatusException {
        RepairStatus currentStatus = repair.getStatus();
        RepairStatus newStatus = RepairStatus.PAYMENT_CANCELED;
        repair = changeRepairStatus(repair, currentStatus, newStatus);

        currentStatus = repair.getStatus();
        newStatus = RepairStatus.RETURN;
        repair = changeRepairStatus(repair, currentStatus, newStatus);
        return repair;
    }

    private Repair changeRepairStatus(
            Repair repair,
            RepairStatus currentStatus,
            RepairStatus newStatus
    ) throws InvalidStatusException {
        componentRepairService.verifyNewStatus(currentStatus, newStatus);
        repair.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
        repair = componentRepairService.changeRepairStatus(repair, newStatus, null, null, null);
        return repair;
    }
}
