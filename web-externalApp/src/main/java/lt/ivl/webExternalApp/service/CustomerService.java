package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.exception.UsernameExistsInDatabaseException;
import lt.ivl.webExternalApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public void createFromRegistrationForm(Customer customer) throws UsernameExistsInDatabaseException {
        Customer customerFromDb = customerRepository.findByEmail(customer.getEmail());
        if (customerFromDb != null) throw new UsernameExistsInDatabaseException("User exists in DB");

        customer.setActive(true);
        customerRepository.save(customer);
    }
}
