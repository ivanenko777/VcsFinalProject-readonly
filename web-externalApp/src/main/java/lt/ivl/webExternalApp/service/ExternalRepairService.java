package lt.ivl.webExternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatus;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.components.repository.RepairRepository;
import lt.ivl.components.service.RepairService;
import lt.ivl.webExternalApp.dto.RepairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExternalRepairService {
    @Autowired
    private RepairService componentRepairService;

    @Autowired
    private RepairRepository repairRepository;

    public Repair createNewRepairItemByCustomer(Customer customer, RepairDto repairDto) {
        String deviceType = repairDto.getDeviceType();
        String deviceManufacturer = repairDto.getDeviceManufacturer();
        String deviceModel = repairDto.getDeviceModel();
        String deviceSerialNo = repairDto.getDeviceSerialNo();
        String description = repairDto.getDescription();

        Repair repair = new Repair(customer, deviceType, deviceManufacturer, deviceModel, deviceSerialNo, description);
        return componentRepairService.saveRepair(repair);
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

    public void deleteByCustomer(Customer customer, int id) throws ItemNotFoundException {
        Repair repair = findCustomerRepairToDelete(customer, id).get();
        repairRepository.delete(repair);
    }
}
