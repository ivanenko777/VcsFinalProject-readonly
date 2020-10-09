package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.exception.CustomerExistsInDatabaseException;
import lt.ivl.components.service.CustomerService;
import lt.ivl.webInternalApp.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternalCustomerService {
    @Autowired
    private CustomerService componentCustomerService;

    public List<Customer> findAll() {
        return componentCustomerService.findAll();
    }

    public Customer createCustomer(CustomerDto customerDto) throws CustomerExistsInDatabaseException {
        String email = customerDto.getEmail();
        componentCustomerService.verifyIsCustomerExists(email);

        Customer customer = new Customer();
        customer = fillCustomerFields(customer, customerDto);
        return componentCustomerService.saveCustomer(customer);
    }

    public Customer updateCustomer(Customer customer, CustomerDto customerDto) throws CustomerExistsInDatabaseException {
        String email = customer.getEmail();
        String newEmail = customerDto.getEmail();
        if (!email.equals(newEmail)) {
            componentCustomerService.verifyIsCustomerExists(newEmail);
        }

        customer = fillCustomerFields(customer, customerDto);
        return componentCustomerService.saveCustomer(customer);
    }

    private Customer fillCustomerFields(Customer customer, CustomerDto customerDto) {
        customer.setEmail(customerDto.getEmail());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhone(customerDto.getPhone());
        return customer;
    }

}
