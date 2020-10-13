package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatus;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.components.service.CustomerService;
import lt.ivl.components.service.RepairService;
import lt.ivl.webInternalApp.dto.RepairDto;
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
    public Repair confirmRepair(Repair repair, RepairDto repairDto, Employee employee) throws CustomerNotFoundInDBException {
        int customerId = repairDto.getCustomer();
        Customer customer = componentCustomerService.findById(customerId);
        repair.setCustomer(customer);
        repair.setDeviceType(repairDto.getDeviceType());
        repair.setDeviceManufacturer(repairDto.getDeviceManufacturer());
        repair.setDeviceModel(repairDto.getDeviceModel());
        repair.setDeviceSerialNo(repairDto.getDeviceSerialNo());
        repair.setDescription(repairDto.getDescription());

        RepairStatus status = RepairStatus.CONFIRMED;
        repair.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
        repair = componentRepairService.saveRepair(repair);
        repair = componentRepairService.changeRepairStatus(repair, status, employee, null, null);

        return repair;
    }
}
