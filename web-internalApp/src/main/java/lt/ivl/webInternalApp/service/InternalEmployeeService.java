package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeResetPasswordToken;
import lt.ivl.components.exception.EmployeeNotFoundInDbException;
import lt.ivl.components.service.EmployeeService;
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
}
