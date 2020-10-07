package lt.ivl.components.service;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeResetPasswordToken;
import lt.ivl.components.exception.EmployeeNotFoundInDbException;
import lt.ivl.components.exception.TokenExpiredException;
import lt.ivl.components.exception.TokenInvalidException;
import lt.ivl.components.repository.EmployeeRepository;
import lt.ivl.components.repository.EmployeeResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
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

    public List<Employee> findAll() {
        return (List<Employee>) employeeRepository.findAll();
    }

    @Transactional
    public Employee findEmployeeByEmail(String email) throws EmployeeNotFoundInDbException {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isEmpty()) {
            throw new EmployeeNotFoundInDbException();
        }
        // FIX: org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role:
        // lt.ivl.components.domain.Employee.roles, could not initialize proxy - no Session
        employee.get().getRoles().size();

        return employee.get();
    }

    public EmployeeResetPasswordToken createPasswordResetTokenForEmployeeAccount(Employee employee) {
        String token = UUID.randomUUID().toString();
        EmployeeResetPasswordToken myToken = new EmployeeResetPasswordToken(token, employee);
        employeePasswordTokenRepository.save(myToken);
        return myToken;
    }

    public EmployeeResetPasswordToken verifyEmployeeAccountPasswordResetToken(String token) throws TokenInvalidException, TokenExpiredException {
        // jei tokeno nera ismetame klaida
        if (token == null) throw new TokenInvalidException();

        // jei tokenas nerastas ismetame klaida
        Optional<EmployeeResetPasswordToken> tokenFromDb = employeePasswordTokenRepository.findByToken(token);
        if (tokenFromDb.isEmpty()) throw new TokenInvalidException();

        // jei tokenas negalioja ismetame klaida
        Timestamp passwordTokenExpiryDate = tokenFromDb.get().getExpiryDate();
        Calendar calendar = Calendar.getInstance();
        if ((passwordTokenExpiryDate.getTime() - calendar.getTime().getTime()) <= 0) {
            throw new TokenExpiredException();
        }

        return tokenFromDb.get();
    }

    public void resetPasswordAndActivateEmployeeAccount(
            Employee employee,
            String newPassword,
            EmployeeResetPasswordToken resetPasswordToken
    ) {
        // change password and activate account
        employee.setPassword(newPassword);
        employee.setActive(true);
        saveEmployee(employee);

        // delete token
        employeePasswordTokenRepository.delete(resetPasswordToken);
    }
}
