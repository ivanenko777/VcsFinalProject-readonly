package lt.ivl.components.repository;

import lt.ivl.components.domain.CustomerVerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerVerificationTokenRepository extends CrudRepository<CustomerVerificationToken, Integer> {
    CustomerVerificationToken findByToken(String token);
}
