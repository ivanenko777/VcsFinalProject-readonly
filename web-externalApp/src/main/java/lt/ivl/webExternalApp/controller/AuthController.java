package lt.ivl.webExternalApp.controller;

import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.exception.PasswordDontMatchException;
import lt.ivl.webExternalApp.exception.UsernameExistsInDatabaseException;
import lt.ivl.webExternalApp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/")
public class AuthController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("customer", new CustomerDto());
        return "registration";
    }

    @PostMapping("/registration")
    public String register(
            @ModelAttribute("customer") @Valid CustomerDto customerDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Form has errors");
            return "registration";
        }
        try {
            customerService.createFromRegistrationForm(customerDto);
        } catch (UsernameExistsInDatabaseException | PasswordDontMatchException e) {
            model.addAttribute("message", e.getMessage());
            return "registration";
        }
        return "redirect:/login";
    }
}
