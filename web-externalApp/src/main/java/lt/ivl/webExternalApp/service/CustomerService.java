package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.CustomerVerificationToken;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.exception.PasswordDontMatchException;
import lt.ivl.webExternalApp.exception.TokenExpiredException;
import lt.ivl.webExternalApp.exception.TokenInvalidException;
import lt.ivl.webExternalApp.exception.UsernameExistsInDatabaseException;
import lt.ivl.webExternalApp.repository.CustomerRepository;
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
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailSender mailSender;

    public void registerNewCustomerAccount(CustomerDto customerDto) throws UsernameExistsInDatabaseException, PasswordDontMatchException {
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
        saveCustomer(customer);

        confirmNewCustomerRegistration(customer);
    }
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
            throw new TokenExpiredException("Patvirtinimo tokenas negalioja. Išsiųstas naujas į el. paštą");
        }

        // jei tokenas galioja, aktyvuojame vartotoja, istriname tokena
        customer.setActive(true);
        customerRepository.save(customer);
        tokenRepository.delete(verificationToken);
        mailSender.sendActivatedEmailToCustomer(customer);
    }

    private boolean emailExist(String email) {
        return customerRepository.findByEmail(email) != null;
    }

    private boolean verifyPasswordPass(String password, String passwordVerify) {
        return password.equals(passwordVerify);
    }
}
