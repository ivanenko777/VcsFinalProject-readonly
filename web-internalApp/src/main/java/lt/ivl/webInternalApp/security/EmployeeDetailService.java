package lt.ivl.webInternalApp.security;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.exception.EmployeeNotFoundInDbException;
import lt.ivl.webInternalApp.service.InternalEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDetailService implements UserDetailsService {
    @Autowired
    private InternalEmployeeService internalEmployeeService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Employee employee = null;
        try {
            employee = internalEmployeeService.findEmployeeAccountByEmail(s);
        } catch (EmployeeNotFoundInDbException e) {
            throw new UsernameNotFoundException(s);
        }

        return new EmployeePrincipal(employee);
    }
}
