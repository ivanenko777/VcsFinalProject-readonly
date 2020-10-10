package lt.ivl.webExternalApp.security;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.webExternalApp.service.ExternalCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailService implements UserDetailsService {
    @Autowired
    private ExternalCustomerService customerService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Customer customer = null;
        try {
            customer = customerService.findCustomerAccountByEmail(s);
        } catch (CustomerNotFoundInDBException e) {
            throw new UsernameNotFoundException(s);
        }
        return new CustomerPrincipal(customer);
    }
}
