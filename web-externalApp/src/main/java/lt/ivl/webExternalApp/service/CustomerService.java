package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.CustomerResetPasswordToken;
import lt.ivl.webExternalApp.domain.CustomerVerificationToken;
import lt.ivl.webExternalApp.dto.CustomerDto;
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

    @Autowired
    private MailSender mailSender;

    public void registerNewCustomerAccount(CustomerDto customerDto) throws UsernameExistsInDatabaseException, PasswordDontMatchException {
        String password = customerDto.getPassword();
        String passwordVerify = customerDto.getPasswordVerify();
        verifyPasswordPass(password, passwordVerify);

        if (emailExist(customerDto.getEmail())) throw new UsernameExistsInDatabaseException("User exists in DB");

        Customer customer = new Customer();
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhone(customerDto.getPhone());
        saveCustomer(customer);

        confirmNewCustomerRegistration(customer);
    }

    public void confirmNewCustomerRegistration(Customer customer) {
        String token = UUID.randomUUID().toString();
        createVerificationTokenForCustomer(customer, token);
        mailSender.sendVerificationEmailToCustomer(customer, token);
    }

    private void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void createVerificationTokenForCustomer(Customer customer, String token) {
        CustomerVerificationToken myToken = new CustomerVerificationToken(token, customer);
        tokenRepository.save(myToken);
    }

    public CustomerVerificationToken generateNewVerificationTokenForCustomer(String existingVerificationToken) {
        CustomerVerificationToken token = tokenRepository.findByToken(existingVerificationToken);
        token.updateToken(UUID.randomUUID().toString());
        return tokenRepository.save(token);
    }

    public void activateByVerificationToken(String token) throws TokenInvalidException, TokenExpiredException {
        // jei tokenas nerastas ismetame klaida
        CustomerVerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) throw new TokenInvalidException("Patvirtinimo tokenas nerastas.");

        // jei tokenas negalioja, issiunciame nauja ir ismetame klaida
        Customer customer = verificationToken.getCustomer();
        Calendar calendar = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0) {
            String newToken = generateNewVerificationTokenForCustomer(token).getToken();
            mailSender.sendVerificationEmailToCustomer(customer, newToken);
            throw new TokenExpiredException("Patvirtinimo tokenas negalioja. Naujas išsiųstas į el. paštą.");
        }

        // jei tokenas galioja, aktyvuojame vartotoja ir issiunciame email
        customer.setActive(true);
        customerRepository.save(customer);
        tokenRepository.delete(verificationToken);
        mailSender.sendActivatedEmailToCustomer(customer);
    }

    private boolean emailExist(String email) {
        return customerRepository.findByEmail(email) != null;
    }

    private void verifyPasswordPass(String password, String passwordVerify) throws PasswordDontMatchException {
        if (!password.equals(passwordVerify)) {
            throw new PasswordDontMatchException("Passwords are not match!");
        }
    }

    public Customer findCustomerByEmail(String email) throws CustomerNotFoundInDBException {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) throw new CustomerNotFoundInDBException("El. pašto adresas nerastas");

        return customer;
    }

    public CustomerResetPasswordToken createPasswordResetTokenForCustomer(Customer customer) {
        String token = UUID.randomUUID().toString();
        CustomerResetPasswordToken myToken = new CustomerResetPasswordToken(token, customer);
        passwordTokenRepository.save(myToken);
        return myToken;
    }
}
