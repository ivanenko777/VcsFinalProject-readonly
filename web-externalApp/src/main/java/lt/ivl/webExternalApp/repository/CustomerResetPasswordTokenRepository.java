package lt.ivl.webExternalApp.repository;

import lt.ivl.webExternalApp.domain.CustomerResetPasswordToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerResetPasswordTokenRepository extends CrudRepository<CustomerResetPasswordToken, Integer> {
}
