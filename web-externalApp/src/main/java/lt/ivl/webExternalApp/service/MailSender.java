package lt.ivl.webExternalApp.service;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.Repair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment environment;

    public void sendAccountVerificationEmailToCustomer(Customer customer, String token) {
        final SimpleMailMessage email = constructCustomerVerificationEmail(customer, token);
        mailSender.send(email);
    }

    public void sendAccountActivatedEmailToCustomer(Customer customer) {
        final SimpleMailMessage email = constructCustomerActivatatedEmail(customer);
        mailSender.send(email);
    }

    public void sendResetPasswordEmailToCustomer(Customer customer, String token) {
        final SimpleMailMessage email = constructCustomerResetPasswordEmail(customer, token);
        mailSender.send(email);
    }

    public void sendRepairRequestToCustomer(Customer customer, Repair repair) {
        final SimpleMailMessage email = constructCustomerRepairRequestEmail(customer, repair);
        mailSender.send(email);
    }

    private SimpleMailMessage constructCustomerVerificationEmail(Customer customer, String token) {
        final String recipientAddress = customer.getEmail();
        final String subject = "Registracijos patvirtinimas";
        final String appUrl = environment.getProperty("app.url");
        final String confirmationUrl = appUrl + "activation?token=" + token;
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Jūs sėkmingai užsiregistravote. Paspauskite žemiau esančią nuorodą, kad patvirtintumėte registraciją.";

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(environment.getProperty("support.email"));
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message1 + " \r\n" + message2 + " \r\n" + confirmationUrl);
        return email;
    }

    private SimpleMailMessage constructCustomerActivatatedEmail(Customer customer) {
        final String recipientAddress = customer.getEmail();
        final String subject = "Registracija patvirtinta";
        final String appUrl = environment.getProperty("app.url");
        final String loginUrl = appUrl + "login";
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Jūsų registracija patvirtinta. Paspauskite žemiau esančią nuorodą, kad prisijungtumėte.";

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(environment.getProperty("support.email"));
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message1 + " \r\n" + message2 + " \r\n" + loginUrl);
        return email;
    }

    private SimpleMailMessage constructCustomerResetPasswordEmail(Customer customer, String token) {
        final String recipientAddress = customer.getEmail();
        final String subject = "Slaptažodžio pakeitimas";
        final String appUrl = environment.getProperty("app.url");
        final String resetPasswordUrl = appUrl + "reset-password?token=" + token;
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Paspauskite žemiau esančią nuorodą, kad pakeisti slaptažodį.";

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(environment.getProperty("support.email"));
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message1 + " \r\n" + message2 + " \r\n" + resetPasswordUrl);
        return email;
    }

    private SimpleMailMessage constructCustomerRepairRequestEmail(Customer customer, Repair repair) {
        // remonto paraiska, STATUS -> PENDING
        final int repairId = repair.getId();

        final String recipientAddress = customer.getEmail();
        final String subject = "Užsakymo paraiška #" + repairId;
        final String appUrl = environment.getProperty("app.url");
        final String repairViewUrl = appUrl + "repair/" + repairId + "/view";
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Remonto paraiška užregistruota. Sekite info žemiau esančioje nuorodoje.";

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(environment.getProperty("support.email"));
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message1 + " \r\n" + message2 + " \r\n" + repairViewUrl);
        return email;
    }
}
