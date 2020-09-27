package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.exception.PasswordDontMatchException;
import lt.ivl.webExternalApp.exception.UsernameExistsInDatabaseException;
import lt.ivl.webExternalApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Customer registerNewCustomerAccount(CustomerDto customerDto) throws UsernameExistsInDatabaseException, PasswordDontMatchException {
        String password = customerDto.getPassword();
        String passwordVerify = customerDto.getPasswordVerify();
        if (!verifyPasswordPass(password, passwordVerify)) throw new PasswordDontMatchException("Passwords are not match!");
        if (emailExist(customerDto.getEmail())) throw new UsernameExistsInDatabaseException("User exists in DB");

        Customer customer = new Customer();
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhone(customerDto.getPhone());
        customer.setActive(true);
        saveCustomer(customer);

        return customer;
    }

    private void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }


    private boolean emailExist(String email) {
        return customerRepository.findByEmail(email) != null;
    }

    private boolean verifyPasswordPass(String password, String passwordVerify) {
        return password.equals(passwordVerify);
    }
}
