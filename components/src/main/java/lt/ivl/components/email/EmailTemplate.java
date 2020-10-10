package lt.ivl.components.email;

import lt.ivl.components.domain.Customer;
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

    public Email customerAccountVerificationEmailTemplate(Customer customer, String token, String from, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Jūs sėkmingai užsiregistravote. Paspauskite žemiau esančią nuorodą, kad patvirtintumėte registraciją.";
        final String confirmationUrl = appUrl + "activation?token=" + token;

        final String to = customer.getEmail();
        final String subject = "Registracijos patvirtinimas";
        final String message = message1 + " \r\n" + message2 + " \r\n" + confirmationUrl;
        return new Email(from, to, subject, message);
    }

    public Email customerAccountActivatedEmailTemplate(Customer customer, String from, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Jūsų registracija patvirtinta. Paspauskite žemiau esančią nuorodą, kad prisijungtumėte.";
        final String loginUrl = appUrl + "login";

        final String to = customer.getEmail();
        final String subject = "Registracija patvirtinta";
        final String message = message1 + " \r\n" + message2 + " \r\n" + loginUrl;
        return new Email(from, to, subject, message);
    }

    public Email customerAccountResetPasswordEmailTemplate(Customer customer, String token, String from, String appUrl) {
        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Paspauskite žemiau esančią nuorodą, kad pakeisti slaptažodį.";
        final String resetPasswordUrl = appUrl + "reset-password?token=" + token;

        final String to = customer.getEmail();
        final String subject = "Slaptažodžio pakeitimas";
        final String message = message1 + " \r\n" + message2 + " \r\n" + resetPasswordUrl;
        return new Email(from, to, subject, message);
    }

    public Email customerRepairRequestEmailTemplate(Customer customer, Repair repair, String from, String appUrl) {
        // remonto paraiska, STATUS -> PENDING
        final int repairId = repair.getId();

        final String message1 = String.format("Sveiki, %s %s,", customer.getFirstName(), customer.getLastName());
        final String message2 = "Remonto paraiška užregistruota. Sekite info žemiau esančioje nuorodoje.";
        final String repairViewUrl = appUrl + "repair/" + repairId + "/view";

        final String to = customer.getEmail();
        final String subject = "Užsakymo paraiška #" + repairId;
        final String message = message1 + " \r\n" + message2 + " \r\n" + repairViewUrl;
        return new Email(from, to, subject, message);
    }


}
