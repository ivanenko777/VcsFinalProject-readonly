package lt.ivl.webExternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatus;
import lt.ivl.components.service.RepairService;
import lt.ivl.webExternalApp.dto.RepairDto;
import lt.ivl.webExternalApp.exception.ItemNotFoundException;
import lt.ivl.components.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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

    public List<Repair> findWithStatusesByCustomer(Customer customer, Iterable<RepairStatus> statuses) {
        return repairRepository.findAllByCreatedByCustomerAndStatusInOrderByCreatedAtDesc(customer, statuses);
    }

    public Repair findByCustomer(Customer customer, int id) throws ItemNotFoundException {
        Optional<Repair> repairFromDb = repairRepository.findByIdAndCreatedByCustomer(id, customer);
        if (repairFromDb.isEmpty()) {
            throw new ItemNotFoundException();
        }

        return repairFromDb.get();
    }

    public Optional<Repair> findToDeleteByCustomer(Customer customer, int id) throws ItemNotFoundException {
        Optional<Repair> repairFromDb = repairRepository.findByIdAndCreatedByCustomer(id, customer);

        if (repairFromDb.isEmpty() || repairFromDb.get().getStatus() != RepairStatus.PENDING) {
            throw new ItemNotFoundException();
        }

        return repairFromDb;
    }

    public void deleteByCustomer(Customer customer, int id) throws ItemNotFoundException {
        Repair repair = findToDeleteByCustomer(customer, id).get();
        repairRepository.delete(repair);
    }
}
