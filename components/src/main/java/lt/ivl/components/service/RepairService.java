package lt.ivl.components.service;

import lt.ivl.components.domain.Repair;
import lt.ivl.components.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepairService {
    @Autowired
    private RepairRepository repairRepository;

    public Repair saveRepair(Repair repair) {
        return repairRepository.save(repair);
    }
}
