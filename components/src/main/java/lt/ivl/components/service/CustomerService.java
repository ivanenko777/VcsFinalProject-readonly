package lt.ivl.components.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.CustomerResetPasswordToken;
import lt.ivl.components.domain.CustomerVerificationToken;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.TokenExpiredException;
import lt.ivl.components.exception.TokenInvalidException;
import lt.ivl.components.repository.CustomerRepository;
import lt.ivl.components.repository.CustomerResetPasswordTokenRepository;
import lt.ivl.components.repository.CustomerVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    private CustomerVerificationTokenRepository tokenRepository;

    @Autowired
    private CustomerResetPasswordTokenRepository passwordTokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> findAll() {
        return (List<Customer>) customerRepository.findAll();
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

    public CustomerVerificationToken generateNewVerificationTokenForCustomerAccount(String existingVerificationToken) {
        Optional<CustomerVerificationToken> verificationToken = tokenRepository.findByToken(existingVerificationToken);
        CustomerVerificationToken verificationTokenFromDb = verificationToken.get();
        String newToken = UUID.randomUUID().toString();
        verificationTokenFromDb.updateToken(newToken);
        return tokenRepository.save(verificationTokenFromDb);
    }

    public CustomerResetPasswordToken createPasswordResetTokenForCustomerAccount(Customer customer) {
        String token = UUID.randomUUID().toString();
        CustomerResetPasswordToken newToken = new CustomerResetPasswordToken(token, customer);
        return passwordTokenRepository.save(newToken);
    }

    public CustomerVerificationToken verifyCustomerAccountVerificationToken(String token) throws TokenInvalidException, TokenExpiredException {
        // jei tokeno nera ismetame klaida
        if (token == null) throw new TokenInvalidException();

        // jei tokenas nerastas ismetame klaida
        Optional<CustomerVerificationToken> verificationToken = tokenRepository.findByToken(token);
        if (verificationToken.isEmpty()) throw new TokenInvalidException();

        // jei tokenas negalioja, ismetame klaida
        CustomerVerificationToken verificationTokenFromDb = verificationToken.get();
        Timestamp verificationTokenExpiryDate = verificationTokenFromDb.getExpiryDate();
        if (validateIsTokenExpired(verificationTokenExpiryDate)) {
            throw new TokenExpiredException();
        }

        return verificationTokenFromDb;
    }

    public CustomerResetPasswordToken verifyCustomerAccountPasswordResetToken(String token) throws TokenInvalidException, TokenExpiredException {
        // jei tokeno nera ismetame klaida
        if (token == null) throw new TokenInvalidException();

        // jei tokenas nerastas ismetame klaida
        Optional<CustomerResetPasswordToken> tokenFromDb = passwordTokenRepository.findByToken(token);
        if (tokenFromDb.isEmpty()) throw new TokenInvalidException();

        // jei tokenas negalioja ismetame klaida
        CustomerResetPasswordToken resetPasswordToken = tokenFromDb.get();
        Timestamp passwordTokenExpiryDate = resetPasswordToken.getExpiryDate();
        if (validateIsTokenExpired(passwordTokenExpiryDate)) {
            throw new TokenExpiredException();
        }

        return resetPasswordToken;
    }

    private boolean validateIsTokenExpired(Timestamp tokenExpiryDate) {
        Calendar calendar = Calendar.getInstance();
        return (tokenExpiryDate.getTime() - calendar.getTime().getTime()) <= 0;
    }
}
