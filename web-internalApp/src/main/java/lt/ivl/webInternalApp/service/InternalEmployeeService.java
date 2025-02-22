package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeResetPasswordToken;
import lt.ivl.components.exception.*;
import lt.ivl.components.service.EmployeeService;
import lt.ivl.webInternalApp.dto.EmployeeDto;
import lt.ivl.webInternalApp.dto.ResetPasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternalEmployeeService {
    @Autowired
    private EmployeeService componentEmployeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Employee> findAll() {
        return componentEmployeeService.findAll();
    }

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
        String newPassword = passwordEncoder.encode(password);
        componentEmployeeService.resetPasswordAndActivateEmployeeAccount(employee, newPassword, resetPasswordToken);
    }

    public Employee updateEmployeeAccount(Employee employee, EmployeeDto employeeDto) throws EmployeeAccountExistsInDatabaseException {
        String email = employee.getEmail();
        String newEmail = employeeDto.getEmail();
        if (!email.equals(newEmail)) {
            componentEmployeeService.verifyIsEmployeeAccountExists(newEmail);
        }

        employee = fillEmployeeFields(employee, employeeDto);
        return componentEmployeeService.saveEmployee(employee);
    }

    public Employee createEmployeeAccount(EmployeeDto employeeDto) throws EmployeeAccountExistsInDatabaseException {
        String email = employeeDto.getEmail();
        // throw exception if exists
        componentEmployeeService.verifyIsEmployeeAccountExists(email);

        Employee employee = new Employee();
        employee = fillEmployeeFields(employee, employeeDto);
        return componentEmployeeService.saveEmployee(employee);
    }

    private Employee fillEmployeeFields(Employee employee, EmployeeDto employeeDto) {
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPhone(employeeDto.getPhone());

        employee.getRoles().clear();
        employee.setRoles(employeeDto.getRoles());

        return employee;
    }
}
