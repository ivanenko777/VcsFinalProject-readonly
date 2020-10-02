package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.Repair;
import lt.ivl.webExternalApp.domain.RepairStatus;
import lt.ivl.webExternalApp.dto.RepairDto;
import lt.ivl.webExternalApp.exception.ItemNotFoundException;
import lt.ivl.webExternalApp.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class RepairService {
    @Autowired
    private RepairRepository repairRepository;

    public Repair createNewRepairItemByCustomer(Customer customer, RepairDto repairDto) {
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

        return repairRepository.save(repair);
    }

    public Iterable<Repair> findWithStatusesByCustomer(Customer customer, Iterable<RepairStatus> statuses) {
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
