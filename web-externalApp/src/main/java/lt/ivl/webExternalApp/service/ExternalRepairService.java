package lt.ivl.webExternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatus;
import lt.ivl.components.domain.RepairStatusHistory;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.components.service.RepairService;
import lt.ivl.webExternalApp.dto.RepairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ExternalRepairService {
    @Autowired
    private RepairService componentRepairService;

    public Repair createNewRepairItemByCustomer(Customer customer, RepairDto repairDto) {
        String deviceType = repairDto.getDeviceType();
        String deviceManufacturer = repairDto.getDeviceManufacturer();
        String deviceModel = repairDto.getDeviceModel();
        String deviceSerialNo = repairDto.getDeviceSerialNo();
        String description = repairDto.getDescription();

        RepairStatus status = RepairStatus.PENDING;
        Repair repair = new Repair(customer, deviceType, deviceManufacturer, deviceModel, deviceSerialNo, description);

        return componentRepairService.saveAndChangeRepairStatus(repair, status, null, null);
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
}
