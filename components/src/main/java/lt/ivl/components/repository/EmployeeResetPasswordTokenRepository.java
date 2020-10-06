package lt.ivl.components.repository;

import lt.ivl.components.domain.EmployeeResetPasswordToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeResetPasswordTokenRepository extends CrudRepository<EmployeeResetPasswordToken, Integer> {
    Optional<EmployeeResetPasswordToken> findByToken(String token);
}
