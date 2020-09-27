package lt.ivl.webExternalApp.controller;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.exception.PasswordDontMatchException;
import lt.ivl.webExternalApp.exception.TokenExpiredException;
import lt.ivl.webExternalApp.exception.TokenInvalidException;
import lt.ivl.webExternalApp.exception.UsernameExistsInDatabaseException;
import lt.ivl.webExternalApp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
            Customer customer = customerService.registerNewCustomerAccount(customerDto);
            customerService.confirmNewCustomerRegistration(customer);
        } catch (UsernameExistsInDatabaseException | PasswordDontMatchException e) {
            model.addAttribute("message", e.getMessage());
            return "registration";
        }
        return "redirect:/activation";
    }

    @GetMapping("/activation")
    public String activate(
            @RequestParam(value = "token", required = false) String token,
            Model model
    ) {
        if (token != null && !token.isEmpty()) {
            try {
                customerService.validateVerificationToken(token);
            } catch (TokenInvalidException | TokenExpiredException e) {
                model.addAttribute("message", e.getMessage());
                return "/activation";
            }
            return "redirect:/login";
        }

        model.addAttribute("info", "Patvirtinkite registraciją. Instrukcijas rasite išsiųstame laiške.");
        return "/activation";
    }
}
