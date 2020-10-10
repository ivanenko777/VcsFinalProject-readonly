package lt.ivl.webExternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment environment;

    @Async
    public void sendAccountVerificationEmailToCustomer(Customer customer, String token) {
        final SimpleMailMessage email = constructCustomerVerificationEmail(customer, token);
        mailSender.send(email);
    }

    @Async
    public void sendAccountActivatedEmailToCustomer(Customer customer) {
        final SimpleMailMessage email = constructCustomerActivatatedEmail(customer);
        mailSender.send(email);
    }

    @Async
    public void sendResetPasswordEmailToCustomer(Customer customer, String token) {
        final SimpleMailMessage email = constructCustomerResetPasswordEmail(customer, token);
        mailSender.send(email);
    }

    @Async
    public void sendRepairRequestToCustomer(Customer customer, Repair repair) {
        final SimpleMailMessage email = constructCustomerRepairRequestEmail(customer, repair);
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmail(String recipientAddress, String subject, String message) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(environment.getProperty("support.email"));
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }

    private SimpleMailMessage constructCustomerVerificationEmail(Customer customer, String token) {
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Jūs sėkmingai užsiregistravote. Paspauskite žemiau esančią nuorodą, kad patvirtintumėte registraciją.";
        final String appUrl = environment.getProperty("app.url");
        final String confirmationUrl = appUrl + "activation?token=" + token;

        final String recipientAddress = customer.getEmail();
        final String subject = "Registracijos patvirtinimas";
        final String message = message1 + " \r\n" + message2 + " \r\n" + confirmationUrl;
        return constructEmail(recipientAddress, subject, message);
    }

    private SimpleMailMessage constructCustomerActivatatedEmail(Customer customer) {
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Jūsų registracija patvirtinta. Paspauskite žemiau esančią nuorodą, kad prisijungtumėte.";
        final String appUrl = environment.getProperty("app.url");
        final String loginUrl = appUrl + "login";

        final String recipientAddress = customer.getEmail();
        final String subject = "Registracija patvirtinta";
        final String message = message1 + " \r\n" + message2 + " \r\n" + loginUrl;
        return constructEmail(recipientAddress, subject, message);
    }

    private SimpleMailMessage constructCustomerResetPasswordEmail(Customer customer, String token) {
        final String appUrl = environment.getProperty("app.url");
        final String resetPasswordUrl = appUrl + "reset-password?token=" + token;
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Paspauskite žemiau esančią nuorodą, kad pakeisti slaptažodį.";

        final String recipientAddress = customer.getEmail();
        final String subject = "Slaptažodžio pakeitimas";
        final String message = message1 + " \r\n" + message2 + " \r\n" + resetPasswordUrl;
        return constructEmail(recipientAddress, subject, message);
    }

    private SimpleMailMessage constructCustomerRepairRequestEmail(Customer customer, Repair repair) {
        // remonto paraiska, STATUS -> PENDING
        final int repairId = repair.getId();

        final String appUrl = environment.getProperty("app.url");
        final String repairViewUrl = appUrl + "repair/" + repairId + "/view";
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Remonto paraiška užregistruota. Sekite info žemiau esančioje nuorodoje.";

        final String recipientAddress = customer.getEmail();
        final String subject = "Užsakymo paraiška #" + repairId;
        final String message = message1 + " \r\n" + message2 + " \r\n" + repairViewUrl;
        return constructEmail(recipientAddress, subject, message);
    }
}
