package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepairService {
    @Autowired
    private RepairRepository repairRepository;
}
