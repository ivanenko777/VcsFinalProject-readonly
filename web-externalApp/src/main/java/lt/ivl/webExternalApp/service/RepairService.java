package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.Repair;
import lt.ivl.webExternalApp.domain.RepairStatus;
import lt.ivl.webExternalApp.dto.RepairDto;
import lt.ivl.webExternalApp.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class RepairService {
    @Autowired
    private RepairRepository repairRepository;

    public void createNewRepairItemFromCustomer(Customer customer, RepairDto repairDto) {
        Repair repair = new Repair();
        repair.setCreatedByCustomer(customer);
        repair.setStatus(RepairStatus.PENDING);

        repair.setDeviceType(repairDto.getDeviceType());
        repair.setDeviceManufacturer(repairDto.getDeviceManufacturer());
        repair.setDeviceModel(repairDto.getDeviceModel());
        repair.setDeviceSerialNo(repairDto.getDeviceSerialNo());
        repair.setDescription(repairDto.getDescription());
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        repair.setCreatedAt(timeNow);
        repair.setUpdatedAt(timeNow);

        repairRepository.save(repair);
    }

    public Iterable<Repair> findCustomerRepairsWithSpecificStatus(Customer customer, Iterable<RepairStatus> statuses) {
        return repairRepository.findAllByCreatedByCustomerAndStatusInOrderByCreatedAtDesc(customer, statuses);
    }
}
