package lt.ivl.webExternalApp.controller;

import lt.ivl.webExternalApp.domain.RepairStatus;
import lt.ivl.webExternalApp.dto.RepairDto;
import lt.ivl.webExternalApp.security.CustomerPrincipal;
import lt.ivl.webExternalApp.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
