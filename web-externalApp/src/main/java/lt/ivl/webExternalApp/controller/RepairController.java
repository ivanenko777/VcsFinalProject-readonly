package lt.ivl.webExternalApp.controller;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.webExternalApp.dto.RepairDto;
import lt.ivl.components.exception.ItemNotFoundException;
import lt.ivl.webExternalApp.security.CustomerPrincipal;
import lt.ivl.webExternalApp.service.MailSender;
import lt.ivl.webExternalApp.service.ExternalRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/repair")
public class RepairController {
    @Autowired
    private ExternalRepairService externalRepairService;

    @Autowired
    private MailSender mailSender;

    @GetMapping("/index")
    public String index(
            @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            Model model
    ) {
        Customer customer = customerPrincipal.getCustomer();
        List<Repair> repairs = externalRepairService.findAllCustomerRepairs(customer);
        model.addAttribute("repairList", repairs);
        return "repair/index";
    }

    @GetMapping("/{id}/view")
    public String view(
            @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            @PathVariable("id") String id,
            Model model
    ) {
        try {
            int repairId = Integer.parseInt(id);
            Customer customer = customerPrincipal.getCustomer();

            Repair repair = externalRepairService.findByCustomer(customer, repairId);
            model.addAttribute("repair", repair);
        } catch (ItemNotFoundException | NumberFormatException e) {
            model.addAttribute("messageError", e.getMessage());
        }
        return "repair/view";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("repair", new RepairDto());
        return "repair/add";
    }

    @PostMapping("/add")
    public String create(
            @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            @ModelAttribute("repair") @Valid RepairDto repairDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "repair/add";
        }
        Customer customer = customerPrincipal.getCustomer();
        Repair newRepair = externalRepairService.createNewRepairItemByCustomer(customer, repairDto);
        mailSender.sendRepairRequestToCustomer(customer, newRepair);
        return "redirect:/repair/index";
    }

    @GetMapping("{id}/delete")
    public String delete(
            @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            @PathVariable("id") String id,
            Model model
    ) {
        try {
            int repairId = Integer.parseInt(id);
            Customer customer = customerPrincipal.getCustomer();
            Repair repair = externalRepairService.findToDeleteByCustomer(customer, repairId).get();
            model.addAttribute("repair", repair);
        } catch (ItemNotFoundException | NumberFormatException e) {
            model.addAttribute("messageError", e.getMessage());
        }

        return "repair/delete";
    }

    @PostMapping("{id}/delete")
    public String destroy(
            @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            @PathVariable("id") String id,
            Model model
    ) {
        try {
            int repairId = Integer.parseInt(id);
            Customer customer = customerPrincipal.getCustomer();
            externalRepairService.deleteByCustomer(customer, repairId);
        } catch (ItemNotFoundException e) {
            model.addAttribute("messageError", e.getMessage());
            return "repair/delete";
        }

        return "redirect:/repair/index";
    }
}
