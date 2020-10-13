package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatusHistory;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.InvalidStatusException;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.webInternalApp.dto.RepairDto;
import lt.ivl.webInternalApp.pdf.PdfGenerator;
import lt.ivl.webInternalApp.security.EmployeePrincipal;
import lt.ivl.webInternalApp.service.InternalCustomerService;
import lt.ivl.webInternalApp.service.InternalRepairService;
import lt.ivl.webInternalApp.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/repair")
public class ManageRepairController {
    @Autowired
    private InternalRepairService internalRepairService;

    @Autowired
    private InternalCustomerService internalCustomerService;

    @Autowired
    private PdfGenerator pdfGenerator;

    @Autowired
    private MailSender mailSender;

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

    @GetMapping("/{repair}/confirm")
    public String showConfirmForm(@PathVariable("repair") Repair repair, Model model) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);

        RepairDto repairDto = new RepairDto(repair);
        model.addAttribute("repairDto", repairDto);

        model.addAttribute("repair", repair);
        return "repair/confirm";
    }

    @PostMapping("/{repair}/confirm")
    public String confirm(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            @ModelAttribute("repairDto") @Valid RepairDto repairDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<Customer> customerList = internalCustomerService.findAll();
            model.addAttribute("customerList", customerList);
            model.addAttribute("repairDto", repairDto);
            model.addAttribute("repair", repair);

            model.addAttribute("messageError", "Formoje yra klaidų");
            return "repair/confirm";
        }

        try {
            Employee employee = employeePrincipal.getEmployee();
            repair = internalRepairService.confirmRepair(repair, repairDto, employee);
            mailSender.sendRepairConfirmedToCustomer(repair);
            return "redirect:/repair/{repair}/view";
        } catch (CustomerNotFoundInDBException | InvalidStatusException e) {
            List<Customer> customerList = internalCustomerService.findAll();
            model.addAttribute("customerList", customerList);
            model.addAttribute("repairDto", repairDto);
            model.addAttribute("repair", repair);

            model.addAttribute("messageError", e.getMessage());
            return "repair/confirm";
        }
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
