package lt.ivl.components.service;

import lt.ivl.components.domain.*;
import lt.ivl.components.exception.InvalidStatusException;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.components.repository.RepairRepository;
import lt.ivl.components.repository.RepairStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RepairService {
    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private RepairStatusHistoryRepository repairStatusHistoryRepository;

    public List<Repair> findAll() {
        return (List<Repair>) repairRepository.findAllByOrderByIdDesc();
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
        statusHistory.getRepair().setStatus(status);
        statusHistory.getRepair().setStored(stored);
        repair.setUpdatedAt(statusHistory.getCreatedAt());
        repairStatusHistoryRepository.save(statusHistory);

        repair.setStatus(status);
        repair.setStored(stored);
        repairRepository.save(repair);

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
        Set<RepairStatus> allowedStatuses = allowedStatuses(newStatus);

        if (currentStatus != null && !allowedStatuses.contains(currentStatus)) {
            throw new InvalidStatusException();
        }
    }

    public boolean checkButtonVisibility(String currentStatus, String newStatus) {
        RepairStatus currentStatusEnum = RepairStatus.valueOf(currentStatus);
        RepairStatus newStatusEnum = RepairStatus.valueOf(newStatus);

        Set<RepairStatus> allowedStatuses = allowedStatuses(newStatusEnum);

        return allowedStatuses.contains(currentStatusEnum);
    }

    private Set<RepairStatus> allowedStatuses(RepairStatus newStatus) {
        Set<RepairStatus> allowedStatuses = new HashSet<>();
        switch (newStatus) {
            case PENDING:
                break;
            case CONFIRMED:
                allowedStatuses.addAll(List.of(
                        RepairStatus.PENDING));
                break;
            case DIAGNOSTIC_WAITING:
                allowedStatuses.addAll(List.of(
                        RepairStatus.CONFIRMED));
                break;
            case DIAGNOSTIC:
                allowedStatuses.addAll(List.of(
                        RepairStatus.DIAGNOSTIC_WAITING,
                        RepairStatus.REPAIR_WAITING));
                break;
            case PAYMENT_CONFIRM_WAITING:
                allowedStatuses.addAll(List.of(
                        RepairStatus.DIAGNOSTIC));
                break;
            case PAYMENT_CONFIRMED:
            case PAYMENT_CANCELED:
                allowedStatuses.addAll(List.of(
                        RepairStatus.PAYMENT_CONFIRM_WAITING));
                break;
            case REPAIR_WAITING:
                allowedStatuses.addAll(List.of(
                        RepairStatus.DIAGNOSTIC,
                        RepairStatus.PAYMENT_CONFIRMED,
                        RepairStatus.REPAIR));
                break;
            case REPAIR:
                allowedStatuses.addAll(List.of(
                        RepairStatus.REPAIR_WAITING));
            case RETURN:
                allowedStatuses.addAll(List.of(
                        RepairStatus.PAYMENT_CANCELED,
                        RepairStatus.REPAIR));
                break;
            case COMPLETED:
                allowedStatuses.addAll(List.of(
                        RepairStatus.RETURN));
        }
        return allowedStatuses;
    }
}
