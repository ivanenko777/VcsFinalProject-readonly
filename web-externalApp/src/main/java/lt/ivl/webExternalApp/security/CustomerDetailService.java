package lt.ivl.webExternalApp.security;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailService implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(s);
        if (customer == null) throw new UsernameNotFoundException(s);

        return new CustomerPrincipal(customer);
    }
}
