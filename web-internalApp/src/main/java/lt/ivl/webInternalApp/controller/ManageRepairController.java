package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.*;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.InvalidStatusException;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.webInternalApp.dto.RepairDto;
import lt.ivl.webInternalApp.dto.RepairStatusNoteDto;
import lt.ivl.webInternalApp.dto.RepairStatusQuestionDto;
import lt.ivl.webInternalApp.dto.RepairStatusStoredDto;
import lt.ivl.webInternalApp.pdf.PdfGenerator;
import lt.ivl.webInternalApp.security.EmployeePrincipal;
import lt.ivl.webInternalApp.service.InternalCustomerService;
import lt.ivl.webInternalApp.service.InternalRepairService;
import lt.ivl.webInternalApp.service.MailSender;
import lt.ivl.webInternalApp.utils.UtilsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public String list(Model model) {
        List<Repair> repairList = internalRepairService.findAll();
        model.addAttribute("repairList", repairList);
        return "repair/list";
    }

    @GetMapping("/{repair}/view")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public String view(@PathVariable("repair") Repair repair, Model model) {
        model.addAttribute("utils", UtilsHelper.getInstance());
        model.addAttribute("repair", repair);
        return "repair/view";
    }

    @GetMapping("/{repair}/history")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public String statusHistory(@PathVariable("repair") Repair repair, Model model) {
        List<RepairStatusHistory> statusHistoryList = repair.getStatusHistory();
        model.addAttribute("statusHistoryList", statusHistoryList);
        model.addAttribute("repair", repair);
        return "repair/history";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showCreateForm(Model model) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);

        model.addAttribute("repairDto", new RepairDto());
        return "repair/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String create(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @ModelAttribute("repairDto") @Valid RepairDto repairDto,
            BindingResult bindingResult,
            Model model
    ) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);
        model.addAttribute("repairDto", repairDto);

        if (bindingResult.hasErrors()) {
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
            model.addAttribute("messageError", e.getMessage());
            return "repair/add";
        }
    }

    @GetMapping("/{repair}/edit")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showUpdateForm(@PathVariable("repair") Repair repair, Model model) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);

        RepairDto repairDto = new RepairDto(repair);
        model.addAttribute("repairDto", repairDto);

        model.addAttribute("repair", repair);
        return "repair/edit";
    }

    @PostMapping("/{repair}/edit")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String update(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            @ModelAttribute("repairDto") @Valid RepairDto repairDto,
            BindingResult bindingResult,
            Model model
    ) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);
        model.addAttribute("repairDto", repairDto);
        model.addAttribute("repair", repair);

        if (bindingResult.hasErrors()) {
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "repair/edit";
        }

        try {
            Employee employee = employeePrincipal.getEmployee();
            repair = internalRepairService.updateRepair(repair, repairDto, employee);
            if (repair.getStatus() == RepairStatus.CONFIRMED) mailSender.sendRepairConfirmedToCustomer(repair);
            return "redirect:/repair/{repair}/view";
        } catch (CustomerNotFoundInDBException | InvalidStatusException | ItemNotFoundException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/edit";
        }
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public void exportConfirmedPdf(@PathVariable("repair") Repair repair, HttpServletResponse response) {
        try {
            pdfGenerator.generateRepairConfirmedPdf(repair, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("{repair}/stored")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showStoredForm(@PathVariable("repair") Repair repair, Model model) {
        model.addAttribute("repairStatusStoredDto", new RepairStatusStoredDto());
        model.addAttribute("repairStatusNoteDto", new RepairStatusNoteDto());
        model.addAttribute("repair", repair);
        return "repair/store";
    }

    @PostMapping("{repair}/stored")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
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

    @GetMapping("{repair}/start-diagnostic")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public String showStartDiagnostic(@PathVariable("repair") Repair repair, Model model) {
        model.addAttribute("repair", repair);
        return "repair/diagnostic-start";
    }

    @PostMapping("{repair}/start-diagnostic")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public String startDiagnostic(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            Model model
    ) {
        try {
            Employee employee = employeePrincipal.getEmployee();
            internalRepairService.startDiagnostic(repair, employee);
            return "redirect:/repair/{repair}/view";
        } catch (InvalidStatusException e) {
            model.addAttribute("repair", repair);
            model.addAttribute("messageError", e.getMessage());
            return "repair/diagnostic-start";
        }
    }

    @GetMapping("{repair}/finish-diagnostic")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public String showFinishDiagnostic(@PathVariable("repair") Repair repair, Model model) {
        boolean deviceWarranty = repair.isDeviceWarranty();
        if (deviceWarranty) model.addAttribute("messageInfo", "Įrenginiui galioja garantija. Nemokamas remontas.");
        else model.addAttribute("messageWarning", "Įrenginiui garantija negalioja. Mokamas remontas.");

        model.addAttribute("repairStatusStoredDto", new RepairStatusStoredDto());
        model.addAttribute("repairStatusNoteDto", new RepairStatusNoteDto());
        model.addAttribute("repair", repair);
        return "repair/diagnostic-finish";
    }

    @PostMapping("{repair}/finish-diagnostic")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public String finishDiagnostic(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            @Valid @ModelAttribute("repairStatusStoredDto") RepairStatusStoredDto repairStatusStoredDto,
            BindingResult repairStatusStoredDtoBindingResult,
            @Valid @ModelAttribute("repairStatusNoteDto") RepairStatusNoteDto repairStatusNoteDto,
            BindingResult repairStatusNoteDtoBindingResult,
            Model model
    ) {
        boolean deviceWarranty = repair.isDeviceWarranty();
        if (deviceWarranty) model.addAttribute("messageInfo", "Prieraisui galioja garantija");
        else model.addAttribute("messageWarning", "Prieraisui  garantija negalioja");

        model.addAttribute("repairStatusStoredDto", repairStatusStoredDto);
        model.addAttribute("repairStatusNoteDto", repairStatusNoteDto);
        model.addAttribute("repair", repair);

        if (repairStatusStoredDtoBindingResult.hasErrors() || repairStatusNoteDtoBindingResult.hasErrors()) {
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "repair/diagnostic-finish";
        }

        try {
            Employee employee = employeePrincipal.getEmployee();
            repair = internalRepairService.finishDiagnostic(repair, repairStatusStoredDto, repairStatusNoteDto, employee);
            if (repair.getStatus() == RepairStatus.PAYMENT_CONFIRM_WAITING) {
                mailSender.sendRepairPaymentConfirmWaitingToCustomer(repair, repairStatusNoteDto);
            }
            return "redirect:/repair/{repair}/view";
        } catch (InvalidStatusException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/diagnostic-finish";
        }
    }

    @GetMapping("{repair}/payment")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String confirmPaymentPage(@PathVariable("repair") Repair repair, Model model) {
        model.addAttribute("repair", repair);
        return "repair/payment";
    }

    @PostMapping("{repair}/payment-confirm")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String confirmPayment(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            Model model
    ) {
        model.addAttribute("repair", repair);
        try {
            Employee employee = employeePrincipal.getEmployee();
            internalRepairService.confirmPayment(repair, employee);
            return "redirect:/repair/{repair}/view";
        } catch (InvalidStatusException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/payment";
        }
    }

    @PostMapping("{repair}/payment-cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String cancelPayment(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            Model model
    ) {
        model.addAttribute("repair", repair);
        try {
            Employee employee = employeePrincipal.getEmployee();
            internalRepairService.cancelPayment(repair, employee);
            return "redirect:/repair/{repair}/view";
        } catch (InvalidStatusException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/payment";
        }
    }

    @GetMapping("{repair}/start-repair")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public String showStartRepair(@PathVariable("repair") Repair repair, Model model) {
        model.addAttribute("repair", repair);
        return "repair/repair-start";
    }

    @PostMapping("{repair}/start-repair")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public String startRepair(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            Model model
    ) {
        try {
            Employee employee = employeePrincipal.getEmployee();
            internalRepairService.startRepair(repair, employee);
            return "redirect:/repair/{repair}/view";
        } catch (InvalidStatusException e) {
            model.addAttribute("repair", repair);
            model.addAttribute("messageError", e.getMessage());
            return "repair/repair-start";
        }
    }

    @GetMapping("{repair}/finish-repair")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public String showFinishRepair(@PathVariable("repair") Repair repair, Model model) {
        model.addAttribute("repairStatusStoredDto", new RepairStatusStoredDto());
        model.addAttribute("repairStatusNoteDto", new RepairStatusNoteDto());
        model.addAttribute("repairStatusQuestionDto", new RepairStatusQuestionDto());
        model.addAttribute("repair", repair);
        return "repair/repair-finish";
    }

    @PostMapping("{repair}/finish-repair")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public String finishRepair(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            @Valid @ModelAttribute("repairStatusQuestionDto") RepairStatusQuestionDto repairStatusQuestionDto,
            BindingResult repairStatusQuestionDtoBindingResult,
            @Valid @ModelAttribute("repairStatusStoredDto") RepairStatusStoredDto repairStatusStoredDto,
            BindingResult repairStatusStoredDtoBindingResult,
            @Valid @ModelAttribute("repairStatusNoteDto") RepairStatusNoteDto repairStatusNoteDto,
            BindingResult repairStatusNoteDtoBindingResult,
            Model model
    ) {
        model.addAttribute("repairStatusStoredDto", repairStatusStoredDto);
        model.addAttribute("repairStatusNoteDto", repairStatusNoteDto);
        model.addAttribute("repairStatusQuestionDto", repairStatusQuestionDto);
        model.addAttribute("repair", repair);

        if (repairStatusStoredDtoBindingResult.hasErrors()
                || repairStatusNoteDtoBindingResult.hasErrors()
                || repairStatusQuestionDtoBindingResult.hasErrors()) {
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "repair/repair-finish";
        }

        try {
            Employee employee = employeePrincipal.getEmployee();
            internalRepairService.finishRepair(repair, repairStatusStoredDto, repairStatusNoteDto, repairStatusQuestionDto, employee);
            return "redirect:/repair/{repair}/view";
        } catch (InvalidStatusException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/repair-finish";
        }
    }

    @GetMapping("{repair}/return")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showCompleteForm(@PathVariable("repair") Repair repair, Model model) {
        boolean deviceWarranty = repair.isDeviceWarranty();
        if (deviceWarranty) model.addAttribute("messageInfo", "Įrenginiui galioja garantija. Nemokamas remontas.");
        else model.addAttribute("messageWarning", "Įrenginiui garantija negalioja. Mokamas remontas.");

        model.addAttribute("repairStatusNoteDto", new RepairStatusNoteDto());
        model.addAttribute("repair", repair);
        return "repair/return";
    }

    @PostMapping("{repair}/return")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String complete(
            @AuthenticationPrincipal EmployeePrincipal employeePrincipal,
            @PathVariable("repair") Repair repair,
            @ModelAttribute("repairStatusNoteDto") RepairStatusNoteDto repairStatusNoteDto,
            BindingResult repairStatusNoteDtoBindingResult,
            Model model
    ) {
        model.addAttribute("repairStatusNoteDto", repairStatusNoteDto);
        model.addAttribute("repair", repair);

        if (repairStatusNoteDtoBindingResult.hasErrors()) {
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "repair/return";
        }

        try {
            Employee employee = employeePrincipal.getEmployee();
            repair = internalRepairService.completeRepair(repair, repairStatusNoteDto, employee);
            mailSender.sendRepairCompeteToCustomer(repair);
            return "redirect:/repair/{repair}/view";
        } catch (InvalidStatusException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/return";
        }
    }
}
