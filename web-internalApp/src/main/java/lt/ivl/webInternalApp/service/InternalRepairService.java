package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Repair;
import lt.ivl.components.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternalRepairService {
    @Autowired
    private RepairService componentRepairService;

    public List<Repair> findAll() {
        return componentRepairService.findAll();
    }
}
