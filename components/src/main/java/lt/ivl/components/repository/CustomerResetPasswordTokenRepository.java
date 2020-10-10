package lt.ivl.components.repository;

import lt.ivl.components.domain.CustomerResetPasswordToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerResetPasswordTokenRepository extends CrudRepository<CustomerResetPasswordToken, Integer> {
    Optional<CustomerResetPasswordToken> findByToken(String token);
}
