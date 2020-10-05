package lt.ivl.components.service;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.exception.EmployeeNotFoundInDbException;
import lt.ivl.components.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    public Employee findEmployeeByEmail(String email) throws EmployeeNotFoundInDbException {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isEmpty()) {
            throw new EmployeeNotFoundInDbException();
        }
        return employee.get();
    }
}
