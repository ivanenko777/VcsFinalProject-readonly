package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatus;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.InvalidStatusException;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.components.service.CustomerService;
import lt.ivl.components.service.RepairService;
import lt.ivl.webInternalApp.dto.RepairDto;
import lt.ivl.webInternalApp.dto.RepairStatusNoteDto;
import lt.ivl.webInternalApp.dto.RepairStatusStoredDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class InternalRepairService {
    @Autowired
    private RepairService componentRepairService;

    @Autowired
    private CustomerService componentCustomerService;

    public List<Repair> findAll() {
        return componentRepairService.findAll();
    }

    public Repair findRepairToDelete(int id) throws ItemNotFoundException {
        return componentRepairService.findRepairToDelete(id);
    }

    public void deleteRepair(int id) throws ItemNotFoundException {
        componentRepairService.deleteRepair(id);
    }

    @Transactional
    public Repair updateRepair(Repair repair, RepairDto repairDto, Employee employee) throws CustomerNotFoundInDBException, InvalidStatusException, ItemNotFoundException {
        int customerId = repairDto.getCustomer();
        Customer customer = componentCustomerService.findById(customerId);
        repair.setCustomer(customer);
        repair.setDeviceType(repairDto.getDeviceType());
        repair.setDeviceManufacturer(repairDto.getDeviceManufacturer());
        repair.setDeviceModel(repairDto.getDeviceModel());
        repair.setDeviceSerialNo(repairDto.getDeviceSerialNo());
        repair.setDeviceWarranty(repairDto.isDeviceWarranty());
        repair.setDescription(repairDto.getDescription());
        repair = componentRepairService.saveRepair(repair);

        boolean confirmRepair = repairDto.isConfirmRepair();
        if (confirmRepair) {
            RepairStatus currentStatus = repair.getStatus();
            RepairStatus newStatus = RepairStatus.CONFIRMED;
            repair = changeRepairStatus(repair, currentStatus, newStatus, employee, null, null);
        }
        return repair;
    }

    @Transactional
    public Repair createRepair(RepairDto repairDto, Employee employee) throws InvalidStatusException, CustomerNotFoundInDBException {
        RepairStatus currentStatus = null;
        RepairStatus newStatus = null;

        int customerId = repairDto.getCustomer();
        Customer customer = componentCustomerService.findById(customerId);
        String deviceType = repairDto.getDeviceType();
        String deviceManufacturer = repairDto.getDeviceManufacturer();
        String deviceModel = repairDto.getDeviceModel();
        String deviceSerialNo = repairDto.getDeviceSerialNo();
        boolean deviceWarranty = repairDto.isDeviceWarranty();
        String description = repairDto.getDescription();
        Repair repair = new Repair(customer, deviceType, deviceManufacturer, deviceModel, deviceSerialNo, deviceWarranty, description);

        newStatus = RepairStatus.PENDING;
        componentRepairService.verifyNewStatus(currentStatus, newStatus);
        repair = componentRepairService.saveRepair(repair);
        repair = componentRepairService.changeRepairStatus(repair, newStatus, employee, null, null);

        boolean confirmRepair = repairDto.isConfirmRepair();
        if (confirmRepair) {
            currentStatus = repair.getStatus();
            newStatus = RepairStatus.CONFIRMED;
            repair = changeRepairStatus(repair, currentStatus, newStatus, employee, null, null);
        }

        return repair;
    }

    public Repair storeDevice(
            Repair repair,
            RepairStatusStoredDto repairStatusStoredDto,
            RepairStatusNoteDto repairStatusNoteDto,
            Employee employee
    ) throws InvalidStatusException {
        String stored = repairStatusStoredDto.getStored();
        String note = repairStatusNoteDto.getNote();
        RepairStatus currentStatus = repair.getStatus();
        RepairStatus newStatus = RepairStatus.DIAGNOSTIC_WAITING;
        repair = changeRepairStatus(repair, currentStatus, newStatus, employee, note, stored);
        return repair;
    }

    public Repair startDiagnostic(Repair repair, Employee employee) throws InvalidStatusException {
        RepairStatus currentStatus = repair.getStatus();
        RepairStatus newStatus = RepairStatus.DIAGNOSTIC;
        repair = changeRepairStatus(repair, currentStatus, newStatus, employee, null, null);
        return repair;
    }

    public Repair finishDiagnostic(
            Repair repair,
            RepairStatusStoredDto repairStatusStoredDto,
            RepairStatusNoteDto repairStatusNoteDto,
            Employee employee
    ) throws InvalidStatusException {
        String stored = repairStatusStoredDto.getStored();
        String note = repairStatusNoteDto.getNote();
        boolean deviceWarranty = repair.isDeviceWarranty();
        RepairStatus currentStatus = repair.getStatus();
        RepairStatus newStatus = deviceWarranty ? RepairStatus.REPAIR_WAITING : RepairStatus.PAYMENT_CONFIRM_WAITING;

        repair = changeRepairStatus(repair, currentStatus, newStatus, employee, note, stored);
        return repair;
    }

    private Repair changeRepairStatus(
            Repair repair,
            RepairStatus currentStatus,
            RepairStatus newStatus,
            Employee employee,
            String note,
            String stored
    ) throws InvalidStatusException {
        componentRepairService.verifyNewStatus(currentStatus, newStatus);
        repair.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
        repair = componentRepairService.changeRepairStatus(repair, newStatus, employee, note, stored);
        return repair;
    }
}
