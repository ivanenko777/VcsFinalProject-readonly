package lt.ivl.webExternalApp.controller;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.CustomerResetPasswordToken;
import lt.ivl.components.domain.CustomerVerificationToken;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.PasswordDontMatchException;
import lt.ivl.components.exception.TokenExpiredException;
import lt.ivl.components.exception.TokenInvalidException;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.dto.ResetPasswordDto;
import lt.ivl.webExternalApp.exception.*;
import lt.ivl.webExternalApp.service.ExternalCustomerService;
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
    private ExternalCustomerService externalCustomerService;

    @Autowired
    private MailSender mailSender;

    @GetMapping("/login")
    public String login(Model model) {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("customer", new CustomerDto());
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String register(
            @ModelAttribute("customer") @Valid CustomerDto customerDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "auth/registration";
        }
        try {
            Customer customer = externalCustomerService.registerNewCustomerAccount(customerDto);
            String token = UUID.randomUUID().toString();
            externalCustomerService.createVerificationTokenForCustomerAccount(customer, token);
            mailSender.sendAccountVerificationEmailToCustomer(customer, token);
            model.addAttribute("messageInfo", "Patvirtinkite registraciją. Instrukcijas rasite laiške.");
            return "auth/activation";
        } catch (UsernameExistsInDatabaseException e) {
            model.addAttribute("messageError", e.getMessage() + " Jei pamiršote prisijungimo duomenis, pasinaudokite slaptažodžio priminimo funkcija.");
            return "auth/registration";
        } catch (PasswordDontMatchException e) {
            model.addAttribute("messageError", e.getMessage());
            return "auth/registration";
        }
    }

    @GetMapping("/activation")
    public String activate(
            @RequestParam(value = "token", required = false) String token,
            Model model
    ) {
        try {
            CustomerVerificationToken verificationToken = externalCustomerService.verifyCustomerAccountVerificationToken(token);
            externalCustomerService.activateCustomerAccount(verificationToken);
            Customer customer = verificationToken.getCustomer();
            mailSender.sendAccountActivatedEmailToCustomer(customer);
            model.addAttribute("messageInfo", "Registracija patvirtinta.");
        } catch (TokenInvalidException e) {
            model.addAttribute("messageError", "Nuoroda negalioja.");
        } catch (TokenExpiredException e) {
            CustomerVerificationToken verificationToken = externalCustomerService.generateNewVerificationTokenForCustomerAccount(token);
            Customer customer = verificationToken.getCustomer();
            String newToken = verificationToken.getToken();
            mailSender.sendAccountVerificationEmailToCustomer(customer, newToken);
            model.addAttribute("messageError", "Nuorodos galiojimo laikas baigėsi. Nauja nuoroda išsiųsta į el. paštą.");
        }
        return "auth/activation";
    }

    @GetMapping("/remember-password")
    public String rememberPasswordForm() {
        return "auth/rememberPassword";
    }

    @PostMapping("/remember-password")
    public String rememberPassword(
            @RequestParam("email") String customerEmail,
            Model model
    ) {
        try {
            Customer customer = externalCustomerService.findCustomerAccountByEmail(customerEmail);
            CustomerResetPasswordToken resetPasswordToken = externalCustomerService.createPasswordResetTokenForCustomerAccount(customer);
            String token = resetPasswordToken.getToken();
            mailSender.sendResetPasswordEmailToCustomer(customer, token);
            model.addAttribute("messageInfo", "Slaptažodžio keitimo instrukcijos išsiųstos į el. paštą.");
            return "auth/rememberPassword";
        } catch (CustomerNotFoundInDBException e) {
            model.addAttribute("messageError", e.getMessage());
            return "auth/rememberPassword";
        }
    }

    @GetMapping("/reset-password")
    private String resetPasswordForm(
            @RequestParam(value = "token", required = false) String token,
            Model model
    ) {
        try {
            model.addAttribute("pageHideForm", false);
            externalCustomerService.verifyCustomerAccountPasswordResetToken(token);
        } catch (TokenInvalidException e) {
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageError", "Nuoroda negalioja.");
        } catch (TokenExpiredException e) {
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageError", "Nuorodos galiojimo laikas baigėsi.");
        }
        model.addAttribute("token", token);
        model.addAttribute("resetPassword", new ResetPasswordDto());
        return "auth/resetPassword";
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
            model.addAttribute("pageHideForm", false);
            model.addAttribute("messageError", "Formoje yra klaidų");
            model.addAttribute("token", token);
            return "auth/resetPassword";
        }

        try {
            CustomerResetPasswordToken resetPasswordToken = externalCustomerService.verifyCustomerAccountPasswordResetToken(token);
            Customer customer = resetPasswordToken.getCustomer();
            externalCustomerService.resetCustomerAccountPassword(customer, resetPasswordDto, resetPasswordToken);
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageInfo", "Slaptažodis pakeistas.");
        } catch (TokenInvalidException e) {
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageError", "Nuoroda negalioja.");
        } catch (PasswordDontMatchException e) {
            model.addAttribute("pageHideForm", false);
            model.addAttribute("messageError", e.getMessage());
        } catch (TokenExpiredException e) {
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageError", "Nuorodos galiojimo laikas baigėsi.");
        }

        model.addAttribute("resetPassword", resetPasswordDto);
        model.addAttribute("token", token);
        return "auth/resetPassword";
    }
}
