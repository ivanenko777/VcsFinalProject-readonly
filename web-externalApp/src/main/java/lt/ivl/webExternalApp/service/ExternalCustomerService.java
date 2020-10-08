package lt.ivl.webExternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.CustomerResetPasswordToken;
import lt.ivl.components.domain.CustomerVerificationToken;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.PasswordDontMatchException;
import lt.ivl.components.exception.TokenExpiredException;
import lt.ivl.components.exception.TokenInvalidException;
import lt.ivl.components.service.CustomerService;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.dto.ResetPasswordDto;
import lt.ivl.webExternalApp.exception.UsernameExistsInDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

@Service
public class ExternalCustomerService {
//    @Autowired
//    CustomerVerificationTokenRepository tokenRepository;
//
//    @Autowired
//    CustomerResetPasswordTokenRepository passwordTokenRepository;

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
            if (!customer.getPassword().isEmpty()) throw new UsernameExistsInDatabaseException();
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
        componentCustomerService.activateCustomerAccount(verificationToken);
    }

    public void resetCustomerAccountPassword(
            Customer customer,
            ResetPasswordDto passwordDto,
            CustomerResetPasswordToken resetPasswordToken
    ) throws PasswordDontMatchException {
        // password validation
        String password = passwordDto.getPassword();
        String passwordVerify = passwordDto.getPasswordVerify();
        if (!password.equals(passwordVerify)) {
            throw new PasswordDontMatchException();
        }

        String newPassword = passwordEncoder.encode(password);
        componentCustomerService.resetCustomerAccountPassword(customer, newPassword, resetPasswordToken);
    }

    public CustomerVerificationToken createVerificationTokenForCustomerAccount(Customer customer) {
        return componentCustomerService.createVerificationTokenForCustomerAccount(customer);
    }

    public CustomerVerificationToken generateNewVerificationTokenForCustomerAccount(String existingVerificationToken) {
        return componentCustomerService.generateNewVerificationTokenForCustomerAccount(existingVerificationToken);
    }

    public Customer findCustomerAccountByEmail(String email) throws CustomerNotFoundInDBException {
        return componentCustomerService.findCustomerAccountByEmail(email);
    }

    public CustomerResetPasswordToken createPasswordResetTokenForCustomerAccount(Customer customer) {
        return componentCustomerService.createPasswordResetTokenForCustomerAccount(customer);
    }

    public CustomerVerificationToken verifyCustomerAccountVerificationToken(String token) throws TokenInvalidException, TokenExpiredException {
        return componentCustomerService.verifyCustomerAccountVerificationToken(token);
    }

    public CustomerResetPasswordToken verifyCustomerAccountPasswordResetToken(String token) throws TokenInvalidException, TokenExpiredException {
        return componentCustomerService.verifyCustomerAccountPasswordResetToken(token);
    }
}
