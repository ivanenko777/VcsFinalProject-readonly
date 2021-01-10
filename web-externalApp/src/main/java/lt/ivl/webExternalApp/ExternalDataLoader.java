package lt.ivl.webExternalApp;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.CustomerVerificationToken;
import lt.ivl.components.exception.CustomerExistsInDatabaseException;
import lt.ivl.components.exception.PasswordDontMatchException;
import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.service.ExternalCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ExternalDataLoader implements ApplicationRunner {
    @Autowired
    private ExternalCustomerService externalCustomerService;

    @Override
    public void run(ApplicationArguments args) {
        CustomerDto customerDto1 = new CustomerDto();
        customerDto1.setEmail("k2@demo.lt");
        customerDto1.setFirstName("Klientas");
        customerDto1.setLastName("2 Ext");
        customerDto1.setPhone("1234567890");
        customerDto1.setPassword("a");
        customerDto1.setPasswordVerify("a");

        try {
            externalCustomerService.registerNewCustomerAccount(customerDto1);
        } catch (CustomerExistsInDatabaseException | PasswordDontMatchException e) {
            e.printStackTrace();
        }

        CustomerDto customerDto2 = new CustomerDto();
        customerDto2.setEmail("k3@demo.lt");
        customerDto2.setFirstName("Klientas");
        customerDto2.setLastName("3 Ext");
        customerDto2.setPhone("1234567890");
        customerDto2.setPassword("a");
        customerDto2.setPasswordVerify("a");
        try {
            Customer customer = externalCustomerService.registerNewCustomerAccount(customerDto2);
            CustomerVerificationToken verificationToken = externalCustomerService.createVerificationTokenForCustomerAccount(customer);
            externalCustomerService.activateCustomerAccount(verificationToken);
        } catch (CustomerExistsInDatabaseException | PasswordDontMatchException e) {
            e.printStackTrace();
        }
    }
}
