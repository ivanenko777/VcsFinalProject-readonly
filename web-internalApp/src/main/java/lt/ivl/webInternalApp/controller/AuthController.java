package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeResetPasswordToken;
import lt.ivl.components.exception.EmployeeNotFoundInDbException;
import lt.ivl.webInternalApp.service.InternalEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
