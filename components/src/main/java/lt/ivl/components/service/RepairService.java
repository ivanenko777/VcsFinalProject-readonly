package lt.ivl.components.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
