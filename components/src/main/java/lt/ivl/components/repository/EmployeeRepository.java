package lt.ivl.components.repository;

import lt.ivl.components.domain.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    Optional<Employee> findByEmail(String email);
}
