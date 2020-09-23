package lt.ivl.webExternalApp.repository;

import lt.ivl.webExternalApp.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {
    Customer findByEmail(String email);
}
