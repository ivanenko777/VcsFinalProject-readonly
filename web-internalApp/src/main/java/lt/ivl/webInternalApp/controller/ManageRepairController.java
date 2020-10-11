package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatusHistory;
import lt.ivl.webInternalApp.service.InternalRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/repair")
public class ManageRepairController {
    @Autowired
    private InternalRepairService internalRepairService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Repair> repairList = internalRepairService.findAll();
        model.addAttribute("repairList", repairList);
        return "repair/list";
    }

    @GetMapping("/{repair}/view")
    public String view(@PathVariable("repair") Repair repair, Model model) {
        model.addAttribute("repair", repair);
        return "repair/view";
    }

    @GetMapping("/{repair}/history")
    public String statusHistory(@PathVariable("repair") Repair repair, Model model) {
        List<RepairStatusHistory> statusHistoryList = repair.getStatusHistory();
        model.addAttribute("statusHistoryList", statusHistoryList);
        model.addAttribute("repair", repair);
        return "repair/history";
    }
}
