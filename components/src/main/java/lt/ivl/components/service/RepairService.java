package lt.ivl.components.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatus;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.components.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepairService {
    @Autowired
    private RepairRepository repairRepository;

    public Repair saveRepair(Repair repair) {
        return repairRepository.save(repair);
    }

    public List<Repair> findAllCustomerRepairs(Customer customer) {
        return repairRepository.findAllByCreatedByCustomerOrderByCreatedAtDesc(customer);
    }

    public Repair findCustomerRepair(Customer customer, int id) throws ItemNotFoundException {
        Optional<Repair> repairFromDb = repairRepository.findByIdAndCreatedByCustomer(id, customer);
        if (repairFromDb.isEmpty()) {
            throw new ItemNotFoundException();
        }

        return repairFromDb.get();
    }

    public Repair findCustomerRepairToDelete(Customer customer, int id) throws ItemNotFoundException {
        // panasus i findCustomerRepair(), bet yra papildoma salyga RepairStatus == PENDING
        Optional<Repair> repairFromDb = repairRepository.findByIdAndCreatedByCustomer(id, customer);
        if (repairFromDb.isEmpty() || repairFromDb.get().getStatus() != RepairStatus.PENDING) {
            throw new ItemNotFoundException();
        }

        return repairFromDb.get();
    }

    public void deleteCustomerRepair(Customer customer, int id) throws ItemNotFoundException {
        Repair repair = findCustomerRepairToDelete(customer, id);
        repairRepository.delete(repair);
    }
}
