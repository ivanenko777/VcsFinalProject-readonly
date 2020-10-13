package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.*;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.InvalidStatusException;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.webInternalApp.dto.RepairDto;
import lt.ivl.webInternalApp.dto.RepairStatusNoteDto;
import lt.ivl.webInternalApp.dto.RepairStatusStoredDto;
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

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);

        model.addAttribute("repairDto", new RepairDto());
        return "repair/add";
    }

    @PostMapping("/add")
    public String create(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @ModelAttribute("repairDto") @Valid RepairDto repairDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<Customer> customerList = internalCustomerService.findAll();
            model.addAttribute("customerList", customerList);
            model.addAttribute("repairDto", repairDto);

            model.addAttribute("messageError", "Formoje yra klaidų");
            return "repair/add";
        }

        try {
            Employee employee = employeePrincipal.getEmployee();
            Repair repair = internalRepairService.createRepair(repairDto, employee);
            if (repair.getStatus() == RepairStatus.CONFIRMED) mailSender.sendRepairConfirmedToCustomer(repair);

            int repairId = repair.getId();
            return "redirect:/repair/" + repairId + "/view";
        } catch (CustomerNotFoundInDBException | InvalidStatusException e) {
            List<Customer> customerList = internalCustomerService.findAll();
            model.addAttribute("customerList", customerList);
            model.addAttribute("repairDto", repairDto);

            model.addAttribute("messageError", e.getMessage());
            return "repair/add";
        }
    }

    @GetMapping("/{repair}/edit")
    public String showUpdateForm(@PathVariable("repair") Repair repair, Model model) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);

        RepairDto repairDto = new RepairDto(repair);
        model.addAttribute("repairDto", repairDto);

        model.addAttribute("repair", repair);
        return "repair/edit";
    }

    @PostMapping("/{repair}/edit")
    public String update(
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
            return "repair/edit";
        }

        try {
            Employee employee = employeePrincipal.getEmployee();
            repair = internalRepairService.updateRepair(repair, repairDto, employee);
            if (repair.getStatus() == RepairStatus.CONFIRMED) mailSender.sendRepairConfirmedToCustomer(repair);
            return "redirect:/repair/{repair}/view";
        } catch (CustomerNotFoundInDBException | InvalidStatusException | ItemNotFoundException e) {
            List<Customer> customerList = internalCustomerService.findAll();
            model.addAttribute("customerList", customerList);
            model.addAttribute("repairDto", repairDto);
            model.addAttribute("repair", repair);

            model.addAttribute("messageError", e.getMessage());
            return "repair/edit";
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

    @PostMapping("/{id}/delete")
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

    @GetMapping(value = "/{repair}/export_confirmed_pdf", produces = {"application/json", "application/x-pdf"})
    public void exportConfirmedPdf(@PathVariable("repair") Repair repair, HttpServletResponse response) {
        try {
            pdfGenerator.generateRepairConfirmedPdf(repair, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("{repair}/stored")
    public String showStoredForm(@PathVariable("repair") Repair repair, Model model) {
        model.addAttribute("repairStatusStoredDto", new RepairStatusStoredDto());
        model.addAttribute("repairStatusNoteDto", new RepairStatusNoteDto());
        model.addAttribute("repair", repair);
        return "repair/store";
    }

    @PostMapping("{repair}/stored")
    public String stored(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            @Valid @ModelAttribute("repairStatusStoredDto") RepairStatusStoredDto repairStatusStoredDto,
            BindingResult repairStatusStoredDtoBindingResult,
            @ModelAttribute("repairStatusNoteDto") RepairStatusNoteDto repairStatusNoteDto,
            BindingResult repairStatusNoteDtoBindingResult,
            Model model
    ) {
        model.addAttribute("repairStatusStoredDto", repairStatusStoredDto);
        model.addAttribute("repairStatusNoteDto", repairStatusNoteDto);
        model.addAttribute("repair", repair);

        if (repairStatusStoredDtoBindingResult.hasErrors() || repairStatusNoteDtoBindingResult.hasErrors()) {
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "repair/store";
        }

        try {
            Employee employee = employeePrincipal.getEmployee();
            internalRepairService.storeDevice(repair, repairStatusStoredDto, repairStatusNoteDto, employee);
            return "redirect:/repair/{repair}/view";
        } catch (InvalidStatusException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/store";
        }
    }
}
