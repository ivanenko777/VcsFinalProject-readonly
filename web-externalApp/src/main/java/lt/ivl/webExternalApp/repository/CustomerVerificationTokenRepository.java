package lt.ivl.webExternalApp.repository;

import lt.ivl.webExternalApp.domain.CustomerVerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerVerificationTokenRepository extends CrudRepository<CustomerVerificationToken, Integer> {
    CustomerVerificationToken findByToken(String token);
}
