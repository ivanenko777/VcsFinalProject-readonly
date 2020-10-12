package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatusHistory;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.webInternalApp.pdf.PdfGenerator;
import lt.ivl.webInternalApp.service.InternalRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/repair")
public class ManageRepairController {
    @Autowired
    private InternalRepairService internalRepairService;

    @Autowired
    private PdfGenerator pdfGenerator;

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

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") String id, Model model) {
        int repairId = Integer.parseInt(id);
        model.addAttribute("repairId", repairId);

        try {
            internalRepairService.findRepairToDelete(repairId);
        } catch (ItemNotFoundException e) {
            model.addAttribute("messageError", e.getMessage());
        }
        return "repair/delete";
    }

    @PostMapping("{id}/delete")
    public String destroy(@PathVariable("id") String id, Model model) {
        int repairId = Integer.parseInt(id);
        model.addAttribute("repairId", repairId);

        try {
            internalRepairService.deleteRepair(repairId);
        } catch (ItemNotFoundException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/delete";
        }
        return "redirect:/repair/list";
    }

    @GetMapping(value = "{repair}/export_confirmed_pdf", produces = {"application/json", "application/x-pdf"})
    public void exportConfirmedPdf(@PathVariable("repair") Repair repair, HttpServletResponse response) {
        try {
            pdfGenerator.generateRepairConfirmedPdf(repair, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
