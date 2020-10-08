package lt.ivl.components.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.CustomerResetPasswordToken;
import lt.ivl.components.domain.CustomerVerificationToken;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.repository.CustomerRepository;
import lt.ivl.components.repository.CustomerResetPasswordTokenRepository;
import lt.ivl.components.repository.CustomerVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public void resetCustomerAccountPassword(Customer customer, String newPassword, CustomerResetPasswordToken resetPasswordToken) {
        // change password
        customer.setPassword(newPassword);
        saveCustomer(customer);

        // delete token
        passwordTokenRepository.delete(resetPasswordToken);
    }

    public void activateCustomerAccount(CustomerVerificationToken verificationToken) {
        Customer customer = verificationToken.getCustomer();
        customer.setActive(true);
        saveCustomer(customer);
        tokenRepository.delete(verificationToken);
    }

    public CustomerVerificationToken createVerificationTokenForCustomerAccount(Customer customer) {
        String token = UUID.randomUUID().toString();
        CustomerVerificationToken myToken = new CustomerVerificationToken(token, customer);
        return tokenRepository.save(myToken);
    }
}
