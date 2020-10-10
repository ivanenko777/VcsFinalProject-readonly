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
    private EmailTemplate emailTemplate;

    @Value("${app.url}")
    private String appUrl;

    @Value(("${support.email}"))
    private String emailFrom;

    private SimpleMailMessage constructEmail(Email template) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(template.getFrom());
        email.setTo(template.getTo());
        email.setSubject(template.getSubject());
        email.setText(template.getMessage());
        return email;
    }

    @Async
    public void sendAccountVerificationEmailToCustomer(Customer customer, String token) {
        Email template = emailTemplate.customerAccountVerificationEmailTemplate(customer, token, emailFrom, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendAccountActivatedEmailToCustomer(Customer customer) {
        Email template = emailTemplate.customerAccountActivatedEmailTemplate(customer, emailFrom, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendResetPasswordEmailToCustomer(Customer customer, String token) {
        Email template = emailTemplate.customerAccountResetPasswordEmailTemplate(customer, token, emailFrom, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendRepairRequestToCustomer(Customer customer, Repair repair) {
        Email template = emailTemplate.customerRepairRequestEmailTemplate(customer, repair, emailFrom, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }
}
