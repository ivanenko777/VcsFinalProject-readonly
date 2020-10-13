package lt.ivl.components.email;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.Repair;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplate {
    private Email email;

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Email customerAccountVerificationEmailTemplate(Customer customer, String token, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Jūs sėkmingai užsiregistravote. Paspauskite žemiau esančią nuorodą, kad patvirtintumėte registraciją.";
        final String confirmationUrl = appUrl + "activation?token=" + token;

        final String to = customer.getEmail();
        final String subject = "Registracijos patvirtinimas";
        final String message = message1 + " \r\n" + message2 + " \r\n" + confirmationUrl;
        return new Email(to, subject, message);
    }

    public Email customerAccountActivatedEmailTemplate(Customer customer, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Jūsų registracija patvirtinta. Paspauskite žemiau esančią nuorodą, kad prisijungtumėte.";
        final String loginUrl = appUrl + "login";

        final String to = customer.getEmail();
        final String subject = "Registracija patvirtinta";
        final String message = message1 + " \r\n" + message2 + " \r\n" + loginUrl;
        return new Email(to, subject, message);
    }

    public Email customerAccountResetPasswordEmailTemplate(Customer customer, String token, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Paspauskite žemiau esančią nuorodą, kad pakeisti slaptažodį.";
        final String resetPasswordUrl = appUrl + "reset-password?token=" + token;

        final String to = customer.getEmail();
        final String subject = "Slaptažodžio pakeitimas";
        final String message = message1 + " \r\n" + message2 + " \r\n" + resetPasswordUrl;
        return new Email(to, subject, message);
    }

    public Email customerRepairRequestEmailTemplate(Customer customer, Repair repair, String appUrl) {
        // remonto paraiska, STATUS -> PENDING
        final int repairId = repair.getId();

        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Remonto paraiška užregistruota. Sekite info žemiau esančioje nuorodoje.";
        final String repairViewUrl = appUrl + "repair/" + repairId + "/view";

        final String to = customer.getEmail();
        final String subject = "Užsakymo paraiška #" + repairId;
        final String message = message1 + " \r\n" + message2 + " \r\n" + repairViewUrl;
        return new Email(to, subject, message);
    }

    public Email customerRepairConfirmedEmailTemplate(Repair repair, String appUrl) {
        // remonto paraiska, STATUS -> PENDING
        final int repairId = repair.getId();
        final Customer customer = repair.getCustomer();

        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Remontas patvirtintas. Sekite info žemiau esančioje nuorodoje.";
        final String repairViewUrl = appUrl + "repair/" + repairId + "/view";

        final String to = customer.getEmail();
        final String subject = "Užsakymo patvirtinimas #" + repairId;
        final String message = message1 + " \r\n" + message2 + " \r\n" + repairViewUrl;
        return new Email(to, subject, message);
    }


    public Email customerRepairPaymentConfirmEmailTemplate(Repair repair, String technicianNote, String appUrl) {
        // remonto paraiska, STATUS -> PAYMENT_CONFIRM_WAITING
        final int repairId = repair.getId();
        final Customer customer = repair.getCustomer();

        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Reikalingas mokėjimo patvirtinimas. Daugiau info žemiau esančioje nuorodoje.";
        final String repairViewUrl = appUrl + "repair/" + repairId + "/payment";
        final String message3 = "Meistro komentaras:";

        final String to = customer.getEmail();
        final String subject = "Mokėjimo patvirtinimas #" + repairId;
        final String message = message1 + " \r\n" + message2 + " \r\n" + repairViewUrl + " \r\n\r\n" + message3 + " \r\n" + technicianNote;
        return new Email(to, subject, message);
    }

    public Email employeeAccountVerificationEmailTemplate(Employee employee, String token, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", employee.getFirstName(), employee.getLastName());
        final String message2 = "Paspauskite žemiau esančią nuorodą, kad aktyvuoti savo paskyrą ir pakeisti slaptažodį.";
        final String confirmationUrl = appUrl + "reset-password?token=" + token;

        final String to = employee.getEmail();
        final String subject = "Paskyros aktyvacija (darbuotojams)";
        final String message = message1 + " \r\n" + message2 + " \r\n" + confirmationUrl;
        return new Email(to, subject, message);
    }

    public Email employeeAccountActivatedEmailTemplate(Employee employee, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", employee.getFirstName(), employee.getLastName());
        final String message2 = "Jūsų registracija patvirtinta. Paspauskite žemiau esančią nuorodą, kad prisijungtumėte.";
        final String loginUrl = appUrl + "login";

        final String to = employee.getEmail();
        final String subject = "Paskyra aktyvuota (darbuotojams)";
        final String message = message1 + " \r\n" + message2 + " \r\n" + loginUrl;
        return new Email(to, subject, message);
    }

    public Email employeeAccountResetPasswordEmailTemplate(Employee employee, String token, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", employee.getFirstName(), employee.getLastName());
        final String message2 = "Paspauskite žemiau esančią nuorodą, kad pakeisti slaptažodį.";
        final String resetPasswordUrl = appUrl + "reset-password?token=" + token;

        final String to = employee.getEmail();
        final String subject = "Slaptažodžio pakeitimas (darbuotojams)";
        final String message = message1 + " \r\n" + message2 + " \r\n" + resetPasswordUrl;
        return new Email(to, subject, message);
    }
}
