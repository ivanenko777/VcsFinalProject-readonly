package lt.ivl.webInternalApp.dto;

import lt.ivl.components.domain.Customer;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CustomerDto {
    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String firstName;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String lastName;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String email;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String phone;

    public CustomerDto() {
    }

    public CustomerDto(Customer customer) {
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.email = customer.getEmail();
        this.phone = customer.getPhone();
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
}
