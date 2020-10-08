package lt.ivl.webInternalApp.dto;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeRole;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class EmployeeDto {
    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String email;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String firstName;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String lastName;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String phone;

    @NotNull
    @NotEmpty(message = "Būtina pažymėti bent vieną rolę.")
    private Set<EmployeeRole> roles;

    public EmployeeDto() {
    }

    public EmployeeDto(Employee employee) {
        this.email = employee.getEmail();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.phone = employee.getPhone();
        this.roles = employee.getRoles();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<EmployeeRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<EmployeeRole> roles) {
        this.roles = roles;
    }
}
