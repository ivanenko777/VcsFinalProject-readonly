package lt.ivl.components.service;

import lt.ivl.components.domain.*;
import lt.ivl.components.exception.InvalidStatusException;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.components.repository.RepairRepository;
import lt.ivl.components.repository.RepairStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RepairService {
    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private RepairStatusHistoryRepository repairStatusHistoryRepository;

    public List<Repair> findAll() {
        return (List<Repair>) repairRepository.findAll();
    }

    public Repair saveRepair(Repair repair) {
        return repairRepository.save(repair);
    }

    @Transactional
    public Repair changeRepairStatus(Repair repair, RepairStatus status, Employee employee, String note, String stored) {
        RepairStatusHistory statusHistory = new RepairStatusHistory();
        statusHistory.setRepair(repair);
        statusHistory.setStatus(status);
        statusHistory.setEmployee(employee);
        statusHistory.setNote(note);
        statusHistory.setStored(stored);

        repair.setStatus(status);
        statusHistory.getRepair().setStatus(status);

        repair.setConfirmedBy(employee);
        statusHistory.getRepair().setConfirmedBy(employee);

        repair.setStored(stored);
        statusHistory.getRepair().setStored(stored);
        repairStatusHistoryRepository.save(statusHistory);

        return repair;
    }

    public List<Repair> findAllCustomerRepairs(Customer customer) {
        return repairRepository.findAllByCustomerOrderByCreatedAtDesc(customer);
    }

    public Repair findCustomerRepair(Customer customer, int id) throws ItemNotFoundException {
        Optional<Repair> repairFromDb = repairRepository.findByIdAndCustomer(id, customer);
        if (repairFromDb.isEmpty()) {
            throw new ItemNotFoundException();
        }

        return repairFromDb.get();
    }

    public Repair findCustomerRepairToDelete(Customer customer, int id) throws ItemNotFoundException {
        // panasus i findCustomerRepair(), bet yra papildoma salyga RepairStatus == PENDING
        Optional<Repair> repairFromDb = repairRepository.findByIdAndCustomer(id, customer);
        if (repairFromDb.isEmpty() || repairFromDb.get().getStatus() != RepairStatus.PENDING) {
            throw new ItemNotFoundException();
        }

        return repairFromDb.get();
    }

    @Transactional
    public void deleteCustomerRepair(Customer customer, int id) throws ItemNotFoundException {
        Repair repair = findCustomerRepairToDelete(customer, id);
        repairStatusHistoryRepository.deleteByRepairId(repair.getId());
        repairRepository.delete(repair);
    }


    public Repair findRepairToDelete(int id) throws ItemNotFoundException {
        Optional<Repair> repairFromDb = repairRepository.findById(id);
        if (repairFromDb.isEmpty() || repairFromDb.get().getStatus() != RepairStatus.PENDING) {
            throw new ItemNotFoundException();
        }
        return repairFromDb.get();
    }

    @Transactional
    public void deleteRepair(int id) throws ItemNotFoundException {
        Repair repair = findRepairToDelete(id);
        repairStatusHistoryRepository.deleteByRepairId(id);
        repairRepository.delete(repair);
    }

    public void verifyNewStatus(RepairStatus currentStatus, RepairStatus newStatus) throws InvalidStatusException {
        Set<RepairStatus> allowedStatuses = new HashSet<>();
        switch (newStatus) {
            case PENDING:
                break;
            case CONFIRMED:
                allowedStatuses.addAll(Collections.singletonList(RepairStatus.PENDING));
                break;
        }

        if (!allowedStatuses.contains(currentStatus)) {
            throw new InvalidStatusException();
        }
    }
}
