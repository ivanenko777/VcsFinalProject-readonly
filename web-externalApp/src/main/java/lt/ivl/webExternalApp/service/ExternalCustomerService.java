package lt.ivl.webExternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.CustomerResetPasswordToken;
import lt.ivl.components.domain.CustomerVerificationToken;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.PasswordDontMatchException;
import lt.ivl.components.exception.TokenExpiredException;
import lt.ivl.components.exception.TokenInvalidException;
import lt.ivl.components.repository.CustomerRepository;
import lt.ivl.components.repository.CustomerResetPasswordTokenRepository;
import lt.ivl.components.repository.CustomerVerificationTokenRepository;
import lt.ivl.components.service.CustomerService;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.dto.ResetPasswordDto;
import lt.ivl.webExternalApp.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExternalCustomerService {
    @Autowired
    CustomerVerificationTokenRepository tokenRepository;

    @Autowired
    CustomerResetPasswordTokenRepository passwordTokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerService componentCustomerService;

    // register by Customer
    public Customer registerNewCustomerAccount(CustomerDto customerDto) throws UsernameExistsInDatabaseException, PasswordDontMatchException {
        String password = customerDto.getPassword();
        String passwordVerify = customerDto.getPasswordVerify();
        if (!password.equals(passwordVerify)) {
            throw new PasswordDontMatchException();
        }

        // TODO: A: jei email != null && pass != null throw UsernameExistsInDatabaseException();
        // TODO: jei email != null && pass == null
        //  B: tada atnaujinti vartotojo info + prideti slaptazodi
        //  C: else sukurti nauja vartotoja
        String email = customerDto.getEmail();
        Customer customer = null;
        try {
            customer = componentCustomerService.findCustomerByEmail(email);
            // A
            if(!customer.getPassword().isEmpty()) throw new UsernameExistsInDatabaseException();
            // B
            // customer.setEmail(customerDto.getEmail());
            customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
            customer.setFirstName(customerDto.getFirstName());
            customer.setLastName(customerDto.getLastName());
            customer.setPhone(customerDto.getPhone());
        } catch (CustomerNotFoundInDBException e) {
            // C
            customer = new Customer();
            customer.setEmail(customerDto.getEmail());
            customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
            customer.setFirstName(customerDto.getFirstName());
            customer.setLastName(customerDto.getLastName());
            customer.setPhone(customerDto.getPhone());
        }

        return componentCustomerService.saveCustomer(customer);
    }

    public void activateCustomerAccount(CustomerVerificationToken verificationToken) {
        Customer customer = verificationToken.getCustomer();
        customer.setActive(true);
        componentCustomerService.saveCustomer(customer);
        tokenRepository.delete(verificationToken);
    }

    public void resetCustomerAccountPassword(
            Customer customer,
            ResetPasswordDto passwordDto,
            CustomerResetPasswordToken resetPasswordToken
    ) throws PasswordDontMatchException {
        // password validation
        String password = passwordDto.getPassword();
        String passwordVerify = passwordDto.getPasswordVerify();
        if (!validateIsPasswordPass(password, passwordVerify)) {
            throw new PasswordDontMatchException();
        }

        // change password
        customer.setPassword(passwordEncoder.encode(password));
        componentCustomerService.saveCustomer(customer);

        // delete token
        passwordTokenRepository.delete(resetPasswordToken);
    }

    public void createVerificationTokenForCustomerAccount(Customer customer, String token) {
        CustomerVerificationToken myToken = new CustomerVerificationToken(token, customer);
        tokenRepository.save(myToken);
    }

    public CustomerVerificationToken generateNewVerificationTokenForCustomerAccount(String existingVerificationToken) {
        CustomerVerificationToken token = tokenRepository.findByToken(existingVerificationToken);
        token.updateToken(UUID.randomUUID().toString());
        return tokenRepository.save(token);
    }

    public Customer findCustomerAccountByEmail(String email) throws CustomerNotFoundInDBException {
        return componentCustomerService.findCustomerAccountByEmail(email);
    }

    public CustomerResetPasswordToken createPasswordResetTokenForCustomerAccount(Customer customer) {
        String token = UUID.randomUUID().toString();
        CustomerResetPasswordToken myToken = new CustomerResetPasswordToken(token, customer);
        passwordTokenRepository.save(myToken);
        return myToken;
    }


    private boolean validateIsPasswordPass(String password, String passwordVerify) {
        return password.equals(passwordVerify);
    }

    private boolean validateIsTokenExpired(Timestamp tokenExpiryDate) {
        Calendar calendar = Calendar.getInstance();
        return (tokenExpiryDate.getTime() - calendar.getTime().getTime()) <= 0;
    }

    public CustomerVerificationToken verifyCustomerAccountVerificationToken(String token) throws TokenInvalidException, TokenExpiredException {
        // jei tokeno nera ismetame klaida
        if (token == null) throw new TokenInvalidException();

        // jei tokenas nerastas ismetame klaida
        CustomerVerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) throw new TokenInvalidException();

        // jei tokenas negalioja, ismetame klaida
        Timestamp verificationTokenExpiryDate = verificationToken.getExpiryDate();
        if (validateIsTokenExpired(verificationTokenExpiryDate)) {
            throw new TokenExpiredException();
        }

        return verificationToken;
    }


    public CustomerResetPasswordToken verifyCustomerAccountPasswordResetToken(String token) throws TokenInvalidException, TokenExpiredException {
        // jei tokeno nera ismetame klaida
        if (token == null) throw new TokenInvalidException();

        // jei tokenas nerastas ismetame klaida
        CustomerResetPasswordToken tokenFromDb = passwordTokenRepository.findByToken(token);
        if (tokenFromDb == null) throw new TokenInvalidException();

        // jei tokenas negalioja ismetame klaida
        Timestamp passwordTokenExpiryDate = tokenFromDb.getExpiryDate();
        if (validateIsTokenExpired(passwordTokenExpiryDate)) {
            throw new TokenExpiredException();
        }

        return tokenFromDb;
    }
}
