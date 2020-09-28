package lt.ivl.webExternalApp.controller;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.CustomerResetPasswordToken;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.dto.ResetPasswordDto;
import lt.ivl.webExternalApp.exception.*;
import lt.ivl.webExternalApp.service.CustomerService;
import lt.ivl.webExternalApp.service.MailSender;
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

    @Autowired
    private MailSender mailSender;

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
            customerService.registerNewCustomerAccount(customerDto);
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
                customerService.activateByVerificationToken(token);
            } catch (TokenInvalidException | TokenExpiredException e) {
                model.addAttribute("message", e.getMessage());
                return "/activation";
            }
            return "redirect:/login";
        }

        model.addAttribute("info", "Patvirtinkite registraciją. Instrukcijas rasite laiške.");
        return "/activation";
    }

    @GetMapping("/remember-password")
    public String rememberPasswordForm() {
        return "rememberPassword";
    }

    @PostMapping("/remember-password")
    public String rememberPassword(
            @RequestParam("email") String customerEmail,
            Model model
    ) {
        try {
            Customer customer = customerService.findCustomerByEmail(customerEmail);
            CustomerResetPasswordToken resetPasswordToken = customerService.createPasswordResetTokenForCustomer(customer);
            String token = resetPasswordToken.getToken();
            mailSender.sendResetPasswordEmailToCustomer(customer, token);
        } catch (CustomerNotFoundInDBException e) {
            model.addAttribute("message", e.getMessage());
            return "rememberPassword";
        }

        model.addAttribute("info", "Slaptažodio pakeitimo instrukcijas rasite laiške.");
        return "rememberPassword";
    }

    @GetMapping("/reset-password")
    private String resetPasswordForm(Model model) {
        model.addAttribute("resetPassword", new ResetPasswordDto());
        return "resetPassword";
    }
}
