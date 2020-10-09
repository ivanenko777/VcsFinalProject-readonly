package lt.ivl.webInternalApp.service;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.service.CustomerService;
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
}
