package lt.ivl.webExternalApp.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CustomerDto {
    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String email;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String password;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String passwordVerify;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String firstName;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String lastName;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String phone;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordVerify() {
        return passwordVerify;
    }

    public void setPasswordVerify(String passwordVerify) {
        this.passwordVerify = passwordVerify;
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
