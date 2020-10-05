package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeResetPasswordToken;
import lt.ivl.components.exception.EmployeeNotFoundInDbException;
import lt.ivl.components.exception.PasswordDontMatchException;
import lt.ivl.components.exception.TokenExpiredException;
import lt.ivl.components.exception.TokenInvalidException;
import lt.ivl.components.service.EmployeeService;
import lt.ivl.webInternalApp.dto.ResetPasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InternalEmployeeService {
    @Autowired
    EmployeeService componentEmployeeService;

    public Employee findEmployeeAccountByEmail(String email) throws EmployeeNotFoundInDbException {
        return componentEmployeeService.findEmployeeByEmail(email);
    }

    public EmployeeResetPasswordToken createPasswordResetTokenForEmployeeAccount(Employee employee) {
        return componentEmployeeService.createPasswordResetTokenForEmployeeAccount(employee);
    }

    public EmployeeResetPasswordToken verifyEmployeeAccountPasswordResetToken(String token) throws TokenInvalidException, TokenExpiredException {
        return componentEmployeeService.verifyEmployeeAccountPasswordResetToken(token);
    }

    public void resetPasswordAndActivateEmployeeAccount(
            Employee employee,
            ResetPasswordDto resetPasswordDto,
            EmployeeResetPasswordToken resetPasswordToken
    ) throws PasswordDontMatchException {
        // validate password
        String password = resetPasswordDto.getPassword();
        String passwordVerify = resetPasswordDto.getPasswordVerify();
        if (!password.equals(passwordVerify)) {
            throw new PasswordDontMatchException();
        }

        // change password
        // TODO: encrypt password
        String newPassword = password;
        componentEmployeeService.resetPasswordAndActivateEmployeeAccount(employee, newPassword, resetPasswordToken);
    }
}
