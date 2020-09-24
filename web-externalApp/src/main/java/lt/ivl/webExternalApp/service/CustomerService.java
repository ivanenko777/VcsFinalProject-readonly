package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.exception.PasswordDontMatchException;
import lt.ivl.webExternalApp.exception.UsernameExistsInDatabaseException;
import lt.ivl.webExternalApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public void createFromRegistrationForm(CustomerDto customerDto) throws UsernameExistsInDatabaseException, PasswordDontMatchException {
        String customerPassword = customerDto.getPassword();
        String customerPasswordVerify = customerDto.getPasswordVerify();
        if (!customerPassword.equals(customerPasswordVerify)) throw new PasswordDontMatchException("Passwords are not match!");

        if (emailExist(customerDto.getEmail())) throw new UsernameExistsInDatabaseException("User exists in DB");

        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setActive(true);
        customerRepository.save(customer);
    }

    private boolean emailExist(String email) {
        return customerRepository.findByEmail(email) != null;
    }
}
