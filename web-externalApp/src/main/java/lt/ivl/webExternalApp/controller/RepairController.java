package lt.ivl.webExternalApp.controller;

import lt.ivl.webExternalApp.dto.RepairDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/repair")
public class RepairController {
    @GetMapping("/index")
    public String index() {
        return "repair/index";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("repair", new RepairDto());
        return "repair/add";
    }

    @PostMapping("/add")
    public String create(
            @ModelAttribute("repair") @Valid RepairDto repairDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Form has errors");
            return "repair/add";
        }
        //TODO: implement create new repair

        return "redirect:/repair/index";
    }
}
