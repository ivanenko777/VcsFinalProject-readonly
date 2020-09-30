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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

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
            Customer customer = customerService.registerNewCustomerAccount(customerDto);
            String token = UUID.randomUUID().toString();
            customerService.createVerificationTokenForCustomer(customer, token);
            mailSender.sendVerificationEmailToCustomer(customer, token);
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
                model.addAttribute("info", "Patvirtinkite registraciją. Instrukcijas rasite laiške.");
            } catch (TokenInvalidException | TokenExpiredException e) {
                model.addAttribute("message", e.getMessage());
                return "/activation";
            }
            return "redirect:/login";
        } else {
            model.addAttribute("message", "Tokenas nerastas");
        }

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
            Customer customer = customerService.findCustomerAccountByEmail(customerEmail);
            CustomerResetPasswordToken resetPasswordToken = customerService.createPasswordResetTokenForCustomer(customer);
            String token = resetPasswordToken.getToken();
            mailSender.sendResetPasswordEmailToCustomer(customer, token);
            model.addAttribute("info", "Slaptažodio pakeitimo instrukcijas rasite laiške.");
        } catch (CustomerNotFoundInDBException e) {
            model.addAttribute("message", e.getMessage());
            return "rememberPassword";
        }

        return "rememberPassword";
    }

    @GetMapping("/reset-password")
    private String resetPasswordForm(
            @RequestParam(value = "token", required = false) String token,
            Model model
    ) {
        try {
            customerService.validatePasswordResetToken(token);
        } catch (TokenInvalidException | TokenExpiredException e) {
            model.addAttribute("message", e.getMessage());
            return "resetPassword";
        }

        model.addAttribute("token", token);
        model.addAttribute("resetPassword", new ResetPasswordDto());
        return "resetPassword";
    }

    @PostMapping("/reset-password")
    private String resetPassword(
            HttpServletRequest request,
            @ModelAttribute("resetPassword") @Valid ResetPasswordDto resetPasswordDto,
            BindingResult bindingResult,
//            @RequestParam(value = "token", required = false) String token,
            Model model
    ) {
        // Paimam GETparametra token, is POSTrequesto
        String token = request.getParameter("token");

        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Form has errors");
            model.addAttribute("token", token);
            return "resetPassword";
        }

        try {
            CustomerResetPasswordToken resetPasswordToken = customerService.validatePasswordResetToken(token);
            Customer customer = resetPasswordToken.getCustomer();
            customerService.resetCustomerPassword(customer, resetPasswordDto, resetPasswordToken);
            model.addAttribute("token", token);
            model.addAttribute("info", "Slaptažodis pakeistas.");
        } catch (TokenInvalidException | TokenExpiredException | PasswordDontMatchException e) {
            model.addAttribute("message", e.getMessage());
            return "resetPassword";
        }
        return "resetPassword";
    }
}
