package lt.ivl.webExternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.email.Email;
import lt.ivl.components.email.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplate mailTemplate;

    @Value("${app.url}")
    private String appUrl;

    @Value(("${support.email}"))
    private String emailFrom;

    private SimpleMailMessage constructEmail(Email template) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(this.emailFrom);
        email.setTo(template.getTo());
        email.setSubject(template.getSubject());
        email.setText(template.getMessage());
        return email;
    }

    @Async
    public void sendAccountVerificationEmailToCustomer(Customer customer, String token) {
        Email template = mailTemplate.customerAccountVerificationEmailTemplate(customer, token, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendAccountActivatedEmailToCustomer(Customer customer) {
        Email template = mailTemplate.customerAccountActivatedEmailTemplate(customer, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendResetPasswordEmailToCustomer(Customer customer, String token) {
        Email template = mailTemplate.customerAccountResetPasswordEmailTemplate(customer, token, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendRepairRequestToCustomer(Customer customer, Repair repair) {
        Email template = mailTemplate.customerRepairRequestEmailTemplate(customer, repair, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }
}
