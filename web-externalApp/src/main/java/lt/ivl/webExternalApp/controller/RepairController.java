package lt.ivl.webExternalApp.controller;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.Repair;
import lt.ivl.webExternalApp.domain.RepairStatus;
import lt.ivl.webExternalApp.dto.RepairDto;
import lt.ivl.webExternalApp.exception.ItemNotFoundException;
import lt.ivl.webExternalApp.security.CustomerPrincipal;
import lt.ivl.webExternalApp.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/repair")
public class RepairController {
    @Autowired
    private RepairService repairService;

    @GetMapping("/index")
    public String index(
            @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            Model model
    ) {
        model.addAttribute("pendingRepairs", repairService.findRepairsWithSpecificStatusByCustomer(
                customerPrincipal.getCustomer(), Arrays.asList(RepairStatus.PENDING)
        ));
        return "repair/index";
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
            model.addAttribute("message", "Form has errors");
            return "repair/add";
        }
        repairService.createNewRepairItemByCustomer(customerPrincipal.getCustomer(), repairDto);

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
            Repair repair = repairService.findRepairToDelete(customerPrincipal.getCustomer(), repairId).get();
            model.addAttribute("repair", repair);
        } catch (ItemNotFoundException | NumberFormatException e) {
            model.addAttribute("message", e.getMessage());
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

            repairService.deleteRepairItemByCustomer(customer, repairId);
        } catch (ItemNotFoundException e) {
            model.addAttribute("message", e.getMessage());
            return "repair/delete";
        }

        return "redirect:/repair/index";
    }
}
