package lt.ivl.components.service;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeResetPasswordToken;
import lt.ivl.components.exception.EmployeeNotFoundInDbException;
import lt.ivl.components.repository.EmployeeRepository;
import lt.ivl.components.repository.EmployeeResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeResetPasswordTokenRepository employeePasswordTokenRepository;

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee findEmployeeByEmail(String email) throws EmployeeNotFoundInDbException {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isEmpty()) {
            throw new EmployeeNotFoundInDbException();
        }
        return employee.get();
    }

    public EmployeeResetPasswordToken createPasswordResetTokenForEmployeeAccount(Employee employee) {
        String token = UUID.randomUUID().toString();
        EmployeeResetPasswordToken myToken = new EmployeeResetPasswordToken(token, employee);
        employeePasswordTokenRepository.save(myToken);
        return myToken;
    }
}
