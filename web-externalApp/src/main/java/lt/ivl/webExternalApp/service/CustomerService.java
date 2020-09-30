package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.CustomerResetPasswordToken;
import lt.ivl.webExternalApp.domain.CustomerVerificationToken;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.dto.ResetPasswordDto;
import lt.ivl.webExternalApp.exception.*;
import lt.ivl.webExternalApp.repository.CustomerRepository;
import lt.ivl.webExternalApp.repository.CustomerResetPasswordTokenRepository;
import lt.ivl.webExternalApp.repository.CustomerVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    CustomerVerificationTokenRepository tokenRepository;

    @Autowired
    CustomerResetPasswordTokenRepository passwordTokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Customer registerNewCustomerAccount(CustomerDto customerDto) throws UsernameExistsInDatabaseException, PasswordDontMatchException {
        String password = customerDto.getPassword();
        String passwordVerify = customerDto.getPasswordVerify();
        if (!validateIsPasswordPass(password, passwordVerify)) {
            throw new PasswordDontMatchException("Slaptažodiai nesutampa!");
//            throw new PasswordDontMatchException("Passwords are not match!");
        }

        if (validateIsCustomerAccountExist(customerDto.getEmail())) {
            throw new UsernameExistsInDatabaseException("Vartotojo paskyra yra DB!");
//            throw new UsernameExistsInDatabaseException("User exists in DB");
        }

        Customer customer = new Customer();
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhone(customerDto.getPhone());
        saveCustomer(customer);

        return customer;
    }
    
    private void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void createVerificationTokenForNewCustomerAccount(Customer customer, String token) {
        CustomerVerificationToken myToken = new CustomerVerificationToken(token, customer);
        tokenRepository.save(myToken);
    }

    public CustomerVerificationToken generateNewVerificationTokenForCustomer(String existingVerificationToken) {
        CustomerVerificationToken token = tokenRepository.findByToken(existingVerificationToken);
        token.updateToken(UUID.randomUUID().toString());
        return tokenRepository.save(token);
    }

    public void activateCustomerAccount(CustomerVerificationToken verificationToken) {
        Customer customer = verificationToken.getCustomer();
        customer.setActive(true);
        customerRepository.save(customer);
        tokenRepository.delete(verificationToken);
    }

    public CustomerVerificationToken validateByVerificationToken(String token) throws TokenInvalidException, TokenExpiredException {
        // jei tokeno nera ismetame klaida
        if (token == null) throw new TokenInvalidException("Tokenas nerastas.");

        // jei tokenas nerastas ismetame klaida
        CustomerVerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) throw new TokenInvalidException("Patvirtinimo tokenas nerastas.");

        // jei tokenas negalioja, issiunciame nauja ir ismetame klaida
        Calendar calendar = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0) {
            throw new TokenExpiredException("Patvirtinimo tokenas negalioja.");
        }

        return verificationToken;
    }

    private boolean validateIsCustomerAccountExist(String email) {
        // TODO: email != null && password != null
        // nes vartotojas gali buti sukurtas Employee
        return customerRepository.findByEmail(email) != null;
    }

    private boolean validateIsPasswordPass(String password, String passwordVerify) {
        return password.equals(passwordVerify);
    }

    public Customer findCustomerAccountByEmail(String email) throws CustomerNotFoundInDBException {
        if (!validateIsCustomerAccountExist(email)) {
            throw new CustomerNotFoundInDBException("Vartotojo paskyra nerasta");
        }
        return customerRepository.findByEmail(email);
    }

    public CustomerResetPasswordToken createPasswordResetTokenForCustomer(Customer customer) {
        String token = UUID.randomUUID().toString();
        CustomerResetPasswordToken myToken = new CustomerResetPasswordToken(token, customer);
        passwordTokenRepository.save(myToken);
        return myToken;
    }

    public CustomerResetPasswordToken verifyCustomerAccountPasswordResetToken(String token) throws TokenInvalidException, TokenExpiredException {
        // jei tokeno nera ismetame klaida
        if (token == null) throw new TokenInvalidException("Tokenas nerastas.");

        // jei tokenas nerastas ismetame klaida
        CustomerResetPasswordToken tokenFromDb = passwordTokenRepository.findByToken(token);
        if (tokenFromDb == null) throw new TokenInvalidException("Tokenas nerastas.");

        // jei tokenas negalioja ismetame klaida
        Calendar calendar = Calendar.getInstance();
        if ((tokenFromDb.getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0) {
            throw new TokenExpiredException("Tokenas negalioja.");
        }

        return tokenFromDb;
    }

    public void resetCustomerPassword(
            Customer customer,
            ResetPasswordDto passwordDto,
            CustomerResetPasswordToken resetPasswordToken
    ) throws PasswordDontMatchException {
        // password validation
        String password = passwordDto.getPassword();
        String passwordVerify = passwordDto.getPasswordVerify();
        if (!validateIsPasswordPass(password, passwordVerify)) {
            throw new PasswordDontMatchException("Slaptažodiai nesutampa!");
//            throw new PasswordDontMatchException("Passwords are not match!");
        }

        // change password
        customer.setPassword(passwordEncoder.encode(password));
        customerRepository.save(customer);

        // delete token
        passwordTokenRepository.delete(resetPasswordToken);
    }
}
