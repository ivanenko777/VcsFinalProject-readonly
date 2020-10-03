package lt.ivl.components.repository;

import lt.ivl.components.domain.CustomerResetPasswordToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerResetPasswordTokenRepository extends CrudRepository<CustomerResetPasswordToken, Integer> {
    CustomerResetPasswordToken findByToken(String token);
}
