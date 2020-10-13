package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Employee;
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

    @Value("${exApp.url}")
    private String externalAppUrl;

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
    public void sendAccountVerificationEmailToEmployee(Employee employee, String token) {
        Email template = mailTemplate.employeeAccountVerificationEmailTemplate(employee, token, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendAccountActivatedEmailToEmployee(Employee employee) {
        Email template = mailTemplate.employeeAccountActivatedEmailTemplate(employee, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendResetPasswordEmailToEmployee(Employee employee, String token) {
        Email template = mailTemplate.employeeAccountResetPasswordEmailTemplate(employee, token, appUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }

    @Async
    public void sendRepairConfirmedToCustomer(Repair repair) {
        Email template = mailTemplate.customerRepairConfirmedEmailTemplate(repair, externalAppUrl);
        SimpleMailMessage email = constructEmail(template);
        mailSender.send(email);
    }
}
