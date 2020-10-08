package lt.ivl.components.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.repository.CustomerRepository;
import lt.ivl.components.repository.CustomerResetPasswordTokenRepository;
import lt.ivl.components.repository.CustomerVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    CustomerVerificationTokenRepository tokenRepository;

    @Autowired
    CustomerResetPasswordTokenRepository passwordTokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer findCustomerAccountByEmail(String email) throws CustomerNotFoundInDBException {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundInDBException();
        }

        // vartotojas gali buti sukurtas Employee, o password empty, tada ismetame exception
        if (customer.get().getPassword().isEmpty()) {
            throw new CustomerNotFoundInDBException();
        }

        return customer.get();
    }

    public Customer findCustomerByEmail(String email) throws CustomerNotFoundInDBException {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundInDBException();
        }

        return customer.get();
    }
}
