package lt.ivl.webExternalApp.repository;

import lt.ivl.webExternalApp.domain.CustomerResetPasswordToken;
import lt.ivl.webExternalApp.domain.CustomerVerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerResetPasswordTokenRepository extends CrudRepository<CustomerResetPasswordToken, Integer> {
    CustomerResetPasswordToken findByToken(String token);
}
