package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeResetPasswordToken;
import lt.ivl.components.exception.EmployeeNotFoundInDbException;
import lt.ivl.components.exception.PasswordDontMatchException;
import lt.ivl.components.exception.TokenExpiredException;
import lt.ivl.components.exception.TokenInvalidException;
import lt.ivl.webInternalApp.dto.ResetPasswordDto;
import lt.ivl.webInternalApp.service.InternalEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/")
public class AuthController {
    @Autowired
    private InternalEmployeeService internalEmployeeService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/remember-password")
    public String rememberPasswordForm() {
        return "auth/rememberPassword";
    }

    @PostMapping("/remember-password")
    public String rememberPassword(
            @RequestParam("email") String employeeEmail,
            Model model
    ) {
        try {
            Employee employee = internalEmployeeService.findEmployeeAccountByEmail(employeeEmail);
            EmployeeResetPasswordToken resetPasswordToken = internalEmployeeService.createPasswordResetTokenForEmployeeAccount(employee);
            String token = resetPasswordToken.getToken();
            // TODO: send email
            model.addAttribute("messageInfo", "Slaptažodžio keitimo instrukcijos išsiųstos į el. paštą.");
        } catch (EmployeeNotFoundInDbException e) {
            model.addAttribute("messageError", e.getMessage());
        }
        return "auth/rememberPassword";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(
            @RequestParam(value = "token", required = false) String token,
            Model model
    ) {
        try {
            model.addAttribute("pageHideForm", false);
            internalEmployeeService.verifyEmployeeAccountPasswordResetToken(token);
        } catch (TokenInvalidException e) {
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageError", "Nuoroda negalioja.");
        } catch (TokenExpiredException e) {
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageError", "Nuorodos galiojimo laikas baigėsi.");
        }
        model.addAttribute("token", token);
        model.addAttribute("resetPasswordDto", new ResetPasswordDto());
        return "auth/resetPassword";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            HttpServletRequest request,
            @ModelAttribute("resetPasswordDto") @Valid ResetPasswordDto resetPasswordDto,
            BindingResult bindingResult,
            Model model
    ) {
        String token = request.getParameter("token");

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHideForm", false);
            model.addAttribute("token", token);
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "auth/resetPassword";
        }

        try {
            EmployeeResetPasswordToken resetPasswordToken = internalEmployeeService.verifyEmployeeAccountPasswordResetToken(token);
            Employee employee = resetPasswordToken.getEmployee();
            internalEmployeeService.resetPasswordAndActivateEmployeeAccount(employee, resetPasswordDto, resetPasswordToken);
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageInfo", "Slaptažodis pakeistas.");
        } catch (TokenInvalidException e) {
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageError", "Nuoroda negalioja.");
        } catch (TokenExpiredException e) {
            model.addAttribute("pageHideForm", true);
            model.addAttribute("messageError", "Nuorodos galiojimo laikas baigėsi.");
        } catch (PasswordDontMatchException e) {
            model.addAttribute("pageHideForm", false);
            model.addAttribute("messageError", e.getMessage());
        }

        model.addAttribute("token", token);
        model.addAttribute("resetPasswordDto", resetPasswordDto);
        return "auth/resetPassword";
    }
}
